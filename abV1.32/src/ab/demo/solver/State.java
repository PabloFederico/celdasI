package ab.demo.solver;

import java.util.List;

import ab.demo.ClientNaiveAgent;
import ab.vision.ABObject;
import ab.vision.Vision;
import ab.vision.GameStateExtractor.GameState;

public class State {

	private Vision vision;
	private List<ABObject> pigs;
	private ClientNaiveAgent clientNaiveAgent;
	private GameState state;

	public State(Vision vision, ClientNaiveAgent clientNaiveAgent) {
		this.vision = vision;
		this.clientNaiveAgent = clientNaiveAgent;
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

}
