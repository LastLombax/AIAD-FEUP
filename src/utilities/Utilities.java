package utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities class
 */
public class Utilities {

	/**
	 * Enum that represents the state/phase of the game
	 */
	public enum State {
		Setup, TeamsInfo, Delegation, Election, ForcedPolicySelection, PolicySelection, GameOver;
	}	
	
	public static State currentState = State.Setup;

	public static String FASCIST = "fascist";
	public static String LIBERAL = "liberal";
	public static String HITLER = "hitler";	
	public static String READY = "READY";
	
	//Ontologies
	public static String TEAMS_INFO= "TeamsInfo";
	public static String REGISTER= "Register";
	public static String PRESIDENT = "President";
	public static String DELEGACY = "Delegacy";
	public static String ELECTION = "Election";
	public static String DISCARD_CARD= "DiscardCard";
	public static String NEW_POLICY = "NewPolicy";
	public static String NEW_POLICY_ELECTION = "NewPolicyElection";
	public static String FASCIST_POLICIES = "Fascist_Policies";
	public static String LIBERAL_POLICIES = "Liberal_Policies";
	public static String ELECTION_BEGIN = "Election_Begin";
	public static String ELECTION_VOTE = "Election_Vote";
	public static String SELECT_FINAL_POLICY = "SelectFinalPolicy";
	public static String SELECTED_POLICY = "SelectedPolicy";
	public static String NEXT_TURN = "NextTurn";
	public static String GAME_OVER = "GameOver";

	
	public static String LIBERAL_CARD = "L";
	public static String FASCIST_CARD = "F";

	public static String FASCISTS_WIN = "Fascists_Win";
	public static String LIBERALS_WIN = "Liberals_Win";
	public static String HITLER_ELECTED = "Hitler_Elected";


	public static int numberPlayers = 7;

	/**
	 * Shuffles an array
	 * @param ar Array of generic object T
	 */
	public static <T> void shuffleArray(T[] ar, T[] ar2)
	{
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.length - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			T a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
			
			T a2 = ar2[index];
			ar2[index] = ar2[i];
			ar2[i] = a2;
		}	    
	}
	
	public static <T> void shuffleArray(T[] ar)
	{
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.length - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			T a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}	    
	}

	/**
	 * Sorts map by value
	 * @param map Map
	 * @return Returns map sorted
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
}
