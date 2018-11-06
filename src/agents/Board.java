package agents;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;
import utilities.Utilities.State;

public class Board extends Agent {

	private int readyPlayers = 0;

	private int fascistPolicies = 0;
	private int liberalPolicies = 0;

	private int currentPresident = -1;
	private int currentChancellor;

	private int electionTracker = 0;

	Queue<String> deck = new LinkedList<>();
	String cardsInPlay;
	
	public void setup() {
		addToDF();
		addBehaviour(new MessagesFromPlayers());
	}


	/**
	 * Sets the president of the turn
	 */
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
		send(msg);
	}

	/**
	 * Gets the top three cards from the deck
	 * 
	 * @return cards
	 */
	public String getCards() {
		cardsInPlay = deck.poll() + deck.poll() + deck.poll();
		return cardsInPlay;
	}
	
	/**
	 * Returns discarded cards to back of deck queue
	 * @param discarded string representing the two discarded cards
	 */
	public void returnDiscardedCards(String discarded) {
		deck.add(discarded.substring(0, 1));
		deck.add(discarded.substring(1));
	}

	/**
	 * Initiates the game after every agent is ready
	 */
	private void startGame() {
		System.out.println("THE GAME HAS BEGUN");
		createCards();
		sendInfoToFascists();
		Utilities.shuffleArray(Utilities.players);
		sendInfoToRest();
		Utilities.currentState = State.Delegation;
		setPresident();
	}
	
	/**
	 * Class to manage all messages from players during the game
	 */
	class MessagesFromPlayers extends CyclicBehaviour {
		Election election = null;
		@Override
		public void action() {
			System.out.println("Board waiting for messages");
			ACLMessage msg = receive();
			if (msg != null) {
				System.out.println("Board received message: " + msg.getOntology());
				ACLMessage reply = null;
				reply = msg.createReply();
				switch (Utilities.currentState) {
				case Setup:
					if(this.verifySetup(msg)) {
						System.out.println("All players are ready!");
						startGame();
					}
					break;
				case Ready:
					break;
				case Delegation :
					switch (msg.getOntology()) {
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
					break;
				case Election :
					switch (msg.getOntology()) {
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
					case "Election_Begin":
						election = new Election(msg.getContent());
						break;
					case "Election_Vote":
						if (election.action(msg)){
							Utilities.currentState = State.PolicySelection;
							informPlayersOfDelegacy();
							sendCardsToPresident();
						}
						break;
					default:
						break;
					}
					break;
					
				case PolicySelection :
					switch (msg.getOntology()) {
					case "SelectedPolicy":
						setNewPolicy(msg.getContent());
						break;
					case "NextTurn":
						Utilities.currentState = State.Ready;
						//addBehaviour(new checkPlayers());
						
						break;
					}
					break;

				default:
					break;
				}

			} else {
				System.out.println("Blocked");
				block();
			}

		}
		
		private boolean verifySetup(ACLMessage msg) {
			if(msg.getOntology().equals(Utilities.READY)) {
				readyPlayers++;
			}
			System.out.println("Players Confirmed: " + readyPlayers);
			return (readyPlayers == Utilities.numberPlayers);
		}
	}

	/**
	 * Election class that manages the election phase
	 * @author vitor
	 *
	 */
	class Election{

		int ja = 0;
		int nein = 0;

		/**
		 * The constructor receives the candidates, updates them in the Board and 
		 * sends them to all players a PROPOSE message to begin the Election
		 * @param candidates
		 */
		Election(String candidates){
			String[] msgContent = candidates.split(","); 
			String chancellor = msgContent[1];

			for (int i = 0; i < Utilities.players.length; i++)
				if (Utilities.players[i].getLocalName().equals(chancellor)) 
					currentChancellor = i;

			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setContent(candidates);
			msg.setOntology("Election");
			for (int i = 0; i < Utilities.players.length; i++)
				msg.addReceiver(Utilities.players[i]);
			send(msg);	
		}
		
		/**
		 * Receives the vote from a Player
		 * and proceeds with the election
		 * @param msg
		 * @return true if election has ended
		 */
		public boolean action(ACLMessage msg) {

			if (checkEnd())
				return true;

			switch (msg.getPerformative()) {
			case ACLMessage.ACCEPT_PROPOSAL:
				ja++;
				break;
			case ACLMessage.REJECT_PROPOSAL:
				nein++;
				break;

			default:
				break;
			}
			if (checkEnd())
				return true;

			return false;

		}

		/**
		 * Verifies the end of the election
		 * If all players have voted, then if the majority voted yes, then election passes
		 * otherwise, the electionTracker is incremented.
		 * @return
		 */
		public boolean checkEnd() {

			if (electionTracker == 3) {
				electionTracker = 0;
				return true;
			}

			if (ja + nein == Utilities.numberPlayers) { 
				if (ja > nein) {
					System.out.println("The Election has passed");
					return true;
				}
				System.out.println("The Election has been refused");
				electionTracker++;
				//setPresident();				
			}
			return false;
		}


	}



	/**
	 * Informs players of the newest president and chancellor
	 * @param delegacy String that contains the string "president,chancellor"
	 */
	public void informPlayersOfDelegacy() {
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setPerformative(ACLMessage.INFORM);
		msg.setOntology("Delegacy");
		msg.setContent(Utilities.players[currentPresident].getLocalName() + "," + Utilities.players[currentChancellor].getLocalName());
		for (int i = 0; i < Utilities.players.length; i++)
			msg.addReceiver(Utilities.players[i]);
		send(msg);

	}

	/**
	 * Sends the top 3 cards to the President
	 */
	public void sendCardsToPresident() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(Utilities.players[currentPresident]);
		msg.setPerformative(ACLMessage.INFORM);
		msg.setOntology("DiscardCard");
		msg.setContent(getCards());
		send(msg);		
	}

	/*
	 * Sets the passed policy from the chancellor
	 */
	public void setNewPolicy(String content) {		

		String[] msgContent = content.split(","); 
		String card = msgContent[1];
		if (card.equals("F"))
			fascistPolicies++;
		else if (card.equals("L"))
			liberalPolicies++;
		returnDiscardedCards(cardsInPlay.replaceFirst(card, ""));
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(content);
		msg.setOntology("NewPolicy");
		for (int i = 0; i < Utilities.players.length; i++)
			msg.addReceiver(Utilities.players[i]);
		send(msg);
	}

	/**
	 * Creates the cards from the deck
	 */
	private void createCards() {
		String[] cards = new String[17];
		int i;
		for (i = 0; i < 6; i++)
			cards[i] = "L";
		for (i = 6; i < 17; i++)
			cards[i] = "F";
		Utilities.shuffleArray(cards);
		
		Random rnd = ThreadLocalRandom.current();
		for(i = 0; i < cards.length; i++) {
			deck.add(cards[i]);
		}
	}

	/**
	 * Sends request to all fascists to register who is a fascist. Hitler does not
	 * hold this information
	 */
	public void sendInfoToFascists() {
		String fascists = "";
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
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

	/**
	 * Sends request to other players to register about the other players
	 */
	private void sendInfoToRest() {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		for (int i = (int) Math.ceil(Utilities.players.length * 0.4) - 1; i < Utilities.numberPlayers; i++)
			msg.addReceiver(Utilities.players[i]);
		msg.setOntology("Register_Others");
		send(msg);
	}
	

	/**
	 * Adds the Board Agent to the DF
	 */
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

}
