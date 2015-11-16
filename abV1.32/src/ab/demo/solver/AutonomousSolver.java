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
	public Set<Teory> teories = null;	
	
	public AutonomousSolver(){
		states = new ArrayList<State>();
		this.teories = new HashSet<Teory>();	
	}
	@Override
	public GameState solve(ClientNaiveAgent clientNaiveAgent) {
		// TODO Auto-generated method stub
		this.sensor = new Sensor(clientNaiveAgent);
		try {
			this.state 	= sensor.checkEnviroment();
			this.states.add(state);
			Teory teory = new Teory();
			teory.addBeginState(state);
			
			List<Teory> teoriesEquals =  Teory.getEquals(teory, teories);
			if(!teoriesEquals.isEmpty()){
				// Ponderacion de teorias
				for (Teory t : teoriesEquals) { 
					t.incrementSucces();
					t.incUses();	
					t.use();
				}
			}else{ 
				
				//Ponderamos y agregamos la teoria local
				if(!this.teories.contains(teory)){
					teory.incrementSucces();
					teory.incUses();
					this.teories.add(teory);
				}
			}			 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null; 
		
	}

}
