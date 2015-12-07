package ab.demo.solver;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ab.demo.ClientNaiveAgent;
import ab.demo.other.ClientActionRobot;
import ab.demo.other.ClientActionRobotJava;
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class DefaultSolver extends Solver{


	@Override
	public GameState solve(ClientNaiveAgent clientNaiveAgent) {
		// TODO Auto-generated method stub
		// capture Image
		BufferedImage screenshot = clientNaiveAgent.ar.doScreenShot();

		// process image
		Vision vision = new Vision(screenshot);
		
		Rectangle sling = vision.findSlingshotMBR();

		//If the level is loaded (in PLAYING　state)but no slingshot detected, then the agent will request to fully zoom out.
		while (sling == null && clientNaiveAgent.ar.checkState() == GameState.PLAYING) {
			System.out.println("no slingshot detected. Please remove pop up or zoom out");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			clientNaiveAgent.ar.fullyZoomOut();
			screenshot = clientNaiveAgent.ar.doScreenShot();
			vision = new Vision(screenshot);
			sling = vision.findSlingshotMBR();
		}

		// Hasta aca se senso. AHora arranca un nuevo estado
		 // get all the pigs
 		List<ABObject> pigs = vision.findPigsMBR();
 		
		GameState state = clientNaiveAgent.ar.checkState();
		// if there is a sling, then play, otherwise skip.
		if (sling != null) {
			
			
			//If there are pigs, we pick up a pig randomly and shoot it. 
			if (!pigs.isEmpty()) {		
				Point releasePoint = null;
				// random pick up a pig
					ABObject pig = pigs.get(clientNaiveAgent.randomGenerator.nextInt(pigs.size()));
					
					Point _tpt = pig.getCenter();

					
					// if the target is very close to before, randomly choose a
					// point near it
					if (clientNaiveAgent.prevTarget != null && clientNaiveAgent.distance(clientNaiveAgent.prevTarget, _tpt) < 10) {
						double _angle = clientNaiveAgent.randomGenerator.nextDouble() * Math.PI * 2;
						_tpt.x = _tpt.x + (int) (Math.cos(_angle) * 10);
						_tpt.y = _tpt.y + (int) (Math.sin(_angle) * 10);
						System.out.println("Randomly changing to " + _tpt);
					}

					clientNaiveAgent.prevTarget = new Point(_tpt.x, _tpt.y);

					// estimate the trajectory
					ArrayList<Point> pts = clientNaiveAgent.tp.estimateLaunchPoint(sling, _tpt);

					// do a high shot when entering a level to find an accurate velocity
					if (clientNaiveAgent.firstShot && pts.size() > 1) {
						releasePoint = pts.get(1);
					} else 
						if (pts.size() == 1)
							releasePoint = pts.get(0);
						else 
							if(pts.size() == 2)
							{
								// System.out.println("first shot " + clientNaiveAgent.firstShot);
								// randomly choose between the trajectories, with a 1 in
								// 6 chance of choosing the high one
								if (clientNaiveAgent.randomGenerator.nextInt(6) == 0)
									releasePoint = pts.get(1);
								else
								releasePoint = pts.get(0);
							}
							Point refPoint = clientNaiveAgent.tp.getReferencePoint(sling);

					// Get the release point from the trajectory prediction module
					int tapTime = 0;
					if (releasePoint != null) {
						double releaseAngle = clientNaiveAgent.tp.getReleaseAngle(sling,
								releasePoint);
						System.out.println("Release Point: " + releasePoint);
						System.out.println("Release Angle: "
								+ Math.toDegrees(releaseAngle));
						int tapInterval = 0;
						switch (clientNaiveAgent.ar.getBirdTypeOnSling()) 
						{

							case RedBird:
								tapInterval = 0; break;               // start of trajectory
							case YellowBird:
								tapInterval = 65 + clientNaiveAgent.randomGenerator.nextInt(25);break; // 65-90% of the way
							case WhiteBird:
								tapInterval =  50 + clientNaiveAgent.randomGenerator.nextInt(20);break; // 50-70% of the way
							case BlackBird:
								tapInterval =  0;break; // 70-90% of the way
							case BlueBird:
								tapInterval =  65 + clientNaiveAgent.randomGenerator.nextInt(20);break; // 65-85% of the way
							default:
								tapInterval =  60;
						}
						
						tapTime = clientNaiveAgent.tp.getTapTime(sling, releasePoint, _tpt, tapInterval);
						
					} else
						{
							System.err.println("No Release Point Found");
							return clientNaiveAgent.ar.checkState();
						}
				
				
					// check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
					clientNaiveAgent.ar.fullyZoomOut();
					screenshot = clientNaiveAgent.ar.doScreenShot();
					vision = new Vision(screenshot);
					Rectangle _sling = vision.findSlingshotMBR();
					if(_sling != null)
					{
						double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
						if(scale_diff < 25)
						{
							int dx = (int) releasePoint.getX() - refPoint.x;
							int dy = (int) releasePoint.getY() - refPoint.y;
							if(dx < 0)
							{
								long timer = System.currentTimeMillis();
								clientNaiveAgent.ar.shoot(refPoint.x, refPoint.y, dx, dy, 0, tapTime, false);
								System.out.println("It takes " + (System.currentTimeMillis() - timer) + " ms to take a shot");
								state = clientNaiveAgent.ar.checkState();
								if ( state == GameState.PLAYING )
								{
									screenshot = clientNaiveAgent.ar.doScreenShot();
									vision = new Vision(screenshot);
									List<Point> traj = vision.findTrajPoints();
									clientNaiveAgent.tp.adjustTrajectory(traj, sling, releasePoint);
									clientNaiveAgent.firstShot = false;
								}
							}
						}
						else
							System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
					}
					else
						System.out.println("no sling detected, can not execute the shot, will re-segement the image");
				
			}
		}
		return state;
	}

	@Override
	public void save() {
	}

	@Override
	public void load() {
		
	}


}