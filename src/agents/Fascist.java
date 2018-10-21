package agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utitilites.Utilities;

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
	
	public int chooseChancellor() {
		//request number of fascist policies from board
		
		//if (num >= 3) select Hitler
		// else select other
		
		return -1;
	}

	public void selectCardToPass() {}

}
