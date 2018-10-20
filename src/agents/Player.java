package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Player extends Agent {
	

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
						System.out.println("Cards: " + msg.getContent());
						String newCards = selectCardToDiscard(msg.getContent());
						System.out.println("new cards: " + newCards);
						int chancellor = chooseChancellor();
						break;
					case "Chancellor":
						selectCardToPass();
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

	public void selectCardToPass() {};

	public String selectCardToDiscard(String cards) {return null;};

}
