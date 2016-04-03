package com.java.basic.inner;
/* Inner can access Outer end else
 * Security reason: many class in one file class. 
 */
class Outer_Demo{
   //private variable of the outer class
   private int num= 175;
   //inner class
   private class Inner_Demo{
      public void print(){	   
         System.out.println("This is an inner class");
         Outer_Demo out = new Outer_Demo();
         out.display_Outer();
      }
      
      public int getNum(){
          return num;
      }
   }
   //Accessing he inner class from the method within
   void display_Inner(){
      Inner_Demo inner = new Inner_Demo();
      inner.print();
   }
   
   void dislayInnerNum(){
	  Inner_Demo inner = new Inner_Demo();
	  System.out.println("This is the getnum method of the inner class: " + inner.getNum());
   }
   void display_Outer(){
	   System.out.println("This is an outer class");
   }
}
   
public class My_class{
   public static void main(String args[]){
      //Instantiating the outer class 
      Outer_Demo outer = new Outer_Demo();
      //Accessing the display_Inner() method.
      outer.display_Inner();
      outer.dislayInnerNum();
   }

}