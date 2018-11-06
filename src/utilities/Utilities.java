package utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import jade.core.AID;


public class Utilities {

	public enum State {
		Setup, Ready, Delegation, Election, PolicySelection;
	}
	
	public static State currentState = State.Setup;


	public static AID[] players;
	public static int numberPlayers = 7;

	/**
	 * Shuffles an array
	 * @param ar Array of generic object T
	 */
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
