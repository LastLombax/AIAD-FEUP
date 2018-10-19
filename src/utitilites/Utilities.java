package utitilites;
 
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import jade.core.AID;

import java.util.Arrays; 

public class Utilities {

	public static void shuffleArray(AID[] ar)
	  {
	    Random rnd = ThreadLocalRandom.current();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      AID a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }	    
	  }
}
