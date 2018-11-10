package gui;

import agents.Board;
import agents.Fascist;
import agents.Hitler;
import agents.Liberal;
import jade.core.Runtime;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utilities.Utilities;

/**
 * Main Class
 */
public class Main {
	
	private static ContainerController container;
	
	/**
	 * Begins the program, by creating all agents and starting them
	 * @param args
	 * @throws StaleProxyException
	 */
	public static void main(String[] args) throws StaleProxyException {
		
		int fascists = (int) Math.ceil(Utilities.numberPlayers*0.4);
				
		container = Runtime.instance().createMainContainer(new ProfileImpl());

		Board b = new Board();
		AgentController board = container.acceptNewAgent("Board", b);
		board.start();	
		
		for (int i = 0; i < fascists-1; i++) {
			Fascist f = new Fascist();
			AgentController agent = container.acceptNewAgent("Player_" + i, f);
			agent.start();
		}
		
		Hitler h = new Hitler();
		AgentController agent = container.acceptNewAgent("Player_" + (fascists-1) , h);
		agent.start();
		
		for (int i = fascists; i < Utilities.numberPlayers; i++) {
			Liberal l = new Liberal();
			AgentController a = container.acceptNewAgent("Player_" + i, l);
			a.start();
		}
	}

}
