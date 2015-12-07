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

public abstract class Solver{
	
	protected String path = "Theories.json";
	
	public abstract GameState solve(ClientNaiveAgent clientNaiveAgent);
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public abstract void save();
	
	public abstract void load();

}