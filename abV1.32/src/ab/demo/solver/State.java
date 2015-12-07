package ab.demo.solver;

import java.util.List;

import ab.demo.ClientNaiveAgent;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class State {

	//TODO eliminate pigs
	//private List<ABObject> pigs;
	Integer pigsQuantity = 0;

	private GameState state;

	public State() {}
	
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
		setPigsQuantity(vision.findPigsMBR().size());
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
		
		return this.pigsQuantity == other.getPigsQuantity();
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
		return this.pigsQuantity <= beginState.getPigsQuantity();
	}

}
