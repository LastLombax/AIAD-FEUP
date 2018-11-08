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
<<<<<<< HEAD




	public String hitler;
=======
	public AID hitler;
>>>>>>> branch 'master' of https://github.com/LastLombax/AIAD-FEUP

	public void setup() {
		super.setup();	
		super.type = "fascist";
		System.out.println(getAID().getLocalName() + ": " + type);
	}

	public void registerFascists(String fascists) {
<<<<<<< HEAD
		
		String[] data = fascists.split(";");
		for (String a : data)
			map.put(a, 100.0);
		
		hitler = data[0];
		int numberFascist = (int) Math.ceil(Utilities.numberPlayers * 0.4) - 1;

		for (int i = numberFascist+1; i < Utilities.numberPlayers; i++)
			map.put("Player_" + i, 0.0);
=======
		for (int i = 0, n = fascists.length(); i < n; i++) {
			int fas = Integer.parseInt(String.valueOf(fascists.charAt(i)));
			getMap().put(Utilities.players[fas], 100.0);
		}
		hitler = Utilities.players[fascists.length()];
		for (int i = fascists.length(); i < Utilities.players.length; i++) {
			getMap().put(Utilities.players[i], 0.0);
		}			
>>>>>>> branch 'master' of https://github.com/LastLombax/AIAD-FEUP
	}

<<<<<<< HEAD
	public String chooseChancellor() {

		int fascistPolicies = super.getPoliciesFromBoard("Fascist_Policies"); 

=======
	public AID chooseChancellor() {
		int fascistPolicies = super.getPoliciesFromBoard(Utilities.FASCIST_POLICIES); 
>>>>>>> branch 'master' of https://github.com/LastLombax/AIAD-FEUP
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
