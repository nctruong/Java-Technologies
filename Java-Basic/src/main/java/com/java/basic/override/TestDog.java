package com.java.basic.override;
class Animal{

	   public void move(){
	      System.out.println("Animals can move");
	   }
	}

	class Dog extends Animal{

	   public void move(){
//01. invokes the super class method
	      super.move(); 
	      System.out.println("Dogs can walk and run");
	   }
	}

	public class TestDog{

	   public static void main(String args[]){
//02. Animal reference but Dog object
	      Animal b = new Dog(); 
//03. Runs the method in Dog class
	      b.move(); 

	   }
	}