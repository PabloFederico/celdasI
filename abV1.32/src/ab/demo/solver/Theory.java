package ab.demo.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ab.demo.ClientNaiveAgent;
import ab.vision.Vision;

public class Theory {
	
	private transient State beginState;
	private transient State endState;
	private transient Action action;
	private int successNumber;
	private int useNumber;
	
	public Theory() {
		successNumber = 0;
		setUseNumber(0);
	}

	public static List<Theory> getEquals(Theory toCompare, Set<Theory> persistenTheories){
		   List<Theory> similarTeories = new ArrayList<Theory>();
		   for (Theory obj : persistenTheories) {
			   if (toCompare.equals(obj)) {
				   similarTeories.add(obj);
			   }
		   }
		   
		  return similarTeories;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Theory other = (Theory) obj;
		
		
		return this.beginState.equals(other.getBeginState());
	}
	
	private boolean isSuccessful() {
		return this.successNumber == this.useNumber;
	}
	
	public static List<Theory> getSimilars(Theory theory, Set<Theory> theories) {
		List<Theory> similarTheories = new ArrayList<Theory>();
		
		for (Theory t : theories) {
			if (theory.isSimilar(t)) {
				similarTheories.add(t);
			}
		}
		return similarTheories;
	}

	public void addBeginState(State state) {
		this.beginState = state;
	}

	public void incrementSucces() {
		successNumber++;
	}

	public void incUses() {
		setUseNumber(getUseNumber() + 1);
	}

	public void use(Vision vision, ClientNaiveAgent client) {
		if (this.action != null){
			System.out.println("Accion conocida");
			this.action.convert();
		}
		else{
			System.out.println("Accion Nueva");
			this.action = new Action();
			this.action.initAction(beginState, vision, client);
		}
		this.action.exec(beginState, vision, client);
	}

	public void addEndState(State endState) {
		this.endState = endState;
	}

	public int getSuccessNumber() {
		return successNumber;
	}

	public void setSuccessNumber(int successNumber) {
		this.successNumber = successNumber;
	}

	public int getUseNumber() {
		return useNumber;
	}

	public void setUseNumber(int useNumber) {
		this.useNumber = useNumber;
	}
	
	protected Boolean isEqual(Theory theory){
		
		if ((beginState.isEqual(theory.getBeginState()))&&
				(action.isEqual(theory.getAction()))&&
				(endState.isEqual(theory.getEndState()))) {
			return true;			
		}
		
		return false;
		
	}
	
	public State getBeginState() {
		return beginState;
	}

	public State getEndState() {
		return endState;
	}

	public Action getAction() {
		return action;
	}

	protected boolean isSimilar(Theory theory){
		return beginState.isSimilar(theory.getBeginState());
		
	}
	
	@Override
	public String toString() {
		
		return "Theory [successNumber="+this.successNumber+"]";
		/*return "Theory [beginState="+this.beginState+", endState="+this.endState+
				", action="+this.action+", successNumber="+this.successNumber+
				", useNumber="+this.useNumber+"]";*/
	}

}
