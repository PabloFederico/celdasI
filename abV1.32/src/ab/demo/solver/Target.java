package ab.demo.solver;

import java.awt.Point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Target {
	private int x = 0;
	private int y = 0;
	
	private Point point;
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Target (Point point){
		if (point == null)
			return;
		this.x = point.x;
		this.y = point.y;
		this.point = point;
	}
}
