package agents;

import utitilites.Utilities;

public class Hitler extends Player {
	
	public void setup() {
		super.setup();
		registerPlayers();
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
	
	public void registerPlayers() {
		for (int i = 0; i < Utilities.players.length; i++)
			getMap().put(Utilities.players[i], 0.0);

	}
	
	public int chooseChancellor() {
		return -1;
	}


	public void selectCardToPass() {}

}
