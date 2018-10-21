package gui;

import agents.Board;
import agents.Fascist;
import agents.Hitler;
import agents.Liberal;
import jade.core.AID;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utitilites.Utilities;

public class Main {
	
	private static ContainerController container;
	
	private static int numberPlayers = 7;

	public static void main(String[] args) throws StaleProxyException {
		
		int fascists = (int) Math.ceil(numberPlayers*0.4);

		Utilities.players = new AID[numberPlayers];
				
		container = Runtime.instance().createMainContainer(new ProfileImpl());
		
		for (int i = 0; i < fascists-1; i++) {
			Fascist f = new Fascist();
			AgentController agent = container.acceptNewAgent("Player_" + i, f);
			Utilities.players[i] = f.getAID();	
			agent.start();
		}
		
		Hitler h = new Hitler();
		AgentController agent = container.acceptNewAgent("Player_" + (fascists-1) , h);
		Utilities.players[fascists-1] = h.getAID();	
		agent.start();
		
		for (int i = fascists; i < numberPlayers; i++) {
			Liberal l = new Liberal();
			AgentController a = container.acceptNewAgent("Player_" + i, l);
			Utilities.players[i] = l.getAID();	
			a.start();
		}
		Board b = new Board();
		AgentController board = container.acceptNewAgent("Board", b);
		board.start();	
	}

}
