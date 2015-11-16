package ab.demo.solver;

import java.util.List;

import ab.demo.ClientNaiveAgent;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;

public class State {

	private Vision vision;
	public Vision getVision() {
		return vision;
	}

	public void setVision(Vision vision) {
		this.vision = vision;
	}

	public ClientNaiveAgent getClientNaiveAgent() {
		return clientNaiveAgent;
	}

	public void setClientNaiveAgent(ClientNaiveAgent clientNaiveAgent) {
		this.clientNaiveAgent = clientNaiveAgent;
	}

	private List<ABObject> pigs;
	private ClientNaiveAgent clientNaiveAgent;
	private GameState state;

	public State(Vision vision, ClientNaiveAgent clientNaiveAgent) {
		this.vision 			= vision;
		this.clientNaiveAgent 	= clientNaiveAgent;
		this.disassembleVision();
		
	}
	
	private void disassembleVision(){
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

}
