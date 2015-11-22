package ab.demo.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ab.demo.ClientNaiveAgent;
import ab.vision.GameStateExtractor.GameState;

public class AutonomousSolver extends Solver{

	private Sensor sensor;
	private State state;
	private List<State> states = null;
	public Set<Theory> theories = null;	
	
	public AutonomousSolver(){
		states = new ArrayList<State>();
		this.theories = new HashSet<Theory>();	
	}
	@Override
	public GameState solve(ClientNaiveAgent clientNaiveAgent) {
		// TODO Auto-generated method stub
		this.sensor = new Sensor(clientNaiveAgent);
		try {
			this.state 	= sensor.checkEnviroment();
			this.states.add(state);
			Theory theory = new Theory();
			theory.addBeginState(state);
			
			List<Theory> teoriesEquals =  Theory.getEquals(theory, theories);
			if(!teoriesEquals.isEmpty()){
				// Ponderacion de teorias
				for (Theory t : teoriesEquals) { 
					t.incrementSucces();
					t.incUses();	
					t.use();
					System.out.println("Loop");
				}
			}else{ 
				
				//Ponderamos y agregamos la teoria local
				if(!this.theories.contains(theory)){
					theory.incrementSucces();
					theory.incUses();
					this.theories.add(theory);
					theory.use();
				}
			}			 
			State endState = sensor.checkEnviroment();
			theory.addEndState(endState);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return clientNaiveAgent.ar.checkState(); 
		
	}

}
