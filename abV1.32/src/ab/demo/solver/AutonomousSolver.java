package ab.demo.solver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ab.demo.ClientNaiveAgent;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import flexjson.JSONSerializer;

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
		this.sensor = new Sensor(clientNaiveAgent);
		ClientNaiveAgent client = sensor.getClientNaiveAgent();
		try {
			this.state 	= sensor.checkEnviroment();
			Vision vision = sensor.getVision();
			this.states.add(state);
			Theory theory = new Theory();
			theory.addBeginState(state);
			
			List<Theory> teoriesEquals =  Theory.getEquals(theory, theories);
			if(!teoriesEquals.isEmpty()){
				// Ponderacion de teorias
				for (Theory t : teoriesEquals) { 
					t.incrementSucces();
					t.incUses();	
					t.use(vision, client);
				}
			}else{ 
				
				//Ponderamos y agregamos la teoria local
				if(!this.theories.contains(theory)){
					theory.incrementSucces();
					theory.incUses();
					this.theories.add(theory);
					theory.use(vision, client);
				}
			}			 
			State endState = sensor.checkEnviroment();
			theory.addEndState(endState);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return clientNaiveAgent.ar.checkState(); 
		
	}
	
	/*@Override
	public void save() {
		try {
			PrintWriter out = new PrintWriter("filename.json");
			JSONSerializer serializer = new JSONSerializer();
			String outS = "";
			for (Theory t : theories) {
				outS = serializer.include("action").serialize( t );
				out.println(outS);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        
	}*/
	
	@Override
	public void save() {
		try {
			File out = new File("fileJ.json");
			ObjectMapper mapper = new ObjectMapper();
			for (Theory t : theories) {
				mapper.writeValue(out, t);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

}
