package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;

public class Hitler extends Player {
	

	public void setup() {
		super.setup();
		super.type = "fascist";

	}

	
	public void registerOthers() {
		for (int i = 0; i < Utilities.players.length; i++)
			getMap().put(Utilities.players[i], -1.0);

		getMap().replace(Utilities.players[getIndex()],  100.0);

	}

	public AID chooseChancellor() {
		int fascistPolicies = 0; 
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(board);
		msg.setOntology("Fascist_Policies");
		send(msg);
		ACLMessage answer = receive();


		while(true) {
			answer = receive();
			if (answer != null) {
				if(answer.getOntology() == "Fascist_Policies") {
					fascistPolicies = Integer.parseInt(answer.getContent());
					break;
				}
				else
					System.out.println("Wrong ontology: " + answer.getOntology());
			}
		}

		List<AID> listOfFas = new ArrayList<>();

		for (Entry<AID, Double> entry : map.entrySet()) 
			if (entry.getValue().equals(70.0) && entry.getKey() != getAID())
				listOfFas.add(entry.getKey());


		if (listOfFas.isEmpty()) {
			int myIndex = getIndex();
			while(true) {
				index = ThreadLocalRandom.current().nextInt(Utilities.players.length);
				if (index != myIndex)
					break;
			}
			return Utilities.players[index];
		}

		int index = ThreadLocalRandom.current().nextInt(listOfFas.size());

		return listOfFas.get(index);
	}	


	
	public Boolean voteForElection(String candidates) {

		//System.out.println("candidates: " + candidates);
		return true;
	};

}
