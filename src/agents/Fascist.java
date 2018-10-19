package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Fascist extends Player{
	
	public void setup() {
		//super.setup();
		System.out.print("But I am a Fascist!\n");
	//	addBehaviour(new ReceivingMessages());
	}
	class ReceivingMessages extends CyclicBehaviour{

		@Override
		public void action() {
			ACLMessage msg = receive();
			if(msg != null) {
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

}
