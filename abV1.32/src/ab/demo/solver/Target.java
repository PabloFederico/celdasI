package ab.demo.solver;

import java.awt.Point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class Target {
	private int x = 0;
	private int y = 0;
	private int RADIO = 10;
	private int ERROR_RADIO = 4;
	
	
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
	
	public Target (){}
	
	public Target (Point point){
		if (point == null)
			return;
		this.x = point.x;
		this.y = point.y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Target otherPosition = (Target) obj;
		
		int x = this.x - otherPosition.getX();
		int y = this.y - otherPosition.getY();
	
		return (x >= -ERROR_RADIO && x <= ERROR_RADIO) && (y >= -ERROR_RADIO && y <= ERROR_RADIO);
	}
	
	// Se considera similar si esta en un radio de 10 px 
	public boolean isSimilar(Target otherPosition) {
		
		int x = this.x - otherPosition.getX();
		int y = this.y - otherPosition.getY();
		
		return (x >= -RADIO && x <= RADIO) && (y >= -RADIO && y <= RADIO);
	}
	
}
