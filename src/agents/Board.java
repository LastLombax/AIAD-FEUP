package agents;

import java.util.LinkedList;
import java.util.Queue;

import jade.core.AID;
import jade.core.Agent;
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
	private int infoPlayers = 0;

	private int fascistPolicies = 0;
	private int liberalPolicies = 0;

	private int currentPresident = -1;
	private int currentChancellor;

	private int electionTracker = 0;

	Queue<String> deck = new LinkedList<>();
	String cardsInPlay;
	private AID[] players;

	public void setPlayers(AID[] players) {
		this.players = players;
	}

	public void setup() {
		addToDF();
		addBehaviour(new MessagesFromPlayers());
	}


	/**
	 * Sets the president of the turn
	 */
	public void setPresident() {

		if (currentPresident == players.length - 1)
			currentPresident = 0;
		else
			currentPresident++;
		System.out.println("President " + players[currentPresident].getLocalName());
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(players[currentPresident]);
		msg.setPerformative(ACLMessage.INFORM);
		msg.setOntology(Utilities.PRESIDENT);
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
		Utilities.currentState = State.Player_info;
		sendInfoToFascists();
		sendInfoToRest();
	}

	/**
	 * Begins delegacy by changing the state and setting the president
	 */
	private void startDelegacy() {
		Utilities.currentState = State.Delegation;
		System.out.println("Fascist Policies: " + fascistPolicies);
		System.out.println("Liberal Policies: " + liberalPolicies);

		setPresident();

	}

	/**
	 * Class to manage all messages from players during the game
	 */
	class MessagesFromPlayers extends CyclicBehaviour {
		Election election = null;
		@Override
		public void action() {
			ACLMessage msg = receive();
			if (msg != null) {
				ACLMessage reply = null;
				reply = msg.createReply();
				switch (Utilities.currentState) {
				case Setup:
					if(this.verifySetup(msg)) 
						startGame();
					break;
				case Player_info:
					if(this.verifyInfo(msg)) 
						startDelegacy();
					break;
				case Delegation :
					switch (msg.getOntology()) {
					case "Fascist_Policies":
						reply.setPerformative(ACLMessage.INFORM);
						reply.setOntology(Utilities.FASCIST_POLICIES);
						reply.setContent(fascistPolicies + "");
						send(reply);
						break;
					case "Liberal_Policies":
						reply.setPerformative(ACLMessage.INFORM);
						reply.setOntology(Utilities.LIBERAL_POLICIES);
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
						reply.setOntology(Utilities.FASCIST_POLICIES);
						reply.setContent(fascistPolicies + "");
						send(reply);
						break;
					case "Liberal_Policies":
						reply.setPerformative(ACLMessage.INFORM);
						reply.setOntology(Utilities.LIBERAL_POLICIES);
						reply.setContent(liberalPolicies + "");
						send(reply);
						break;
					case "Election_Begin":
						election = new Election(msg.getContent());
						break;
					case "Election_Vote":
						election.verifyElection(msg);
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
						//addBehaviour(new checkPlayers());

						break;
					}
					break;

				default:
					break;
				}

			} else 
				block();

		}

		private boolean verifyInfo(ACLMessage msg) {
			
			if(msg.getOntology().equals(Utilities.PLAYER_INFO)) {
				if (Integer.parseInt(msg.getContent() ) != Utilities.numberPlayers) {
					ACLMessage reply = new ACLMessage(ACLMessage.REQUEST);
					reply.setOntology(Utilities.REGISTER_FASCIST);
					reply.addReceiver(msg.getSender());
					send(reply);
				}
				else
					infoPlayers++;
			}
			return (infoPlayers == Utilities.numberPlayers);
		}

		private boolean verifySetup(ACLMessage msg) {
			if(msg.getOntology().equals(Utilities.READY))
				readyPlayers++;

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

			for (int i = 0; i < players.length; i++)
				if (players[i].getLocalName().equals(chancellor)) 
					currentChancellor = i;

			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setContent(candidates);
			msg.setOntology(Utilities.ELECTION);
			for (int i = 0; i < players.length; i++)
				msg.addReceiver(players[i]);
			send(msg);	
		}

		public void verifyElection(ACLMessage msg) {
			switch(action(msg)) {
			case 0: 
				System.out.println("The Election has passed");
				Utilities.currentState = State.PolicySelection;
				informPlayersOfDelegacy();
				sendCardsToPresident();
				break;
			case 1: //election is still going
				break;
			case -1: 
				System.out.println("The Election has been refused");
				electionTracker++;
				System.out.println("election Tracker: " + electionTracker);
				startDelegacy();
				break;
			case -2: //election tracker is 3
				System.out.println("tracker = 3, the top card will be the new policy");
				setNewPolicyFromHead(deck.poll());
				break;

			}


		}

		/**
		 * Receives the vote from a Player
		 * and proceeds with the election
		 * @param msg
		 * @return true if election has ended
		 */
		public int action(ACLMessage msg) {

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

			if (ja + nein == Utilities.numberPlayers) { 
				if (ja > nein) 
					return 0;
				return -1;
			}

			if (electionTracker == 3) {
				electionTracker = 0;
				return -2;
			}

			return 1;
		}

		/**
		 * Verifies the end of the election
		 * If all players have voted, then if the majority voted yes, then election passes
		 * otherwise, the electionTracker is incremented.
		 * @return 0 if passed, -1 if refused 
		 */
		public int electionEnded() {

			if (ja + nein == Utilities.numberPlayers) { 
				if (ja > nein) {
					System.out.println("The Election has passed");
					return 0;
				}
				System.out.println("The Election has been refused");
				electionTracker++;
				return -1;
			}
			return 1;
		}


	}



	/**
	 * Informs players of the newest president and chancellor
	 * @param delegacy String that contains the string "president,chancellor"
	 */
	public void informPlayersOfDelegacy() {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setPerformative(ACLMessage.INFORM);
		msg.setOntology(Utilities.DELEGACY);
		msg.setContent(players[currentPresident].getLocalName() + "," + players[currentChancellor].getLocalName());
		for (int i = 0; i < players.length; i++)
			msg.addReceiver(players[i]);
		send(msg);

	}

	/**
	 * Sends the top 3 cards to the President
	 */
	public void sendCardsToPresident() {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(players[currentPresident]);
		msg.setPerformative(ACLMessage.INFORM);
		msg.setOntology(Utilities.DISCARD_CARD);
		msg.setContent(getCards());
		send(msg);		
	}

	/*
	 * Sets the passed policy from the chancellor
	 */
	public void setNewPolicy(String content) {		

		String[] msgContent = content.split(","); 
		String card = msgContent[1];
		if (card.equals(Utilities.Fascist_Card))
			fascistPolicies++;
		else if (card.equals(Utilities.Liberal_Card))
			liberalPolicies++;
		returnDiscardedCards(cardsInPlay.replaceFirst(card, ""));
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(content);
		msg.setOntology(Utilities.NEW_POLICY);
		for (int i = 0; i < players.length; i++)
			msg.addReceiver(players[i]);
		send(msg);
	}

	/*
	 * Sets the passed policy from the chancellor
	 */
	public void setNewPolicyFromHead(String policy) {	

		if (policy.equals(Utilities.Fascist_Card))
			fascistPolicies++;
		else if (policy.equals(Utilities.Liberal_Card))
			liberalPolicies++;
		deck.add(policy);
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent(policy);
		msg.setOntology(Utilities.NEW_POLICY_ELECTION);
		for (int i = 0; i < players.length; i++)
			msg.addReceiver(players[i]);
		send(msg);
	}

	/**
	 * Creates the cards from the deck
	 */
	private void createCards() {
		String[] cards = new String[17];
		int i;
		for (i = 0; i < 6; i++)
			cards[i] = Utilities.Liberal_Card;
		for (i = 6; i < 17; i++)
			cards[i] = Utilities.Fascist_Card;
		Utilities.shuffleArray(cards);

		for(i = 0; i < cards.length; i++) {
			deck.add(cards[i]);
		}
	}

	/**
	 * Sends request to all fascists to register who is a fascist. Hitler does not
	 * hold this information
	 */
	private void sendInfoToFascists() {
		
		String fascists = "";
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		int numberFascist = (int) Math.ceil(players.length * 0.4) - 1;
		for (int j = 0; j < players.length; j++) {
			String[] playerInfo = players[j].getLocalName().split("_");
			int number = Integer.parseInt(playerInfo[1]);
			if(number == numberFascist) {
				fascists += players[j].getLocalName() + ";";
				break;
			}
		}

		for (int j = 0; j < players.length; j++) {
			String[] playerInfo = players[j].getLocalName().split("_");
			int number = Integer.parseInt(playerInfo[1]);
			if(number >= 0 && number < numberFascist) {
				msg.addReceiver(players[j]);
				fascists += players[j].getLocalName() + ";";
			}
		}
		fascists = fascists.substring(0, fascists.length() - 1);

		msg.setOntology(Utilities.REGISTER_FASCIST);
		msg.setContent(fascists);
		send(msg);
	}

	/**
	 * Sends request to other players to register about the other players
	 */
	private void sendInfoToRest() {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		int numberFascist = (int) Math.ceil(players.length * 0.4) - 1;

		for (int i = 0; i < Utilities.numberPlayers; i++) {
			String[] playerInfo = players[i].getLocalName().split("_");
			int number = Integer.parseInt(playerInfo[1]);
			if(number >= numberFascist) 
				msg.addReceiver(players[i]);
			
		}

		msg.setOntology(Utilities.REGISTER_OTHERS);
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
