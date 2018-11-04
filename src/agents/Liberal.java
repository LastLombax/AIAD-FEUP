package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;

public class Liberal extends Player{
	

	public void setup() {
		super.setup();
		super.type = "liberal";
	}

	
	public void registerOthers() {
		for (int i = 0; i < Utilities.players.length; i++)
			getMap().put(Utilities.players[i], -1.0);

		getMap().replace(Utilities.players[getIndex()],  100.0);

	}

	public AID chooseChancellor() {

		int liberalPolicies = 0; 
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.addReceiver(board);
		msg.setOntology("Liberal_Policies");
		send(msg);
		ACLMessage answer = receive();

		while(true) {
			answer = receive();
			if (answer != null) {
				if(answer.getOntology() == "Liberal_Policies") {
					liberalPolicies = Integer.parseInt(answer.getContent());
					break;
				}
				else
					System.out.println("Wrong ontology: " + answer.getOntology());
			}
		}

		List<AID> listOfLib = new ArrayList<>();

		for (Entry<AID, Double> entry : map.entrySet())
			if (entry.getValue() > 85 && entry.getKey() != getAID())
				listOfLib.add(entry.getKey());

		int index = 0;

		if (listOfLib.isEmpty()) {
			int myIndex = getIndex();
			while(true) {
				index = ThreadLocalRandom.current().nextInt(Utilities.players.length);
				if (index != myIndex)
					break;
			}
			return Utilities.players[index];
		}

		index = ThreadLocalRandom.current().nextInt(listOfLib.size());
		return listOfLib.get(index);
	}


	

	public Boolean voteForElection(String candidates) {
/*		HashMap<AID, Double> sortedMap = (HashMap<AID, Double>) Utilities.sortByValue(map);

		String[] cand = candidates.split(","); 

		String president = cand[0];
		String chancellor = cand[1];
		
		Double weight = 0.0;


		for (Entry<AID, Double> entry : sortedMap.entrySet()) {
			System.out.println(entry.getKey().getLocalName()+" : "+entry.getValue());
			if (entry.getKey().getLocalName().equals(president)){
				if (entry.getValue() == -1) 
					weight = 1.0;
				else if (entry.getValue() >= 60) 
					vote = true;
				else
					vote = false;
			}
			if (entry.getKey().getLocalName().equals(chancellor)){
				if (entry.getValue() == -1) 
					weight = 1.0;
				else if (entry.getValue() >= 60) 
					vote = true;
				else
					vote = false;

			}

		}
		
		if (weight == 2)
			return true;
		


		return true;
	}

	private boolean mapHasInitialValues(HashMap<AID, Double> sortedMap) {
		int count = Collections.frequency(new ArrayList<Double>(sortedMap.values()), -1.0);

		*/
		return false;
	};

}
