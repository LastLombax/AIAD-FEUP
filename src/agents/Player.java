package agents;

import java.util.HashMap;
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

	HashMap<AID, Double> map = new HashMap<AID, Double>();

	AID board;

	AID president, chancellor;

	protected String type = null;

	int index = 0;


	public void setup() {

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
					switch(msg.getOntology()) {
					case "Register_Fascist":
						registerFascists(msg.getContent());
						break;
					case "Register_Others":
						registerOthers();
						break;
					default:
						break;
					}
					break;

				case Ready:
					//addBehaviour(new sendBoardReady());

					break;
				case Delegation :
					switch(msg.getOntology()) {
					case "President":
						chancellor = chooseChancellor();
						System.out.println("chancellor " + chancellor.getLocalName());
						startElection(chancellor.getLocalName(), this.getAgent().getLocalName());
						break;						

					default:
						break;					
					}
					break;
				case Election :
					switch(msg.getOntology()) {
					case "Election":
						Boolean vote = voteForElection(msg.getContent());
						System.out.println(getAID().getLocalName() + " voted: " + vote);
						sendVoteToBoard(vote);
					default:
						break;		
					}
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

	}

	/**
	 * Class to send a READY message to Board to start the game
	 */
	class sendBoardReady extends OneShotBehaviour{
		@Override
		public void action() {
			getBoardFromDF();
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.addReceiver(board);
			msg.setOntology("READY");
			send(msg);
		}

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
			if (Utilities.players[i].getLocalName().equals(pres))
				president = Utilities.players[i];			
			if (Utilities.players[i].getLocalName().equals(chanc))
				chancellor = Utilities.players[i];
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
	 * Returns type of player
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Messages the Board to start the election
	 * @param chancellor Name of the chancellor
	 * @param president Name of the president
	 */
	public void startElection(String chancellor, String president) {
		Utilities.currentState = State.Election;
		ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
		msg.setContent(president + "," + chancellor);
		msg.setOntology("Election_Begin");
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
		msg.setOntology("Election_Vote");
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
	 * Returns the HashMap of agents that maps a key Agent with a Probability of being of the same team
	 * @return map
	 */
	public HashMap<AID, Double> getMap(){
		return map;
	}


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
	 * Updates information about the chancellor
	 * @param chancellorCards Cards that the chancellor received
	 * @param card Card that was chosen by the chancellor
	 */
	public void updateInformation(String chancellorCards, String card) {};



	public Boolean electionChoice(Double presidentValue, Double chancellorValue) {return null;};

	/**
	 * Registers information about the fascists. Send to Fascists
	 * @param fascists String that indicates who is a fascist and who is hitler
	 */
	public void registerFascists(String string) {};

	/**
	 * Registers information about all players. Send to Liberals and Hitler
	 */
	public void registerOthers() {};

	/**
	 * Chooses the chancellor 
	 * @return Returns the chosen chancellor
	 */
	public AID chooseChancellor() {return null;};

	/**
	 * Selects a card to be discarded by the President
	 * @param cards Cards to choose from
	 * @return Two remaining cards
	 */
	public String selectCardToDiscard(String cards) {

		int countFascists = cards.length() - cards.replaceAll("F","").length();
		int countLiberals = cards.length() - cards.replaceAll("L","").length();
		if(countFascists == 3 || countLiberals == 3) 
			cards = cards.substring(2);

		else if (countFascists == 2 && countLiberals == 1) {
			if (getType().equals("fascist"))
				cards = cards.replace("L_", "");
			else 
				cards = Utilities.replaceChar('F', cards);

		} 
		else if (countLiberals == 2 && countFascists == 1) {
			if (getType().equals("liberal"))
				cards = cards.replace("F_", "");
			else 
				cards = Utilities.replaceChar('L', cards);
		}		
		return cards;
	}

	/**
	 * Selects a card to be selected as the new Policy by the Chancellor
	 * @param cards Cards to choose from
	 * @return Card that is the new policy
	 */
	public String selectCardToPass(String cards) {  
		int countFascists = cards.length() - cards.replaceAll("F","").length();
		int countLiberals = cards.length() - cards.replaceAll("L","").length();
		if(countFascists == 2 || countLiberals == 2) 
			cards = cards.substring(2);

		else if (countFascists == 1) {
			if (getType().equals("fascist"))
				cards = cards.replace("L_", "");
			else
				cards = cards.replace("F_", "");
		}

		return cards;
	}


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

}
