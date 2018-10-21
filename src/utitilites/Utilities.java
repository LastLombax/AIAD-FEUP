package utitilites;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import jade.core.AID;

import java.util.Arrays; 

public class Utilities {
	
	public static AID[] players;

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
}
