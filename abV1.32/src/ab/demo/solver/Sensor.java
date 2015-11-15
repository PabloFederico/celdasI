package ab.demo.solver;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

import ab.demo.ClientNaiveAgent;
import ab.vision.Vision;
import ab.vision.GameStateExtractor.GameState;

public class Sensor {

		private ClientNaiveAgent clientNaiveAgent; 
		private static final Logger log = Logger.getLogger( Sensor.class.getName() );
		
		public Sensor(ClientNaiveAgent clientNaiveAgent){
			this.clientNaiveAgent = clientNaiveAgent;
		}
		
		public State checkEnviroment() throws IOException{
			
			BufferedImage screenshot = clientNaiveAgent.ar.doScreenShot();

			// process image
			Vision vision = new Vision(screenshot);
			
			Rectangle sling = vision.findSlingshotMBR();

			//If the level is loaded (in PLAYING　state)but no slingshot detected, then the agent will request to fully zoom out.
			while (sling == null && clientNaiveAgent.ar.checkState() == GameState.PLAYING) {
				log.warning("no slingshot detected. Please remove pop up or zoom out");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				clientNaiveAgent.ar.fullyZoomOut();
				screenshot = clientNaiveAgent.ar.doScreenShot();
				vision = new Vision(screenshot);
				sling = vision.findSlingshotMBR();
			}
			
			return new State(vision, clientNaiveAgent);
			
		}


}
