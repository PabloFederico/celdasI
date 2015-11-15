package ab.demo.solver;

import java.awt.Point;
import java.util.ArrayList;

public class Action {
	private Point target;
	private ArrayList<Point> trajectory;
	private int tapTime;
	
	public Point getTarget() {
		return target;
	}
	
	public void setTarget(Point target) {
		this.target = target;
	}
	
	public ArrayList<Point> getTrajectory() {
		return trajectory;
	}
	
	public void setTrajectory(ArrayList<Point> trajectory) {
		this.trajectory = trajectory;
	}
	
	public int getTapTime() {
		return tapTime;
	}
	
	public void setTapTime(int tapTime) {
		this.tapTime = tapTime;
	} 
}
