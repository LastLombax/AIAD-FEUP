package agents;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;

public class Board extends Agent {


	private int fascistPolicies = 0;
	private int liberalPolicies = 0;

	private int currentPresident;
	private int currentChancellor;

	private int electionTracker = 0;

	public void setup() {
		System.out.println("Hello world!");
	}

	public class GameLoop extends CyclicBehaviour {
		public void action() {
			System.out.println("here");
		}
		
	}

}
