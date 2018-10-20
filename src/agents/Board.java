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

	private int currentPresident = -1;
	private int currentChancellor;

	private int electionTracker = 0;

	private AID[] players;


	public void setup() {

		this.players = (AID[]) this.getArguments();
		System.out.println("Board");
		for(int i = 0; i < players.length; i++) {
			System.out.println((players[i]).getName());
		}
		//addBehaviour(new SendMessageToPlayers());
		addBehaviour(new SetPresident());

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

	public class SetPresident extends OneShotBehaviour{
		public void action() {
			if (currentPresident == players.length-1)
				currentPresident = 0;
			else
				currentPresident++;
			System.out.println("President " + players[currentPresident].getName());
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(players[currentPresident]);
			msg.setPerformative(ACLMessage.INFORM);
			msg.setOntology("President");
			msg.setContent(getCards());
			send(msg);

			done();
		}
	}

	public String getCards() {
		
		
		return "L_F_L_";
	}
	
	/*
	 * Fascista escolhe fascista
	 * Hitler escolhe quem tem + prob de ser fascista
	 * Liberal igual mas para fascista
	 * 
	 * Cada jogador tem associado uma probabilidade de os outros serem de uma certa equipa
	 * 
	 *
	 */
	 

}
