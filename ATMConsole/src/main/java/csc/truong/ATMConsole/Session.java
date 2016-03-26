package csc.truong.ATMConsole;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;

public class Session {
	// mapping
	private Customer customer;
	private ATMScreen atm;
	
	
	// state
	public static final int READING_PIN     = 1;
	public static final int CHOOSING_TRAN   = 2;
	public static final int PERFORMING_TRAN = 3;
	public static final int EXITING			= 4;
	
	private int state;
	private int action;
	
	public Session(Customer customer, ATMScreen atm){
		this.customer = customer;
		this.atm      = atm;
		state    	  = READING_PIN;
	}
	
	public void startSession() throws IOException, ParseException{
		Customer cus = new Customer();
		while (state != EXITING){
	            switch(state) {
	            	case READING_PIN:
	            		Map<String, String> loginInfo = atm.requestLoginInfo();
	            		cus = new Customer(loginInfo.get("username"), Integer.parseInt(loginInfo.get("pin")));
	            		if (atm.authenticateCustomer(cus)){
	            			if (atm.verifyStatusAccount(cus)){
	            				state = CHOOSING_TRAN;
	            			} else {
	            				System.out.println("Enter to exit !");
	            				atm.getInput().next();
	            				System.exit(0);	            				
	            			}
	            		} 	            		
	            		break;
	            	case CHOOSING_TRAN:
	            		atm.inquiryCurrentAccountInfo(cus.getName());
	            		atm.showMenuAction();
	            		action = atm.getActionMenu();
	            		state  = PERFORMING_TRAN;
	            		break;
	            	case PERFORMING_TRAN:
	            		atm.inquiryCurrentAccountInfo(cus.getName());
	            		switch (action){
	            			case 1:
	               				atm.withDrawal(cus.getName());
	               				atm.inquiryCurrentAccountInfo(cus.getName());	               			    				
	            				break;
	            			case 2:
	            				atm.deposit(cus.getName());
	            				atm.inquiryCurrentAccountInfo(cus.getName());	         
	            				break;
	            			case 3:
	            				atm.transfer(cus.getName());		
	            				break;
	            			case 4:
	            				atm.changePin(cus.getName());
	            				break;
	            			case 5:
	            				atm.exit();
	            		}
	            		
	            		System.out.println("Do you want to continue ? (y/n)");
           				String res = atm.getInput().next();
	            		if (res.equals("y") || res.equals("Y")){
           					state = CHOOSING_TRAN;
           				} else if (res.equals("n") || res.equals("N")) {
           					action = 5;
           				} 
	            		break;

	            }
		}
		
		
	}
}
