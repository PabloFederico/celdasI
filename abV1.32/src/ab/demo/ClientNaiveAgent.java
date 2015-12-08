/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
**This work is licensed under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
**To view a copy of this license, visit http://www.gnu.org/licenses/
 *****************************************************************************/
package ab.demo;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ab.demo.other.ClientActionRobot;
import ab.demo.other.ClientActionRobotJava;
import ab.demo.solver.AutonomousSolver;
import ab.demo.solver.Solver;
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
//Naive agent (server/client version)

public class ClientNaiveAgent implements Runnable {


	//Wrapper of the communicating messages
	public ClientActionRobotJava ar;
	public byte currentLevel = -1;
	public int failedCounter = 0;
	public int[] solved;
	public TrajectoryPlanner tp; 
	public int id = 28888;
	public boolean firstShot;
	public Point prevTarget;
	public Random randomGenerator;

	private Solver solver = new AutonomousSolver();
	
	public Solver getSolver() {
		return this.solver;
	}
	
	/**
	 * Constructor using the default IP
	 * */
	public ClientNaiveAgent() {
		// the default ip is the localhost
		ar = new ClientActionRobotJava("127.0.0.1");
		tp = new TrajectoryPlanner();
		randomGenerator = new Random();
		prevTarget = null;
		firstShot = true;

	}
	/**
	 * Constructor with a specified IP
	 * */
	public ClientNaiveAgent(String ip) {
		ar = new ClientActionRobotJava(ip);
		tp = new TrajectoryPlanner();
		randomGenerator = new Random();
		prevTarget = null;
		firstShot = true;

	}
	public ClientNaiveAgent(String ip, int id)
	{
		ar = new ClientActionRobotJava(ip);
		tp = new TrajectoryPlanner();
		randomGenerator = new Random();
		prevTarget = null;
		firstShot = true;
		this.id = id;
	}
	public int getNextLevel()
	{
		int level = 0;
		boolean unsolved = false;
		//all the level have been solved, then get the first unsolved level
		for (int i = 0; i < solved.length; i++)
		{
			if(solved[i] == 0 )
			{
					unsolved = true;
					level = i + 1;
					if(level <= currentLevel && currentLevel < solved.length)
						continue;
					else
						return level;
			}
		}
		if(unsolved)
			return level;
	    level = (currentLevel + 1)%solved.length;
		if(level == 0)
			level = solved.length;
		return level; 
	}
	
	
	public int checkMyScore(int level){
		int[] scores = ar.checkMyScore();
		return scores[level];
		
	}
    /* 
     * Run the Client (Naive Agent)
     */
	private void checkMyScore()
	{
		
		int[] scores = ar.checkMyScore();
		System.out.println(" My score: ");
		int level = 1;
		for(int i: scores)
		{
			System.out.println(" level " + level + "  " + i);
			if (i > 0)
				solved[level - 1] = 1;
			level ++;
		}
	}
	public void run() {	
		byte[] info = ar.configure(ClientActionRobot.intToByteArray(id));
		solved = new int[info[2]];
		
		//load the initial level (default 1)
		//Check my score
		checkMyScore();
		
		currentLevel = (byte)getNextLevel(); 
		ar.loadLevel(currentLevel);
		//ar.loadLevel((byte)9);
		GameState state;
		System.out.println("Loading...");
		this.getSolver().load();
		System.out.println("Theories Loaded");
		while (true) {
			
			state = solve();
			//If the level is solved , go to the next level
			if (state == GameState.WON) {
							
				///System.out.println(" loading the level " + (currentLevel + 1) );
				checkMyScore();
				System.out.println();
				currentLevel = (byte)getNextLevel(); 
				ar.loadLevel(currentLevel);
				//ar.loadLevel((byte)9);
				//display the global best scores
				int[] scores = ar.checkScore();
				System.out.println("Global best score: ");
				System.out.println("Saving...");
				this.getSolver().save();
				System.out.println("Theories Saved");
				for (int i = 0; i < scores.length ; i ++)
				{
				
					System.out.print( " level " + (i+1) + ": " + scores[i]);
				}
				System.out.println();
				
				// make a new trajectory planner whenever a new level is entered
				tp = new TrajectoryPlanner();

				// first shot on this level, try high shot first
				firstShot = true;
				
			} else 
				//If lost, then restart the level
				if (state == GameState.LOST) {
				failedCounter++;
				if(failedCounter > 3)
				{
					failedCounter = 0;
					currentLevel = (byte)getNextLevel(); 
					ar.loadLevel(currentLevel);
					
					//ar.loadLevel((byte)9);
				}
				else
				{		
					System.out.println("restart");
					ar.restartLevel();
				}
						
			} else 
				if (state == GameState.LEVEL_SELECTION) {
				System.out.println("unexpected level selection page, go to the last current level : "
								+ currentLevel);
				ar.loadLevel(currentLevel);
			} else if (state == GameState.MAIN_MENU) {
				System.out
						.println("unexpected main menu page, reload the level : "
								+ currentLevel);
				ar.loadLevel(currentLevel);
			} else if (state == GameState.EPISODE_MENU) {
				System.out.println("unexpected episode menu page, reload the level: "
								+ currentLevel);
				ar.loadLevel(currentLevel);
			}

		}

	}


	  /** 
	   * Solve a particular level by shooting birds directly to pigs
	   * @return GameState: the game state after shots.
     */
	public GameState solve()

	{
		//Solver solver = new DefaultSolver();
		//Solver solver = new AutonomousSolver();
		return solver.solve(this);
	}

	public double distance(Point p1, Point p2) {
		return Math.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)* (p1.y - p2.y)));
	}

	public static void main(String args[]) {

		ClientNaiveAgent na;
		if(args.length > 0)
			na = new ClientNaiveAgent(args[0]);
		else
			na = new ClientNaiveAgent();
		na.run();
		
	}
}
