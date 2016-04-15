package algorithm.sort;



public class AlphaSortingExchange
{
     public static void main(String[ ] args)
     {
             String[ ] names = {"joe", "slim", "ed", "george"};
             sortStringExchange (names);
             for ( int k = 0;  k < 4;  k++ )
                System.out.println( names [ k ] );
      }

      public static void sortStringExchange( String  x [ ] )
      {
            int i, j;
            String temp;

            for ( i = 0;  i < x.length - 1;  i++ )
            {
                for ( j = i + 1;  j < x.length;  j++ )
                {  
                         if ( x [ i ].compareToIgnoreCase( x [ j ] ) > 0 )
                          {                                             // ascending sort
                                      temp = x [ i ];
                                      x [ i ] = x [ j ];    // swapping
                                      x [ j ] = temp; 
                                      
                           } 
                   } 
             } 
      } 
}