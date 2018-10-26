package agents;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
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


	public void sendInfoToFascists() {
		String fascists = "";
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		int i = 0;
		for (; i < (int) Math.ceil(Utilities.players.length * 0.4) - 1; i++) {
			msg.addReceiver(Utilities.players[i]);
			fascists += i;
		}
		fascists += i;
		msg.setOntology("Register_Fascist");
		msg.setContent(fascists);
		send(msg);

	}

	private void sendInfoToRest() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		for (int i = (int) Math.ceil(Utilities.players.length * 0.4) - 1; i < Utilities.numberPlayers ; i++)
			msg.addReceiver(Utilities.players[i]);		
		msg.setOntology("Register_Others");
		send(msg);
	}

	public void setPresident() {

		if (currentPresident == Utilities.players.length - 1)
			currentPresident = 0;
		else
			currentPresident++;
		System.out.println("President " + Utilities.players[currentPresident].getLocalName());
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(Utilities.players[currentPresident]);
		msg.setPerformative(ACLMessage.INFORM);
		msg.setOntology("President");
		msg.setContent(getCards());
		send(msg);
	}


	public String getCards() {

		return "L_F_L_";
	}

	public void startGame() {
		System.out.println("THE GAME HAS BEGUN");
		createCards();
		sendInfoToFascists();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {e.printStackTrace();}
		Utilities.shuffleArray(Utilities.players);
		sendInfoToRest();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {e.printStackTrace();}
		setPresident();
		addBehaviour(new MessagesFromPlayers());
	}


	class MessagesFromPlayers extends CyclicBehaviour{
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {
				ACLMessage reply = null;
				reply = msg.createReply();

				switch(msg.getOntology()) {
				case "Fascist_Policies":
					reply.setPerformative(ACLMessage.INFORM);
					reply.setOntology("Fascist_Policies");
					reply.setContent(fascistPolicies + "");
					send(reply);
					break;
				case "Liberal_Policies":
					reply.setPerformative(ACLMessage.INFORM);
					reply.setOntology("Liberal_Policies");
					reply.setContent(liberalPolicies + "");
					send(reply);
					break;
				default:
					break;					
				}
			} else {
				block();
			}

		}

	}

	class GameLoop extends Behaviour{
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

		@Override
		public boolean done() {
			return false;
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

	public class checkPlayers extends Behaviour {
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				switch (msg.getOntology()) {
				case "READY":
					readyPlayers++;
					break;
				default:
					break;
				}
			}
		}

		@Override
		public boolean done() {
			if (readyPlayers == Utilities.numberPlayers) {
				startGame();
				return true;
			}
			return false;
		}

	}

}
