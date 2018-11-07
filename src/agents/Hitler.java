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


	public void registerOthers() {
		for (int i = 0; i < Utilities.players.length; i++)
			getMap().put(Utilities.players[i], -1.0);
		getMap().replace(Utilities.players[getIndex()],  100.0);

	}

	public AID chooseChancellor() {

		int fascistPolicies = super.getPoliciesFromBoard("Fascist_Policies");

		HashMap<AID, Double> listOfFas = new HashMap<AID, Double>();

		for (Entry<AID, Double> entry : map.entrySet())
			if ((entry.getValue() >= 65 || entry.getValue() == -1 ) && entry.getKey() != getAID())
				listOfFas.put(entry.getKey(), entry.getValue());

		int index = 0;

		if (listOfFas.isEmpty()) {
			int myIndex = getIndex();
			while(true) {
				index = ThreadLocalRandom.current().nextInt(Utilities.players.length);
				if (index != myIndex)
					break;
			}
			return Utilities.players[index];
		}

		return Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
	}	


	public Boolean electionChoice(Double presidentValue, Double chancellorValue) {

		//both are fascists or inconclusive
		if ( (presidentValue >= 65 && chancellorValue >= 65 )
				|| (presidentValue == -1 && chancellorValue == -1))
			return true;

		int fascistPolicies = super.getPoliciesFromBoard("Fascist_Policies"); 
		int liberalPolicies = super.getPoliciesFromBoard("Liberal_Policies"); 


		//president -1
		if (presidentValue == -1) {
			if (chancellorValue >= 65 && fascistPolicies - liberalPolicies >= 0)
				return true;
			return false;
		}

		//chancellor -1
		if (chancellorValue == -1) {
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
	
	public void updateInformation(String chancellorCards, String card) {
		System.out.println("hitler updating stuff");
		
		//chancellor was inconclusive
		//if (getMap().get(chancellor) == -1) {
		//	if(chancellorCards.indexOf("F") == -1)
				
		//}

	}


}
