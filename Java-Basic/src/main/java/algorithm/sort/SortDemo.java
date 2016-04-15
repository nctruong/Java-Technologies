package algorithm.sort;

import java.awt.List;
import java.util.ArrayList;
import java.util.Enumeration;

public class SortDemo {
	public static void BubbleSort( int [ ] num )
	{
	     int j;
	     boolean flag = true;   // set flag to true to begin first pass
	     int temp;   //holding variable

	     while ( flag )
	     {
	            flag= false;    //set flag to false awaiting a possible swap
	            for( j=0;  j < num.length -1;  j++ )
	            {
	                   if ( num[ j ] < num[j+1] )   // change to > for ascending sort
	                   {
	                           temp = num[ j ];                //swap elements
	                           num[ j ] = num[ j+1 ];
	                           num[ j+1 ] = temp;
	                          flag = true;              //shows a swap occurred  
	                  } 
	            } 
	      } 
	} 
//The exchange sort compares the first element with each following element of the array, making any necessary swaps.
/*Array at beginning: 	84	69	76	86	94	91
		After Pass #1:	94	69	76	84	86	91
		After Pass #2:	94	91	69	76	84	86
		After Pass #3: 	94	91	86	69	76	84
		After Pass #4: 	94	91	86	84	69	76
After Pass #5 (done): 	94	91	86	84	76	69*/
	public static void ExchangeSort ( int [ ] num )
	{
	     int i, j, temp;  //be sure that the temp variable is the same "type" as the array
	     for ( i = 0; i < num.length - 1; i ++ )  
	     {
	          for ( j = i + 1; j < num.length; j ++ )
	          {
	               if( num[ i ] < num[ j ] )         //sorting into descending order
	               {
	                       temp = num[ i ];   //swapping
	                       num[ i ] = num[ j ];
	                       num[ j ] = temp; 
	                }           
	          }
	     }
	}
/*Array at beginning: 	84	69	76	86	94	91      /
		After Pass #1:	84	91	76	86	94	69     / choose min value for last array trong n phan tu
		After Pass #2:	84	91	94	86	76	69    / choose min value for n-1 trong n-1 phan tu
		After Pass #3: 	86	91	94	84	76	69   / choose min value for n-2 trong n-2 phan tu
		After Pass #4: 	94	91	86	84	76	69  / choose min value for n-3 trong n-3 phan tu
After Pass #5 (done): 	94	91	86	84	76	69 /*/
	
	public static void SelectionSort ( int [ ] num )
	{
	     int i, j, first, temp;  
	     for ( i = num.length - 1; i > 0; i-- )  
	     {
	          first = 0;   //initialize to subscript of first element
	          for(j = 1; j <= i; j ++)   //locate smallest element between positions 1 and i.
	          {
	               if( num[ j ] < num[ first ] )         
	                 first = j;
	          }
	          temp = num[ first ];   //swap smallest found with element in position i.
	          num[ first ] = num[ i ];
	          num[ i ] = temp; 
	      }           
	}

	/*Array at beginning: 

		84	69	76	86	94	91 = 1st sub-array
		84	69	76	86	94	91 = 2nd sub-array
		84	69	76	86	94	91
		84	76	69	86	94	91
		86	84	76	69	94	91
		94	86	84	76	69	91 2nd sub-array empty 
		94	91	86	84	76	69 */
	public static void InsertionSort( int [ ] num)
	{
	     int j;                     // the number of items sorted so far
	     int key;                // the item to be inserted
	     int i;  

	     for (j = 1; j < num.length; j++)    // Start with 1 (not 0)
	    {
	           key = num[ j ];
	           for(i = j - 1; (i >= 0) && (num[ i ] < key); i--)   // Smaller values are moving up
	          {
	                 num[ i+1 ] = num[ i ];
	          }
	         num[ i+1 ] = key;    // Put the key in its proper location
	     }
	}
	
	
	
	public static void main(String arg[]){
		int[] num = {12,89,58,69,12,3,5,4,8,7,9,25,68,741,25,92,29,38};
		SortDemo demo = new SortDemo();
		demo.BubbleSort(num);
		System.out.println("Elements: ");
		for( int j=0;  j < num.length -1;  j++ )
			System.out.print(num[j] + ", ");
	
		
		
	}
}
