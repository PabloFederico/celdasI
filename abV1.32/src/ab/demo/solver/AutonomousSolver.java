package ab.demo.solver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
			Theory maxTeory = null;
			if(!teoriesEquals.isEmpty()){
				// SI hay muchas similares agarramos la de mayor ponderacion
				System.out.println("Hay teorias iguales");
				float maxRange = 0;
				for (Theory t : teoriesEquals) { 
					float range = t.getSuccessNumber() /  t.getUseNumber();
					if (range > maxRange){
						maxRange = range;
						theory = t;
					}	
				}
				theory.incUses();	
				theory.use(vision, client);
			}else{ 
				
				//Ponderamos y agregamos la teoria local
				if(!this.theories.contains(theory)){
					System.out.println("Not Contains theory");
					this.theories.add(theory);	
				}else{
					System.out.println("theory usasada");
				}
				theory.incUses();
				theory.use(vision, client);
			}	
			
			State endState = sensor.checkEnviroment();
			
			if (endState.pigsQuantity < this.state.pigsQuantity){
				theory.incrementSucces();
			}
			
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
			File out = new File(path);
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			Path file = Paths.get(path);
			List <String> line = Arrays.asList("Number of theories: " + theories.size());
			Files.write(file, line, Charset.forName("UTF-8"));
			/*for (Theory t : theories) {
				mapper.writeValue(out, t);
			}*/
			mapper.writeValue(out, theories);
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
	@Override
	public void load() {
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			//TODO Replace with logger
			System.out.println("Couldn't load theory configuration");
			return;
		}
		try {
			theories = new ObjectMapper().readValue(file, new TypeReference<Set<Theory>>() {});
		} catch (JsonParseException e) {
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
