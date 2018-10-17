package gui;

import agents.Fascist;
import agents.Hitler;
import agents.Liberal;
import jade.core.AID;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
	
	private static ContainerController container;
	
	private static int numberPlayers = 7;

	public static void main(String[] args) throws StaleProxyException {
		
		int fascists = (int) Math.ceil(numberPlayers*0.4);

		
		AID[] players = new AID[numberPlayers];
		
		
		
		container = Runtime.instance().createMainContainer(new ProfileImpl());
		
		for (int i = 0; i < fascists; i++) {
			Fascist f = new Fascist();
			AgentController agent = container.acceptNewAgent("Player_" + i, f);
			players[i] = f.getAID();	
			agent.start();
		}
		
		for (int i = fascists; i < numberPlayers - 1; i++) {
			Liberal l = new Liberal();
			AgentController agent = container.acceptNewAgent("Player_" + i, l);
			players[i] = l.getAID();	
			agent.start();
		}
		
		Hitler h = new Hitler();
		AgentController agent = container.acceptNewAgent("Player_" + (numberPlayers - 1) , h);
		players[numberPlayers-1] = h.getAID();	
		agent.start();
		
		
		AgentController board = container.createNewAgent("Board", "agents.Board", players);
		board.start();
		
		
	}

}
