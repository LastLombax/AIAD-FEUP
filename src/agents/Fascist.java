package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;

public class Fascist extends Player{

	public AID hitler;
	
	public void setup() {
		super.setup();	
		super.type = "fascist";
	}

	
	public void registerFascists(String fascists) {
		for (int i = 0, n = fascists.length(); i < n; i++) {
			int fas = Integer.parseInt(String.valueOf(fascists.charAt(i)));
			getMap().put(Utilities.players[fas], 100.0);
		}
		hitler = Utilities.players[fascists.length()];
		for (int i = fascists.length(); i < Utilities.players.length; i++)
			getMap().put(Utilities.players[i], 0.0);

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
		
		if (fascistPolicies >= 3) 
			return hitler;

		List<AID> listOfFas = new ArrayList<>();

		for (Entry<AID, Double> entry : map.entrySet()) 
			if (entry.getValue().equals(100.0) && entry.getKey() != getAID())
				listOfFas.add(entry.getKey());

	
		int index = ThreadLocalRandom.current().nextInt(listOfFas.size());

		return listOfFas.get(index);
	}



	
	/*public Boolean voteForElection(String candidates) {
		
		ArrayList<Double> players = Utilities.Map2ArraySorted(map);
		
				
		for (Double temp : players) {
			System.out.println(temp);
		}
		
		System.out.println("candidates: " + candidates);
		return true;
	};*/

}
