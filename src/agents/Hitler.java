package agents;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import jade.core.AID;
import utilities.Utilities;

public class Hitler extends Player {



	public void setup() {
		super.setup();
		super.type = "fascist";

	}


	public Boolean electionChoice(Double presidentValue, Double chancellorValue) {

		//both are fascists or inconclusive
		if ( (presidentValue >= 65 && chancellorValue >= 65 )
				|| (presidentValue == 50 && chancellorValue == 50))
			return true;

		int fascistPolicies = super.getPoliciesFromBoard("Fascist_Policies"); 
		int liberalPolicies = super.getPoliciesFromBoard("Liberal_Policies"); 


		//president 50
		if (presidentValue == 50) {
			if (chancellorValue >= 65 && fascistPolicies - liberalPolicies >= 0)
				return true;
			return false;
		}

		//chancellor 50
		if (chancellorValue == 50) {
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

		if(chancellorCards.indexOf(Utilities.Fascist_Card) == -1) // fascist with LLL or liberal with LLL/FLL
			value+=6.0;

		else if (chancellorCards.indexOf(Utilities.Liberal_Card) == -1) //fascist with FFF/FFL or liberal with FFF
			value+=9.0;

		else {
			if (card.equals(Utilities.Liberal_Card))
				value+=10.0;
			else
				value-=20.0;			
		}
		if (value < 0)
			value = 0.0;
		else if (value > 100)
			value = 100.0;

		//map.put(super.president, value);
	}


	public void updateInformationOnChancellor(String chancellorCards, String card, Double value) {

		//if chancellor cards are LL or FF, the chancellor didn't have a choice, so no conclusions
		int countFascists = chancellorCards.length() - chancellorCards.replaceAll("F","").length();
		int countLiberals = chancellorCards.length() - chancellorCards.replaceAll("L","").length();

		if (countFascists == 1 && countLiberals == 1) //FL or LF
			value+=30;
		else
			value-=30;			

		if (value < 0)
			value = 0.0;
		else if (value > 100)
			value = 100.0;

	//	map.put(chancellor, value);

	}


}
