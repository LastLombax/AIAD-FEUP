package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import jade.core.AID;
import utilities.Utilities;

public class Fascist extends Player{




	public String hitler;

	public void setup() {
		super.setup();	
		super.type = "fascist";
	}

	public void registerFascists(String fascists) {
		
		String[] data = fascists.split(";");
		for (String a : data)
			map.put(a, 100.0);
		
		hitler = data[0];
		int numberFascist = (int) Math.ceil(Utilities.numberPlayers * 0.4) - 1;

		for (int i = numberFascist+1; i < Utilities.numberPlayers; i++)
			map.put("Player_" + i, 0.0);
	}

	public String chooseChancellor() {

		int fascistPolicies = super.getPoliciesFromBoard("Fascist_Policies"); 

		if (fascistPolicies >= 3) 
			return hitler;

		List<String> listOfFas = new ArrayList<>();

		for (Entry<String, Double> entry : map.entrySet()) 
			if (entry.getValue().equals(100.0))
				listOfFas.add(entry.getKey());

		listOfFas.remove(getAID().getLocalName());

		int index = ThreadLocalRandom.current().nextInt(listOfFas.size());

		return listOfFas.get(index);
	}




	public Boolean electionChoice(Double presidentValue, Double chancellorValue) {
		//both are fascists
		if (presidentValue == 100 && chancellorValue == 100)
			return true;

		int fascistPolicies = super.getPoliciesFromBoard("Fascist_Policies"); 
		int liberalPolicies = super.getPoliciesFromBoard("Liberal_Policies"); 

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


	/*public void updateInformation(String chancellorCards, String card) {
		System.out.println("fascist updating stuff");

		//if chancellor fascist
		if (getMap().get(chancellor) == 100)
	}*/




}
