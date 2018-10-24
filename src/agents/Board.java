package agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;

public class Board extends Agent {

	private int readyPlayers = 0;
	private int fascistPolicies = 0;
	private int liberalPolicies = 0;

	private int currentPresident = -1;
	private int currentChancellor;

	private int electionTracker = 0;

	private AID[] players;

	private String[] cards = new String[17];

	public void setup() {
		addToDF();
		addBehaviour(new checkPlayers());
	}


	private void createCards() {
		int i;
		for (i = 0; i < 6; i++)
			cards[i] = "L_";
		for (i = 6; i < 17; i++)
			cards[i] = "F_";
		Utilities.shuffleArray(cards);

	}

	public class sendInfoToFascists extends OneShotBehaviour {
		public void action() {
			String fascists = "";
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			int i = 0;
			for (; i < (int) Math.ceil(players.length * 0.4) - 1; i++) {
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

	public class SetPresident extends OneShotBehaviour {
		public void action() {
			if (currentPresident == players.length - 1)
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
		}
	}

	

	public String getCards() {

		return "L_F_L_";
	}

	public void startGame() {
		System.out.println("THE GAME HAS BEGUN");
		createCards();
		this.players = Utilities.players;
		addBehaviour(new sendInfoToFascists());
		addBehaviour(new SetPresident());

	}
	
	class MessagesFromPlayers extends CyclicBehaviour{
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {
				
				switch(msg.getOntology()) {
					case "Fascist_Policies":
						ACLMessage reply = msg.createReply();
						reply.setPerformative(ACLMessage.INFORM);
						reply.setOntology("Fascist_Policies");
						reply.setContent(fascistPolicies + "");
						send(reply);
						break;
					case "Chancellor":
						break;
					case "Fascist":
						break;
					default:
						break;					
					}
			} else {
				block();
			}
			
		}
		
	}
	
	class GameLoop extends CyclicBehaviour{
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {
				
				switch(msg.getOntology()) {
					case "President":
						break;
					case "Chancellor":
						break;
					case "Fascist":
						break;
					default:
						break;					
					}
			} else {
				block();
			}
			
		}
		
	}
	

	private void addToDF() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("board");
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	/*
	 * Fascista escolhe fascista Hitler escolhe quem tem + prob de ser fascista
	 * Liberal igual mas para liberal
	 * 
	 * Cada jogador tem associado uma probabilidade de os outros serem de uma certa
	 * equipa
	 * 
	 *
	 */
	
	public class checkPlayers extends CyclicBehaviour {
		public void action() {
			ACLMessage msg = receive();
			if (readyPlayers == Utilities.numberPlayers) {
				startGame();
				done();
			}
			if (msg != null) {
				switch (msg.getOntology()) {
				case "READY":
					readyPlayers++;
					break;
				default:
					break;
				}
			} else {
				block();
			}
		}
	}

}
