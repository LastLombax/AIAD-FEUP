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
	}

	public String selectCardToDiscard(String cards) {
		int countFascists = cards.length() - cards.replaceAll("F","").length();
		int countLiberals = cards.length() - cards.replaceAll("L","").length();
		if(countFascists == 3 || countLiberals == 3) 
			cards = cards.substring(2);

		else if (countFascists == 2 && countLiberals == 1)
			cards = cards.replace("L_", "");

		else if (countLiberals == 2 && countFascists == 1) {
			int indexF = cards.indexOf('L');
			String aux = cards.substring(indexF, indexF + 2);
			cards = cards.replaceFirst(aux, "");
		}

		return cards;

	}

	public void registerOthers() {
		for (int i = 0; i < Utilities.players.length; i++)
			getMap().put(Utilities.players[i], 0.0);
		
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


	public String selectCardToPass(String cards) {
		int countFascists = cards.length() - cards.replaceAll("F","").length();
		int countLiberals = cards.length() - cards.replaceAll("L","").length();
		if(countFascists == 2 || countLiberals == 2) 
			cards = cards.substring(2);

		else if (countFascists == 2)
			cards = cards.replace("L_", "");

		else if (countLiberals == 1 && countFascists == 1) {
			int indexF = cards.indexOf('L');
			String aux = cards.substring(indexF, indexF + 2);
			cards = cards.replaceFirst(aux, "");
		}		

		return cards;
	}

}
