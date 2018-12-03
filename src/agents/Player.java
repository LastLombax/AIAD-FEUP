package agents;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
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
import utilities.CSV;
import utilities.Utilities;

/**
 * Class that represents the Player
 * The Player receives messages from the player and decides how to proceed
 * during the game. It also manages messages from/to the board.
 * Each in game player extends this class
 */
public class Player extends Agent {

	ConcurrentHashMap<String, Double> map = new ConcurrentHashMap<String, Double>(); // double is the probability of being of the same faction

	AID board;

	String president, chancellor;
	
	protected String type = null;

	int index = 0;
	
	int personality;
	
	CSV csv;
	
	String winner;
	

	/**
	 * (non-Javadoc)
	 * @see jade.core.Agent#setup()
	 */
	public void setup() {
		try {
			csv = new CSV(this.getAID().getLocalName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.doWait(250);
		addBehaviour(new sendBoardReady());
		addBehaviour(new MessageFromBoard());
	}

	/**
	 * Class to manage all messages from the Board during the game
	 */
	class MessageFromBoard extends CyclicBehaviour{
		String chancellor = null;
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {
				if(msg.getOntology().equals(Utilities.REGISTER)) {
					register(msg);
				}
				else if(msg.getOntology().equals(Utilities.PRESIDENT)) {
					president = msg.getContent();
					if(president.equals(getAID().getLocalName())) {
						chancellor = chooseChancellor();
						System.out.println("The Chancellor will be " + chancellor);
						startElection(chancellor, this.getAgent().getLocalName());
					}
				}
				else if(msg.getOntology().equals(Utilities.ELECTION)) {
					Boolean vote = voteForElection(msg.getContent());
					System.out.println(getAID().getLocalName() + " voted " + vote);
					sendVoteToBoard(vote);
				}
				else if(msg.getOntology().equals(Utilities.DELEGACY)) {
					updateDelegacy(msg.getContent());
				}
				else if(msg.getOntology().equals(Utilities.DISCARD_CARD)) {
					System.out.println("Cards passed to " + getAID().getLocalName() + ": " + msg.getContent());
					String newCards = selectCardToDiscard(msg.getContent());
					System.out.println("Cards selected by " +getAID().getLocalName() +": " + newCards);
					sendCardsToChancellor(chancellor, newCards);
				}
				else if(msg.getOntology().equals(Utilities.SELECT_FINAL_POLICY)) {
					String selectedPolicy = selectCardToPass(msg.getContent());
					System.out.println("Card chosen by " + getAID().getLocalName() + ": " + selectedPolicy);
					sendPolicyToBoard(msg.getContent(), selectedPolicy);
				}
				else if(msg.getOntology().equals(Utilities.NEW_POLICY)) {
					updateInformation(msg.getContent());
					enterNextTurn();
				}
				else if(msg.getOntology().equals(Utilities.NEW_POLICY_ELECTION)) 
					enterNextTurn();
				else if(msg.getOntology().equals(Utilities.GAME_OVER)) {
					System.out.println(getAID().getLocalName() + ": " +  msg.getContent());
					if (msg.getContent().equals(Utilities.FASCISTS_WIN))
						winner = "fascists";
					else if (msg.getContent().equals(Utilities.LIBERALS_WIN))
						winner = "liberals";
					else if (msg.getContent().equals(Utilities.HITLER_ELECTED))
						winner = "hitler";
				}
				else if(msg.getOntology().equals(Utilities.GAME_OVER_INFO)) {
					//csv.saveDelegacy(president, chancellor);
					try {
						csv.writeMembership(msg.getContent());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					csv.closeFile();
					doDelete();
				}
			} else {
				block();
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
			msg.setContent(type);
			send(msg);
		}

	}

	/**
	 * Sends board a message to enter next turn
	 */
	public void enterNextTurn() {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(board);
		msg.setOntology(Utilities.NEXT_TURN);
		send(msg);
	}


	/**
	 * Updates information regarding who is the president and who is the chancellor
	 * @param delegacy String received from the board
	 */
	public void updateDelegacy(String delegacy) {
		String[] msgContent = delegacy.split(","); 
		president = msgContent[0];
		chancellor = msgContent[1];
	}


	/**
	 * Updates information using the new policy and the cards from the chancellor
	 * @param content String that includes the new policy and the cards
	 */
	public void updateInformation(String content) {

		String[] msgContent = content.split(","); 
		String chancellorCards = msgContent[0];
		String card = msgContent[1];
		
		saveInformationForModel(chancellorCards, card);

		updateInformation(chancellorCards, card);

	}

	private void saveInformationForModel(String chancellorCards, String card) {
		
		String[] information = {president, chancellorCards, chancellor, card};
		csv.write(information);				
	}


	/**
	 * Sends the policy chosen by the Chancellor to the board
	 * @param cards
	 * @param selectedPolicy
	 */
	public void sendPolicyToBoard(String cards, String selectedPolicy) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(board);
		msg.setOntology(Utilities.SELECTED_POLICY);
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
	public void sendCardsToChancellor(String chancellor, String cards) {
		AID c = new AID();
		c.setLocalName(chancellor);
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.addReceiver(c);
		msg.setContent(cards);
		msg.setOntology(Utilities.SELECT_FINAL_POLICY);
		send(msg);
	};



	/**
	 * Player receives information of who is President and Chancellor and votes For or Against the election
	 * @param string The President and the Chancellor 
	 * @return
	 */
	public Boolean voteForElection(String candidates) {
		HashMap<String, Double> sortedMap = (HashMap<String, Double>) Utilities.sortByValue(map);
		String[] cand = candidates.split(","); 

		String president = cand[0];
		String chancellor = cand[1];

		Double presidentValue = 0.0, chancellorValue = 0.0;

		for (Entry<String, Double> entry : sortedMap.entrySet()) {

			if (entry.getKey().equals(president))	
				presidentValue = entry.getValue();
			if (entry.getKey().equals(chancellor))
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
		else if(getType().equals("fascist") || getType().equals("hitler")) {
			cards = cards.replace(Utilities.LIBERAL_CARD, "");
		}
		else 
			cards = cards.replace(Utilities.FASCIST_CARD, "");
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
	 * Updates information about the chancellor
	 * @param chancellorCards Cards that the chancellor received
	 * @param card Card that was chosen by the chancellor
	 */
	public void updateInformation(String chancellorCards, String card) {
		
		Double presidentValue = map.get(president);
		Double chancellorValue = map.get(chancellor);
		
		if (presidentValue < 65.0 && !president.equals(this.getAID().getLocalName())) 
			updateInformationOnPresident(chancellorCards, card, presidentValue);

		if (chancellorValue < 65.0 && !chancellor.equals(this.getAID().getLocalName())) 
			updateInformationOnChancellor(chancellorCards, card, chancellorValue);

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
	public ConcurrentHashMap<String, Double> getMap(){
		return map;
	}
	
	

	/**
	 * Updates information regarding the player who is the chancellor
	 * @param chancellorValue Value of the chancellor
	 * @param card Card selected by the chancellor
	 * @param chancellorCards Cards received by the chancellor
	 */
	public void updateInformationOnChancellor(String chancellorCards, String card, Double chancellorValue) {};

	/**
	 * Updates information regarding the player who is the president
	 * @param value Value of the president
	 * @param card Card selected by the chancellor
	 * @param chancellorCards Cards received by the chancellor
	 */
	public void updateInformationOnPresident(String chancellorCards, String card, Double value) {};


	/**
	 * Each player decides on voting Yes or No on the election, depending on the information
	 * they have
	 * @param presidentValue Percentage of the President belonging into the Player's faction
	 * @param chancellorValue Percentage of the Chancellor belonging into the Player's faction
	 * @return true if voted yes, false if voted no
	 */
	public Boolean electionChoice(Double presidentValue, Double chancellorValue) {return null;};


	/**
	 * Registers information about the other players
	 * @param msg Message received
	 */
	public void register(ACLMessage msg) {}

	/**
	 * Chooses the chancellor 
	 * @return Returns the chosen chancellor
	 */
	public String chooseChancellor() {return null;};
	

	/**
	 * Selects a card to be discarded by the President
	 * @param cards Cards to choose from
	 * @return Two remaining cards
	 */
	public String selectCardToDiscard(String cards) {return null;}

}
