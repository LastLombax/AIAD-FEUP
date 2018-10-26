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

	int index = 0;

	public void setup() {

		addBehaviour(new sendBoardReady());
		addBehaviour(new MessageFromBoard());
	}


	class MessageFromBoard extends CyclicBehaviour{
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {

				switch(msg.getOntology()) {
				case "President":
					AID chancellor = chooseChancellor();
					System.out.println("chancellor " + chancellor.getLocalName());
					System.out.println("Cards: " + msg.getContent());
					String newCards = selectCardToDiscard(msg.getContent());
					System.out.println("new cards: " + newCards);
					sendCardsToChancellor(chancellor);
					break;
				case "Chancellor":
					selectCardToPass();
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


	public void registerOthers() {};


	public AID chooseChancellor() {return null;};

	public void sendCardsToChancellor(AID chancellor) {};

	public HashMap<AID, Double> getMap(){
		return map;
	}

	public int getIndex() {

		for (int i = 0; i < Utilities.players.length; i++) {			
			if (Utilities.players[i].equals(getAID()))
				return i;
		}
		
		return -1;
	}

	public void selectCardToPass() {};

	public String selectCardToDiscard(String cards) {return null;};

	public void registerFascists(String string) {};

}
