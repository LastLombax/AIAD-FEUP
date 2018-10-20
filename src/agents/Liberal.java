package agents;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class Liberal extends Player{
	
	public void setup() {
		super.setup();
	}
	
	public String selectCardToDiscard(String cards) {
		System.out.println("Card liberal");
		int countFascists = cards.length() - cards.replaceAll("F","").length();
		int countLiberals = cards.length() - cards.replaceAll("L","").length();
		if(countFascists == 3 || countLiberals == 3) 
			cards = cards.substring(2);
		
		else if (countFascists == 2 && countLiberals == 1){
			int indexF = cards.indexOf('F');
			String aux = cards.substring(indexF, indexF + 2);
			cards = cards.replaceFirst(aux, "");
		}
		
		else if (countLiberals == 2 && countFascists == 1) 
			cards = cards.replace("F_", "");
			
		return cards;
	}
	
	public int chooseChancellor() {
		return -1;
	}

	public void selectCardToPass() {}
}
