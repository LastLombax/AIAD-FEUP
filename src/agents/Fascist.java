package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;

/**
 * Class that extends Player and represents a Fascist in the game
 */
public class Fascist extends Player{
	
	public String hitler;

	public void setup() {
		super.setup();	
		super.type = Utilities.FASCIST;
		System.out.println(getAID().getLocalName() + ": " + type);
	}
	
	public void register(ACLMessage msg) {
		String[] players = msg.getContent().split(";");
		for(int i = 0; i < players.length; i++) {
			String[] player = players[i].split(":");
			String role = player[1];
			String localName = player[0];
			if(role.equals(Utilities.FASCIST) || role.equals(Utilities.HITLER)) {
				if(role.equals(Utilities.HITLER))
					hitler = localName;
				getMap().put(localName, 100.0);
			}
			else if(role.equals(Utilities.LIBERAL)) {
				getMap().put(localName, 0.0);
			}
		}
	}

	public String chooseChancellor() {
		int fascistPolicies = super.getPoliciesFromBoard(Utilities.FASCIST_POLICIES); 
		if (fascistPolicies >= 3) {
			return hitler;
		}

		List<String> listOfFas = new ArrayList<>();

		for (Entry<String, Double> entry : map.entrySet()) {
			if (entry.getValue().equals(100.0) && entry.getKey() != getAID().getLocalName())
				listOfFas.add(entry.getKey());
		}
		listOfFas.remove(getAID().getLocalName());

		int index = ThreadLocalRandom.current().nextInt(listOfFas.size());

		return listOfFas.get(index);
	}
	

	public Boolean electionChoice(Double presidentValue, Double chancellorValue) {
		//both are fascists
		if (presidentValue == 100 && chancellorValue == 100)
			return true;

		int fascistPolicies = super.getPoliciesFromBoard(Utilities.LIBERAL_POLICIES); 
		int liberalPolicies = super.getPoliciesFromBoard(Utilities.FASCIST_POLICIES); 

		//president liberal and chancellor fascist
		if (presidentValue == 0 && chancellorValue == 100) {
			if (fascistPolicies - liberalPolicies >= 2)
				return true;
			return false;
		}

		//president fascist and chancellor liberal
		if (presidentValue == 100 && chancellorValue == 0) {
			if (fascistPolicies - liberalPolicies >= 1)
				return true;
			return false;
		}

		//president liberal and chancellor liberal
		return false;
	}
	
	public String selectCardToDiscard(String cards) {
		if(cards.indexOf(Utilities.FASCIST_CARD) == -1 || cards.indexOf(Utilities.LIBERAL_CARD) == -1) {
			cards = cards.substring(1);
		}
		else {
			cards = cards.replaceFirst(Utilities.LIBERAL_CARD, "");
		}
		return cards;
	}

}
