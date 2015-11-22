package ab.demo.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class Teory {

	private State beginState;
	private State endState;
	private Action action;
	private int successNumber;
	private int useNumber;
	
	public State getBeginState() {
		return beginState;
	}

	public Action getAction() {
		return action;
	}


	public void setAction(Action action) {
		this.action = action;
	}

	public void setBeginState(State beginState) {
		this.beginState = beginState;
	}



	public void addBeginState(State state) {
		this.beginState = state;
		
	}
	
	
	public static List<Teory> getEquals(Teory toCompare, Set<Teory> persistenTeories){
		   List<Teory> similarTeories = new ArrayList<Teory>();
		   for (Teory obj : persistenTeories) {
			   if (toCompare.equals(obj) && obj.isSuccessful()) similarTeories.add(obj);
		   }
		   
		  return similarTeories;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Teory other = (Teory) obj;
		
		
		return this.beginState == other.getBeginState();
	}
	
	private boolean isSuccessful() {
		return this.successNumber == this.useNumber;
	}


	public void incrementSucces() {
		this.successNumber++;	
	}


	public void incUses() {
		this.useNumber++;
	}


	public void use() {
		this.action = new Action();
		this.action.exec(beginState);
	}


}
