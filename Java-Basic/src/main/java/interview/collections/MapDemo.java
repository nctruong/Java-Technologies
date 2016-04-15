package interview.collections;

import java.util.HashMap;
import java.util.Map;
/*
 * unique keys
 * random when printing
 */
public class MapDemo {
	public static void main(String[] args) {
		try{
	      Map m1 = new HashMap(); 
	      m1.put("Zara", "8");
	      m1.put("Mahnaz", "31");
	      m1.put("Ayan", "12");
	      // duplicate key
	      m1.put("Daisy", "14");
	      m1.put("Daisy", "15");
	      System.out.println();
	      System.out.println(" Map Elements");
	      System.out.print("\t" + m1);
		}catch(Exception e){
			System.out.println("Exception e: " + e);
		}
	}
}
 