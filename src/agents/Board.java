package agents;
import java.util.Vector;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utitilites.Utilities;

public class Board extends Agent {


	private int fascistPolicies = 0;
	private int liberalPolicies = 0;

	private int currentPresident = -1;
	private int currentChancellor;

	private int electionTracker = 0;
	
	private AID[] players;
	
	private String[] cards = new String[17];

	public void setup() {

		createCards();
		this.players = Utilities.players;
		
		//addBehaviour(new sendInfoToFascists());

		
		//addBehaviour(new SendMessageToPlayers());
		//addBehaviour(new SetPresident());

	}

	private void createCards() {
		int i;
		for (i = 0; i < 6; i++)
			cards[i] = "L_";
		for (i = 6; i < 17; i++)
			cards[i] = "F_";
		Utilities.shuffleArray(cards);
		
	}

	public class sendInfoToFascists extends OneShotBehaviour{
		public void action() {
			String fascists = "";
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			int i = 0;
			for(; i < (int) Math.ceil(players.length*0.4) -1 ; i++) {
				msg.addReceiver(players[i]);
				fascists += i;
			}
			fascists += i;
			System.out.println("fascists " + fascists);
			msg.setOntology("Fascist");
			msg.setContent(fascists);
			send(msg);
			
			Utilities.shuffleArray(Utilities.players);
			
			done();
		}
	}

	public class SendMessageToPlayers extends OneShotBehaviour{
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			for(int i = 0; i < players.length; i++) {
				msg.addReceiver(players[i]);
			}
			msg.setContent("WAZUP");
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
