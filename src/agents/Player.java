package agents;

import java.util.HashMap;

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

public class Player extends Agent {

	HashMap<AID, Double> map = new HashMap<AID, Double>();

	AID board;

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
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {

				switch(msg.getOntology()) {
				case "President":
					//AID chancellor = chooseChancellor();
					//System.out.println("chancellor " + chancellor.getLocalName());
					//send President and Chancellor to board so it can send to everyone to start election
					//startElection(chancellor.getLocalName(), this.getAgent().getLocalName());
					System.out.println("Cards: " + msg.getContent());
					String newCards = selectCardToDiscard(msg.getContent());
					System.out.println("new cards: " + newCards);
					/*sendCardsToChancellor(chancellor, newCards);*/
					break;
				case "Election":
					Boolean vote = voteForElection(msg.getContent());
					//sendVoteToBoard(vote);
					break;
				case "Chancellor":
					String selectedPolicy = selectCardToPass(msg.getContent());
					System.out.println("Policy: " + selectedPolicy);					
					break;
				case "Register_Fascist":
					registerFascists(msg.getContent());
					break;
				case "Register_Others":
					registerOthers();
					break;

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

	public String getType() {

		return type;
	}

	public void startElection(String chancellor, String president) {
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
		msg.setOntology("Chancellor");
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
	public Boolean voteForElection(String string) {return null;};

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
				replaceChar('F', cards);

		} 
		else if (countLiberals == 2 && countFascists == 1) {
			if (getType().equals("liberal"))
				cards = cards.replace("F_", "");
			else 
				replaceChar('L', cards);
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
	
	public String replaceChar(char c, String cards) {
		int index = cards.indexOf(c);
		String aux = cards.substring(index, index + 2);
		return cards.replaceFirst(aux, "");
	}


}
