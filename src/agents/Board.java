package agents;
import java.util.Vector;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;

public class Board extends Agent {


	private int fascistPolicies = 0;
	private int liberalPolicies = 0;

	private int currentPresident;
	private int currentChancellor;

	private int electionTracker = 0;
	
	private Vector<Agent> players;

	public Board(Vector<Agent> pl) {
		players = pl;
		
	}

	public void setup() {
		System.out.println("Hello world!");
	}

	public class SendMessageToPlayers extends Behaviour{
		public void action() {
			System.out.println("here");
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

}
