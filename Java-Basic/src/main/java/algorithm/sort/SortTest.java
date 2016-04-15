package algorithm.sort;
// short demo to show built-in sorting routine at work
import java.io.*;
import java.util.Arrays;  //notice the new required import statement
public class SortTest
{
   public static void main(String[] args)
   {
      //Sorting integers
      int[ ] nums = {12, 18, 7, 20, 11};
      Arrays.sort(nums);    //use the built-in sorting routine
      for (int i = 0; i < 5; i++)
         System.out.println(nums[ i ]);

      //Sorting strings
      String[ ] people = {"Wally", "Henry", "Felix", "George"};
      Arrays.sort(people);    //use the built-in sorting routine
      for (int i = 0; i < 4; i++)
         System.out.println(people[ i ]);
   }
}