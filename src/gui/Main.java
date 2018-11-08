package gui;

import agents.Board;
import agents.Fascist;
import agents.Hitler;
import agents.Liberal;
import agents.Player;
import jade.core.AID;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utilities.Utilities;

public class Main {
	
	private static ContainerController container;
	
	public static void main(String[] args) throws StaleProxyException {
		
		AID[] players;
		
		AgentController[] agents  = new AgentController[Utilities.numberPlayers];

		int fascists = (int) Math.ceil(Utilities.numberPlayers*0.4);

		players = new AID[Utilities.numberPlayers];
		
		Player[] auxPlayers = new Player[Utilities.numberPlayers];
				
		container = Runtime.instance().createMainContainer(new ProfileImpl());

		Board b = new Board();
		AgentController board = container.acceptNewAgent("Board", b);
		board.start();	
		
		for (int i = 0; i < fascists-1; i++) {
			Fascist f = new Fascist();
			auxPlayers[i] = f;
			AgentController agent = container.acceptNewAgent("Player_" + i, f);
			players[i] = f.getAID();	
			agents[i] = agent;

		}
		
		Hitler h = new Hitler();
		AgentController agent = container.acceptNewAgent("Player_" + (fascists-1) , h);
		players[fascists-1] = h.getAID();
		auxPlayers[fascists-1] = h;
		agents[fascists-1] = agent;
		
		for (int i = fascists; i < Utilities.numberPlayers; i++) {
			Liberal l = new Liberal();
			AgentController a = container.acceptNewAgent("Player_" + i, l);
			players[i] = l.getAID();
			auxPlayers[i] = l;
			agents[i] = a;
		}
		
		Utilities.shuffleArray(players);
		
		for (int i = 0; i < Utilities.numberPlayers; i++) 
			auxPlayers[i].setPlayers(players);	

		b.setPlayers(players);
		
		for (int i = 0; i < Utilities.numberPlayers; i++) 
			agents[i].start();
	}

}
