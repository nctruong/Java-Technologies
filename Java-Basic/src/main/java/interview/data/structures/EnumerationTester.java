package interview.data.structures;
import java.util.Vector;
import java.util.Enumeration;
/*
 * The Enumeration interface defines the methods by which you can enumerate (obtain one at a time) 
 * the elements in a collection of objects.
 */
public class EnumerationTester {

   public static void main(String args[]) {
      Enumeration days;
      Vector dayNames = new Vector();
      dayNames.add("Sunday");
      dayNames.add("Monday");
      dayNames.add("Tuesday");
      dayNames.add("Wednesday");
      dayNames.add("Thursday");
      dayNames.add("Friday");
      dayNames.add("Saturday");
      days = dayNames.elements();
      /* hasMoreElements(): When implemented, it must return true while there are still more elements to extract, 
      and false when all the elements have been enumerated.*/
      while (days.hasMoreElements()){
         System.out.println(days.nextElement()); 
      }
   }
}