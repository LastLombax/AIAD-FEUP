package agents;

import java.util.ArrayList;
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

	public void registerFascists(String fascists) {
		System.out.println("registering");
		for (int i = 0, n = fascists.length(); i < n; i++) {
			int fas = Integer.parseInt(String.valueOf(fascists.charAt(i)));
			getMap().put(Utilities.players[fas], 100.0);
		}
		hitler = Utilities.players[fascists.length()];
		for (int i = fascists.length(); i < Utilities.players.length; i++)
			getMap().put(Utilities.players[i], 0.0);

	}

	public AID chooseChancellor() {

		//request number of fascist policies from board
		int fascistPolicies = 0; 
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(board);
		msg.setOntology("Fascist_Policies");
		send(msg);
		ACLMessage answer = receive();
		if(answer != null) {
			if(answer.getOntology() == "Fascist_Policies") {
				fascistPolicies = Integer.parseInt(answer.getContent());
			}
			else
				System.out.println("Deu asneira");
		}
		else 
			System.out.println("Deu muita asneira");

		if (fascistPolicies >= 3) 
			return hitler;

		List<AID> listOfFas = null;
		listOfFas = new ArrayList<>();

		for (Entry<AID, Double> entry : map.entrySet()) 
			if (entry.getValue().equals(100.0))
				listOfFas.add(entry.getKey());

		int index = ThreadLocalRandom.current().nextInt(listOfFas.size()) % listOfFas.size();
		
		return listOfFas.get(index);
	}



	public void selectCardToPass() {}

}
