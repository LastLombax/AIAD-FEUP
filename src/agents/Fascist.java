package agents;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Fascist extends Player{
	
	public void setup() {
		super.setup();	
	}
	
	public String selectCardToDiscard(String cards) {
		System.out.println("Card fascist");
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
	
	public int chooseChancellor() {
		return -1;
	}

	public void selectCardToPass() {}

}
