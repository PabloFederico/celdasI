package ab.demo.solver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ab.demo.ClientNaiveAgent;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class State {

	//TODO eliminate pigs
	private List<ABObject> pigs;
	private List<Target> pigsPosition = new ArrayList<Target>();


	Integer pigsQuantity = 0;

	private GameState state;

	public State() {}
	
	public List<Target> getPigsPosition() {
		return pigsPosition;
	}

	public void setPigsPosition(List<Target> pigsPosition) {
		this.pigsPosition = pigsPosition;
	}
	
	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public State(Vision vision, ClientNaiveAgent clientNaiveAgent) {
		//this.clientNaiveAgent 	= clientNaiveAgent;
		this.disassembleVision(vision, clientNaiveAgent);
		
	}
	
	private void disassembleVision(Vision vision, ClientNaiveAgent clientNaiveAgent){
		this.pigs = vision.findPigsMBR();
		for (ABObject pig : pigs) {
			Target position = new Target(pig.getCenter());
			pigsPosition.add(position);
		}
		setPigsQuantity(pigs.size());
		state = clientNaiveAgent.ar.checkState();
	}
	

	/*public List<ABObject> getPigs() {
		return pigs;
	}

	public void setPigs(List<ABObject> pigs) {
		this.pigs = pigs;
	}*/
	
	public Integer getPigsQuantity() {
		return pigsQuantity;
	}

	public void setPigsQuantity(Integer pigsQuantity) {
		this.pigsQuantity = pigsQuantity;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		boolean equalsQuantity = (this.pigsQuantity == other.getPigsQuantity());
		if (!equalsQuantity)
			return false;
		int i = 0;
		for (Target pigPosition : pigsPosition) {
			Target otherPosition = other.getPigsPosition().get(i);
			if (! pigPosition.equals(otherPosition))
				return false;
			i++;
		}
		return true;
	}

	public boolean isEqual(State beginState) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//TODO:Needs to simplify because vision and pigs has a lot of complex classes
	@Override
	public String toString() {
		return "State []";
	}


	public boolean isSimilar(State beginState) {
		if (this.pigsQuantity != beginState.getPigsQuantity())
			return false;
		int i = 0;
		for (Target pigPosition : pigsPosition) {
			Target otherPosition = beginState.getPigsPosition().get(i);
			if (! pigPosition.isSimilar(otherPosition))
				return false;
			i++;
		}			
		return true;
	}

}
