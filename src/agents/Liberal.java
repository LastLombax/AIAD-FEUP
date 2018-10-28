package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;

public class Liberal extends Player{

	public void setup() {
		super.setup();
	}

	public String selectCardToDiscard(String cards) {
		int countFascists = cards.length() - cards.replaceAll("F","").length();
		int countLiberals = cards.length() - cards.replaceAll("L","").length();
		if(countFascists == 3 || countLiberals == 3) 
			cards = cards.substring(2);

		else if (countFascists == 2 && countLiberals == 1){
			int indexF = cards.indexOf('F');
			String aux = cards.substring(indexF, indexF + 2);
			cards = cards.replaceFirst(aux, "");
		}

		else if (countLiberals == 2 && countFascists == 1) 
			cards = cards.replace("F_", "");

		return cards;
	}

	public void registerOthers() {
		for (int i = 0; i < Utilities.players.length; i++)
			getMap().put(Utilities.players[i], 0.0);

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


	public String selectCardToPass(String cards) {
		int countFascists = cards.length() - cards.replaceAll("F","").length();
		int countLiberals = cards.length() - cards.replaceAll("L","").length();
		if(countFascists == 2 || countLiberals == 2) 
			cards = cards.substring(1);

		else if (countFascists == 1 && countLiberals == 1){
			int indexF = cards.indexOf('F');
			String aux = cards.substring(indexF, indexF + 2);
			cards = cards.replaceFirst(aux, "");
		}

		else if (countLiberals == 2) 
			cards = cards.replace("F_", "");

		return cards;
	}
	
	public Boolean voteForElection() {
		return null;
		
		
	}

}
