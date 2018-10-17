package gui;

import java.util.Vector;

import agents.Board;
import agents.Fascist;
import agents.Liberal;
import jade.core.Agent;
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
		int liberals = (int) Math.ceil(numberPlayers*0.6);

		
		Vector<AgentController> players = new Vector<AgentController>();
		
		container = Runtime.instance().createMainContainer(new ProfileImpl());
		
		for (int i = 0; i < fascists; i++) {
			AgentController agent = container.acceptNewAgent("Player_" + i, new Fascist());
			agent.start();
		}
		
		for (int i = fascists; i < numberPlayers; i++) {
			AgentController agent = container.acceptNewAgent("Player_" + i, new Liberal());
			agent.start();
		}
		
	}

}
