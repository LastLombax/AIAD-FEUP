package agents;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Board extends Agent {


	private int fascistPolicies = 0;
	private int liberalPolicies = 0;

	private int currentPresident;
	private int currentChancellor;

	private int electionTracker = 0;
	

	public void setup() {
		System.out.println("Hello world!");
		
		for(int i = 0; i < this.getArguments().length; i++) {
			System.out.println(((AID) this.getArguments()[i]).getName());
		}
		addBehaviour(new SendMessageToPlayers());
	}

	public class SendMessageToPlayers extends OneShotBehaviour{
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			for(int i = 0; i < Board.this.getArguments().length; i++) {
				msg.addReceiver((AID) Board.this.getArguments()[i]);
			}
			msg.setContent("WAZUP MY NIGGAS");
			send(msg);
			
			done();
		}
		
	}

}
