package agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import utilities.Utilities;

public class Fascist extends Player{
	public AID hitler;

	public void setup() {
		super.setup();	
		super.type = "fascist";
		System.out.println(getAID().getLocalName() + ": " + type);
		registerFascists();
	}

	public void registerFascists() {
		String fascists = "";
		for (int i = 0; i < (int) Math.ceil(Utilities.players.length * 0.4) - 1; i++) {
			fascists += i;
		}
		for (int i = 0, n = fascists.length(); i < n; i++) {
			int fas = Integer.parseInt(String.valueOf(fascists.charAt(i)));
			getMap().put(Utilities.players[fas], 100.0);
		}
		hitler = Utilities.players[fascists.length()];
		for (int i = fascists.length(); i < Utilities.players.length; i++) {
			getMap().put(Utilities.players[i], 0.0);
		}			
	}

	public AID chooseChancellor() {
		int fascistPolicies = super.getPoliciesFromBoard(Utilities.FASCIST_POLICIES); 
		if (fascistPolicies >= 3) 
			return hitler;

		List<AID> listOfFas = new ArrayList<>();

		for (Entry<AID, Double> entry : map.entrySet()) {
			if (entry.getValue().equals(100.0) && entry.getKey() != getAID())
				listOfFas.add(entry.getKey());
		}
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
	
	
	/*public void updateInformation(String chancellorCards, String card) {
		System.out.println("fascist updating stuff");
		
		//if chancellor fascist
		if (getMap().get(chancellor) == 100)
	}*/
	
	
	

}
