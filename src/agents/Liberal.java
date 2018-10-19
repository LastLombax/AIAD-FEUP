package agents;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class Liberal extends Player{
	
	public void setup() {
		//super.setup();
		System.out.print("But I am a Liberal!\n");
	//	addBehaviour(new ReceivingMessages());
	}
	
	class ReceivingMessages extends Behaviour{

		@Override
		public void action() {
			System.out.println("RECEIVE...");
			ACLMessage msg = receive();
			System.out.println("AFTER RECEIVE...");
			if(msg != null) {
			System.out.println("hello there");
				System.out.println(msg);
				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("Got you fam...");
				send(reply);
			} else {
				block();
			}
			
			done();
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
