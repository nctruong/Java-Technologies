package controller;

import java.io.IOException;
import java.text.ParseException;

public abstract class Transaction {
	
	public Transaction(){

	}

	public void withDrawal(String name,int money) {}
		
	public void deposit(String name, int money) throws IOException, ParseException {}

	
	public void transfer(int money, String fromCus, String toCus) throws IOException, ParseException {}

	public void changePin(String name, int newPin) throws IOException, ParseException{}
	
	public Customer inquiry(String username) throws IOException, ParseException {}
	
}

