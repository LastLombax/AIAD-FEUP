package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import jade.core.AID;
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

		int fascistPolicies = super.getPoliciesFromBoard("Fascist_Policies"); 

		if (fascistPolicies >= 3) 
			return hitler;

		List<AID> listOfFas = new ArrayList<>();

		for (Entry<AID, Double> entry : map.entrySet()) 
			if (entry.getValue().equals(100.0) && entry.getKey() != getAID())
				listOfFas.add(entry.getKey());


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
