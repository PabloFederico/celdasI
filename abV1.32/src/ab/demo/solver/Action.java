package ab.demo.solver;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ab.demo.ClientNaiveAgent;
import ab.vision.ABObject;
import ab.vision.Vision;
import ab.vision.GameStateExtractor.GameState;


public class Action {
	//private Point target;
	//private ArrayList<Point> trajectory;
	private int tapTime; //[1 - (0, 60 %) ]
	private Point target;
	private TargetType targetType;
	private Target serializableTarget;
	private int variateTarget = 5;
	private ArrayList<Point> trajectory = new ArrayList<Point>();
	private ArrayList<Target> serializableTrajectory = new ArrayList<Target>();
	
	private enum TargetType {
		PIG
	}
	
	public Action(){}
	
	public ArrayList<Target> getSerializableTrajectory() {
		return serializableTrajectory;
	}

	public void setSerializableTrajectory(ArrayList<Target> serializableTrajectory) {
		this.serializableTrajectory = serializableTrajectory;
	}

	private Point releasePoint;
	private Target serializableReleasePoint;
	

	public Target getSerializableReleasePoint() {
		return serializableReleasePoint;
	}

	public void setSerializableReleasePoint(Target serializableReleasePoint) {
		this.serializableReleasePoint = serializableReleasePoint;
	}

	public Target getSerializableTarget() {
		return serializableTarget;
	}

	public void setSerializableTarget(Target target) {
		this.serializableTarget = target;
	}

	
	public int getTapTime() {
		return tapTime;
	}
	
	public void setTapTime(int tapTime) {
		this.tapTime = tapTime;
	} 
	

	public void getDefaultTapTime(State state, Vision vision, ClientNaiveAgent clientNaiveAgent, Point target, Point releasePoint){
		//ClientNaiveAgent clientNaiveAgent = state.getClientNaiveAgent();
		Rectangle sling = vision.findSlingshotMBR();//state.getVision().findSlingshotMBR();
		
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
			
			tapTime = clientNaiveAgent.tp.getTapTime(sling, releasePoint, target, tapInterval);
			
		} 
		this.tapTime = tapTime;
		
	}
	
	public void getDefaultTrajectory(State state, Vision vision, ClientNaiveAgent clientNaiveAgent, Point target){
		//ClientNaiveAgent clientNaiveAgent = state.getClientNaiveAgent();
		Rectangle sling = vision.findSlingshotMBR(); //state.getVision().findSlingshotMBR();
		this.trajectory = null;
		if (sling == null || target == null)
			return;
		// estimate the trajectory
		ArrayList<Point> pts = clientNaiveAgent.tp.estimateLaunchPoint(sling, target);
		
		
		this.trajectory = pts;
	}
	
	private void getReleasePoint (State state, Vision vision, ClientNaiveAgent clientNaiveAgent, ArrayList<Point> pts) {
		this.releasePoint = null;
		if (pts == null){
			return;
		}
			
		// do a high shot when entering a level to find an accurate velocity
		if (clientNaiveAgent.firstShot && pts.size() > 1) {
			releasePoint = pts.get(1);
		} else 
			if (pts.size() == 1){
				releasePoint = pts.get(0);
			}
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
		
		return;
	}
	
	
	public void getDefaultTarget(State state, Vision vision, ClientNaiveAgent clientNaiveAgent){
		Point target = null;
		
		this.target = null;
		
		List<ABObject> pigs = vision.findPigsMBR();
		//ClientNaiveAgent clientNaiveAgent = state.getClientNaiveAgent();
		ABObject pig = null;
		
		if (pigs.isEmpty()) {
			return;
		}
		pig = pigs.get(clientNaiveAgent.randomGenerator.nextInt(pigs.size()));
		
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
		target = clientNaiveAgent.prevTarget;
		this.target = target;
	}
	
	private void selectTarget() {
		targetType = TargetType.PIG;
		
	}

	public State initAction(State state, Vision oldVision, ClientNaiveAgent clientNaiveAgent){
		Rectangle sling = oldVision.findSlingshotMBR();
		
		selectTarget();
		getDefaultTarget(state, oldVision, clientNaiveAgent);
		getDefaultTrajectory(state, oldVision, clientNaiveAgent, target);
		getReleasePoint(state, oldVision, clientNaiveAgent, trajectory);
		
		getDefaultTapTime(state, oldVision, clientNaiveAgent, target, releasePoint);
		
		this.serializableTarget = new Target(target);
		this.serializableReleasePoint = new Target(releasePoint);
		if (trajectory != null){
			for (Point pt : trajectory) {
				this.serializableTrajectory.add(new Target(pt));
			}
		}
		return determineConsequences (state, oldVision, clientNaiveAgent);

	}
	
	private State determineConsequences(State state, Vision vision, ClientNaiveAgent clientNaiveAgent) {
		
		State endState = new State(vision, clientNaiveAgent);
		//Check action
		if (endState.getPigsQuantity()-1 >= 0) {
			endState.setPigsQuantity(endState.getPigsQuantity()-1);
		}
		return endState;
	}

	public GameState defaultAction(State state, Vision oldVision, ClientNaiveAgent clientNaiveAgent){
		
		Rectangle sling = oldVision.findSlingshotMBR();
		Point refPoint  = clientNaiveAgent.tp.getReferencePoint(sling);
		
		// check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
		clientNaiveAgent.ar.fullyZoomOut();
		BufferedImage screenshot = clientNaiveAgent.ar.doScreenShot();
		Vision vision = new Vision(screenshot);
		GameState gState = clientNaiveAgent.ar.checkState();
		Rectangle _sling = vision.findSlingshotMBR();
		
		System.out.println("check release Points");
		if( releasePoint == null){
			System.err.println("No Release Point Found");
			return clientNaiveAgent.ar.checkState();
		}
		if(_sling != null)
		{
			//System.out.println("hay _sling");
			double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
			if(scale_diff < 25)
			{
				//System.out.println("dif scala");
				int dx = (int) releasePoint.getX() - refPoint.x;
				int dy = (int) releasePoint.getY() - refPoint.y;
				//System.out.println("dx dy");
				if(dx < 0)
				{
					long timer = System.currentTimeMillis();
					clientNaiveAgent.ar.shoot(refPoint.x, refPoint.y, dx, dy, 0, tapTime, false);
					System.out.println("It takes " + (System.currentTimeMillis() - timer) + " ms to take a shot");
					
					gState = clientNaiveAgent.ar.checkState();
					if ( gState == GameState.PLAYING )
					{
						screenshot = clientNaiveAgent.ar.doScreenShot();
						vision = new Vision(screenshot);
						List<Point> traj = vision.findTrajPoints();
						clientNaiveAgent.tp.adjustTrajectory(traj, sling, releasePoint);
						clientNaiveAgent.firstShot = false;
					}
				}
			}
			else{
				System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
			}
		}
		return gState;
	}
	
	public GameState exec(State beginState, Vision vision, ClientNaiveAgent client) {
		
		return this.defaultAction(beginState, vision, client);
		
	}

	public boolean isEqual(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	public void convert() {
		this.target = new Point();
		if (this.serializableTarget == null){
			System.out.println("Target en null");
		}
		
		
		this.target.x = this.serializableTarget.getX();
		this.target.y = this.serializableTarget.getY();
		
		for (Target tg : this.serializableTrajectory) {
			Point p = new Point();
			p.x = tg.getX();
			p.y = tg.getY();
			this.trajectory.add(p);
		}
		this.releasePoint = new Point();
		this.releasePoint.x = this.serializableReleasePoint.getX();
		this.releasePoint.y = this.serializableReleasePoint.getY();
	}

	public void variate() {
		this.serializableTarget.setX(this.serializableTarget.getX()+ this.variateTarget) ;
		this.serializableTarget.setY(this.serializableTarget.getY()+ this.variateTarget) ;
		this.variateTarget = this.variateTarget * (-1);
	}
	

}

