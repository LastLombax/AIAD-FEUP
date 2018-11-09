package agents;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;

public class Hitler extends Player {


	public void setup() {
		super.setup();
		super.type = Utilities.HITLER;
		System.out.println(getAID().getLocalName() + ": " + type);
	}

	public String chooseChancellor() {
		System.out.println("MapSize: " + map.size());
		HashMap<String, Double> listOfLib = new HashMap<String, Double>();
		for (Entry<String, Double> entry : map.entrySet())
			if (entry.getKey() != getAID().getLocalName())
				listOfLib.put(entry.getKey(), entry.getValue());
		System.out.println("Size :" + listOfLib.size());
		return Collections.max(listOfLib.entrySet(), Map.Entry.comparingByValue()).getKey();
	}	
	
	public void register(ACLMessage msg) {
		String[] players = msg.getContent().split(";");
		for(int i = 0; i < players.length; i++) {
			String[] player = players[i].split(":");
			String localName = player[0];
			getMap().put(localName, 60.0);
		}
	}



	public Boolean electionChoice(Double presidentValue, Double chancellorValue) {

		//both are fascists or inconclusive
		if ( (presidentValue >= 65 && chancellorValue >= 65 )
				|| (presidentValue == 60 && chancellorValue == 60))
			return true;

		int fascistPolicies = super.getPoliciesFromBoard(Utilities.FASCIST_POLICIES); 
		int liberalPolicies = super.getPoliciesFromBoard(Utilities.LIBERAL_POLICIES); 


		//president -1
		if (presidentValue == 60) {
			if (chancellorValue >= 65 && fascistPolicies - liberalPolicies >= 0)
				return true;
			return false;
		}

		//chancellor -1
		if (chancellorValue == 60) {
			if (presidentValue >= 65)
				return true;
			if (presidentValue < 65 && fascistPolicies - liberalPolicies >= 3)
				return true;
			return false;
		}


		//president liberal and chancellor fascist
		if (presidentValue < 65 && chancellorValue >= 65) {
			if (fascistPolicies - liberalPolicies >= 2)
				return true;
			return false;
		}

		//president fascist and chancellor liberal
		if (presidentValue >= 65 && chancellorValue < 65)
			return true;		

		//both are liberals
		return false;
	}
	
	

	public void updateInformationOnPresident(String chancellorCards, String card, Double value) {

		if(chancellorCards.indexOf(Utilities.FASCIST_CARD) == -1) // fascist with LLL or liberal with LLL/FLL
			value+=6.0;

		else if (chancellorCards.indexOf(Utilities.LIBERAL_CARD) == -1) //fascist with FFF/FFL or liberal with FFF
			value+=9.0;

		else {
			if (card.equals(Utilities.LIBERAL_CARD))
				value+=10.0;
			else
				value-=20.0;			
		}
		if (value < -1)
			value = 0.0;
		else if (value > 100)
			value = 100.0;

		map.put(super.president, value);
	}


	public void updateInformationOnChancellor(String chancellorCards, String card, Double value) {

		//if chancellor cards are LL or FF, the chancellor didn't have a choice, so no conclusions
		int countFascists = chancellorCards.length() - chancellorCards.replaceAll("F","").length();
		int countLiberals = chancellorCards.length() - chancellorCards.replaceAll("L","").length();

		if (countFascists == 1 && countLiberals == 1) //FL or LF
			value+=30;
		else
			value-=30;			

		if (value < -1)
			value = 0.0;
		else if (value > 100)
			value = 100.0;

		map.put(chancellor, value);

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
