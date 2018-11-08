package agents;

import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;
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
import utilities.Utilities.State;

public class Player extends Agent {

	HashMap<AID, Double> map = new HashMap<AID, Double>(); // double is the probability of being of the same faction

	AID board;

	AID president, chancellor;
	
	private int fascistPolicies;

	protected String type = null;

	int index = 0;


	public void setup() {
		this.doWait(250);
		addBehaviour(new sendBoardReady());
		addBehaviour(new MessageFromBoard());
	}

	/**
	 * Class to manage all messages from the Board during the game
	 */
	class MessageFromBoard extends CyclicBehaviour{
		AID chancellor = null;
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {
				switch (Utilities.currentState) {
				case Setup:
					break;
				case Delegation :
					this.dealDelegation(msg);
					break;
				case Election :
					this.dealElection(msg);
					break;
				case PolicySelection :
					switch(msg.getOntology()) {
					case "DiscardCard":
						System.out.println("Cards: " + msg.getContent());
						String newCards = selectCardToDiscard(msg.getContent());
						System.out.println("new cards: " + newCards);
						sendCardsToChancellor(chancellor, newCards);
						break;
					case "SelectFinalPolicy":
						String selectedPolicy = selectCardToPass(msg.getContent());
						System.out.println("New Policy: " + selectedPolicy);
						sendPolicyToBoard(msg.getContent(), selectedPolicy);
						break;
					case "Delegacy":
						updateDelegacy(msg.getContent());
						break;

					case "NewPolicy":
						updateInformation(msg.getContent());
						enterNextTurn();
						break;
					default:
						break;		
					}
				default:
					break;
				}

			} else {
				block();
			}

		}

		private void dealDelegation(ACLMessage msg) {
			if(msg.getOntology().equals(Utilities.PRESIDENT)) {
				int p = Integer.parseInt(msg.getContent());
				president = Utilities.players[p];
				System.out.println(getAID().getLocalName() + ": PRESIDENT IS " + Utilities.players[p].getLocalName());
				if(president.equals(Utilities.players[getIndex()])) {
					chancellor = chooseChancellor();
					System.out.println(getAID().getLocalName() + ": Chancellor: " + chancellor.getLocalName());
					startElection(chancellor.getLocalName(), this.getAgent().getLocalName());
				}
			}

		}
		
		private void dealElection(ACLMessage msg) {
			if(msg.getOntology().equals(Utilities.ELECTION)) {
				System.out.println(getAID().getLocalName() +  ": ELECTION BEGIN of chancelor: " + msg.getContent());
				Boolean vote = voteForElection(msg.getContent());
				System.out.println(getAID().getLocalName() + ":voted: " + vote);
				sendVoteToBoard(vote);
			}
			else if(msg.getOntology().equals("NewPolicyElection")) {
				updateInformation(msg.getContent());
				enterNextTurn();
			}	
		}

	}

	/**
	 * Class to send a READY message to Board to start the game
	 */
	class sendBoardReady extends OneShotBehaviour{
		@Override
		public void action() {
			getBoardFromDF();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(board);
			msg.setOntology(Utilities.READY);
			send(msg);
		}

	}


	public void enterNextTurn() {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(board);
		msg.setOntology("NextTurn");
		send(msg);

	}


	/**
	 * Updates information regarding who is the president and who is the chancellor
	 * @param delegacy String received from the board
	 */
	public void updateDelegacy(String delegacy) {
		String[] msgContent = delegacy.split(","); 
		String pres = msgContent[0];
		String chanc= msgContent[1];

		for (int i = 0; i < Utilities.players.length; i++) {
			String playerName = Utilities.players[i].getLocalName();
			if (playerName.equals(pres)) {
				president = Utilities.players[i];	
				break;
			}
		}

		for (int i = 0; i < Utilities.players.length; i++) {
			String playerName = Utilities.players[i].getLocalName();
			if (playerName.equals(chanc)) {
				chancellor = Utilities.players[i];
				break;
			}
		}
	}


	/**
	 * Updates information using the new policy and the cards from the chancellor
	 * @param content String that includes the new policy and the cards
	 */
	public void updateInformation(String content) {

		String[] msgContent = content.split(","); 
		String chancellorCards = msgContent[0];
		String card = msgContent[1];

		updateInformation(chancellorCards, card);

	}

	/**
	 * Sends the policy chosen by the Chancellor to the board
	 * @param cards
	 * @param selectedPolicy
	 */
	public void sendPolicyToBoard(String cards, String selectedPolicy) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(board);
		msg.setOntology("SelectedPolicy");
		msg.setContent(cards + "," + selectedPolicy);
		send(msg);

	}



	/**
	 * Messages the Board to start the election
	 * @param chancellor Name of the chancellor
	 * @param president Name of the president
	 */
	public void startElection(String chancellor, String president) {
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.setContent(chancellor);
		msg.setOntology(Utilities.ELECTION_BEGIN);
		msg.addReceiver(board);
		send(msg);
	}


	/**
	 * Sends the election vote to the Board
	 * @param vote Boolean that indicates whether the Players votes yes or no
	 */
	public void sendVoteToBoard(Boolean vote) {
		ACLMessage msg = new ACLMessage();
		if (vote)
			msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
		else
			msg.setPerformative(ACLMessage.REJECT_PROPOSAL);
		msg.setOntology(Utilities.ELECTION_VOTE);
		msg.addReceiver(board);
		send(msg);

	}

	/**
	 * Sends cards for the chancellor to choose from
	 * @param chancellor AID that represents the current Chancellor
	 * @param cards String that contains two of the selected cards
	 */
	public void sendCardsToChancellor(AID chancellor, String cards) {
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.addReceiver(chancellor);
		msg.setContent(cards);
		msg.setOntology("SelectFinalPolicy");
		send(msg);
	};



	/**
	 * Player receives information of who is President and Chancellor and votes For or Against the election
	 * @param string The President and the Chancellor 
	 * @return
	 */
	public Boolean voteForElection(String candidates) {

		HashMap<AID, Double> sortedMap = (HashMap<AID, Double>) Utilities.sortByValue(map);

		String[] cand = candidates.split(","); 

		String president = cand[0];
		String chancellor = cand[1];

		Double presidentValue = 0.0, chancellorValue = 0.0;

		for (Entry<AID, Double> entry : sortedMap.entrySet()) {

			if (entry.getKey().getLocalName().equals(president))	
				presidentValue = entry.getValue();
			if (entry.getKey().getLocalName().equals(chancellor))
				chancellorValue = entry.getValue();		
		}

		if (getAID().getLocalName().equals(president) || getAID().getLocalName().equals(chancellor))
			return true;

		return electionChoice(presidentValue, chancellorValue);
	}



	/**
	 * Selects a card to be selected as the new Policy by the Chancellor
	 * @param cards Cards to choose from
	 * @return Card that is the new policy
	 */
	public String selectCardToPass(String cards) { 
		if(cards.indexOf(Utilities.FASCIST_CARD) == -1 || cards.indexOf(Utilities.LIBERAL_CARD) == -1) {
			cards = cards.substring(1);
		}
		else if(getType().equals("fascist"))
			cards = cards.replace(Utilities.LIBERAL_CARD, "");
		else 
			cards = cards.replace(Utilities.FASCIST_CARD, "");
		return cards;
	}


	/**
	 * Selects a card to be discarded by the President
	 * @param cards Cards to choose from
	 * @return Two remaining cards
	 */
	public String selectCardToDiscard(String cards) {
		if(cards.indexOf(Utilities.FASCIST_CARD) == -1 || cards.indexOf(Utilities.LIBERAL_CARD) == -1) {
			cards = cards.substring(1);
		}
		else if (getType().equals("fascist")) {
			cards = cards.replaceFirst(Utilities.LIBERAL_CARD, "");
		}
		else {
			cards = cards.replaceFirst(Utilities.FASCIST_CARD, "");
		}
		return cards;
	}


	/**
	 * Asks the Board for the number of passed policies of a specified faction
	 * @param ontology Specifies faction on the message
	 * @return Number of passed policies
	 */
	public int getPoliciesFromBoard(String ontology) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(board);
		msg.setOntology(ontology);
		send(msg);
		ACLMessage answer = null;
		while(answer == null) {
			answer = receive();
			if (answer != null) {
				if(answer.getOntology().equals(ontology))
					return Integer.parseInt(answer.getContent());	
				else
					System.out.println("Wrong ontology: " + answer.getOntology());
			}
		}
		return -1;
	}
	



	/**
	 * Searched the DF for the Board and saves it on @field Board
	 */
	private void getBoardFromDF() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("board");
		template.addServices(sd);
		try{
			DFAgentDescription[] result = DFService.search(this, template);
			for (int i = 0; i < result.length; i++)
				board =  result[i].getName();

		} catch(FIPAException fe) {fe.printStackTrace();}
	}


	/**
	 * Returns the index of the Agent from the array on Utilities.Utilities.java
	 * @return position in the array
	 */
	public int getIndex() {
		for (int i = 0; i < Utilities.players.length; i++) 		
			if (Utilities.players[i].equals(getAID()))
				return i;

		return -1;
	}

	/**
	 * Returns type of player
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the HashMap of agents that maps a key Agent with a Probability of being of the same team
	 * @return map
	 */
	public HashMap<AID, Double> getMap(){
		return map;
	}


	//OVERRIDEN METHODS

	/**
	 * Updates information about the chancellor
	 * @param chancellorCards Cards that the chancellor received
	 * @param card Card that was chosen by the chancellor
	 */
	public void updateInformation(String chancellorCards, String card) {
		Double presidentValue = null;
		Double chancellorValue = null;

		// O map.get(president) n�o est� a funcionar e isto tamb�m n�o
		for (Entry<AID, Double> entry : map.entrySet()) 
			if (entry.getKey().getLocalName().equals(president.getLocalName()))
				presidentValue = entry.getValue();

		for (Entry<AID, Double> entry : map.entrySet()) 
			if (entry.getKey().getLocalName().equals(chancellor.getLocalName()))
				chancellorValue = entry.getValue();


		System.out.println(presidentValue);
		if (presidentValue < 65.0 && president.equals(this.getAID())) 
			updateInformationOnPresident(chancellorCards, card, presidentValue);


		if (chancellorValue < 65.0 && chancellor.equals(this.getAID())) 
			updateInformationOnChancellor(chancellorCards, card, chancellorValue);
		

	}


	/**
	 * Updates information regarding the player who is the chancellor
	 * @param chancellorValue 
	 * @param card 
	 * @param chancellorCards2 
	 */
	public void updateInformationOnChancellor(java.lang.String chancellorCards2, java.lang.String card, Double chancellorValue) {};

	/**
	 * Updates information regarding the player who is the president
	 * @param value 
	 * @param card 
	 * @param chancellorCards 
	 */
	public void updateInformationOnPresident(String chancellorCards, String card, Double value) {};


	/**
	 * Each player decides on voting Yes or No on the election, depending on the information
	 * they have
	 * @param presidentValue Percentage of the President belonging into the Player's faction
	 * @param chancellorValue Percentage of the Chancellor belonging into the Player's faction
	 * @return
	 */
	public Boolean electionChoice(Double presidentValue, Double chancellorValue) {return null;};


	/**
	 * Registers information about all players. Send to Liberals and Hitler
	 */
	public void registerOthers() {};

	/**
	 * Chooses the chancellor 
	 * @return Returns the chosen chancellor
	 */
	public AID chooseChancellor() {return null;};

}
