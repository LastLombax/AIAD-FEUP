package agents;

import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Player extends Agent {
	
    HashMap<AID, Double> map = new HashMap<AID, Double>();

	public void setup() {
		addBehaviour(new MessageFromBoard());
	}
	
	class MessageFromBoard extends CyclicBehaviour{
		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {
				
				switch(msg.getOntology()) {
					case "President":
						int chancellor = chooseChancellor();
						System.out.println("Cards: " + msg.getContent());
						String newCards = selectCardToDiscard(msg.getContent());
						System.out.println("new cards: " + newCards);
						sendCardsToChancellor(chancellor);
						break;
					case "Chancellor":
						selectCardToPass();
						break;
					case "Fascist":
						System.out.println("FASCIST");
						registerFascists(msg.getContent());
						break;
					default:
						break;					
					}
				System.out.println(msg);
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("U wot m8....");
				send(reply);
			} else {
				block();
			}
			
		}
		
	}
	
	
	public int chooseChancellor() {return -1;};

	public void sendCardsToChancellor(int chancellor) {};
	
	public HashMap<AID, Double> getMap(){
		return map;
	}

	public void selectCardToPass() {};

	public String selectCardToDiscard(String cards) {return null;};

	public void registerFascists(String string) {};

}
