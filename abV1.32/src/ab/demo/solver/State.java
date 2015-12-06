package ab.demo.solver;

import java.util.List;

import ab.demo.ClientNaiveAgent;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class State {

	//TODO needs to remove vision from the class
	//private Vision vision;
	/*public Vision getVision() {
		return vision;
	}*/

	/*public void setVision(Vision vision) {
		this.vision = vision;
	}*/

	/*public ClientNaiveAgent getClientNaiveAgent() {
		return clientNaiveAgent;
	}*/

	/*public void setClientNaiveAgent(ClientNaiveAgent clientNaiveAgent) {
		this.clientNaiveAgent = clientNaiveAgent;
	}*/

	private List<ABObject> pigs;
	//TODO needs to remove this
	//private ClientNaiveAgent clientNaiveAgent;
	private GameState state;

	public State(Vision vision, ClientNaiveAgent clientNaiveAgent) {
		//this.clientNaiveAgent 	= clientNaiveAgent;
		this.disassembleVision(vision, clientNaiveAgent);
		
	}
	
	private void disassembleVision(Vision vision, ClientNaiveAgent clientNaiveAgent){
		setPigs(vision.findPigsMBR());
		state = clientNaiveAgent.ar.checkState();
	}

	public List<ABObject> getPigs() {
		return pigs;
	}

	public void setPigs(List<ABObject> pigs) {
		this.pigs = pigs;
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
		
		return this.pigs.size() == other.getPigs().size();
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

}
