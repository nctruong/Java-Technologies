package csc.truong.ATMConsole;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import csc.truong.ATMConsole.helper.FileUtil;

public class Main {
	public static DateFormat formatter ; 
	public static final int NO_ACCOUNT_INFO = 5;
	static FileUtil file = new FileUtil();
	
	public static void main(String[] args) throws IOException, ParseException {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

		ATMScreen atm = (ATMScreen) context.getBean("atmScreen");

		List<Customer> listCustomer;
	    
	// Get list customers
	    listCustomer = (List<Customer>) file.readFromFile("account.txt");
	    atm.setCusList(listCustomer);
	    
	    Customer customer = new Customer();
		Session session = new Session(customer, atm);
		session.startSession();
		/*FileUtil file = new FileUtil();
	    List<Customer> listCustomer;
	    
	    listCustomer = (List<Customer>) file.readFromFile("account.txt");
	    */
	    for(Customer cus : listCustomer){
	    	System.out.print(cus.getName() + " - ");
	    	System.out.print(cus.getPin() + " - ");
	    	System.out.print(cus.getActivated() + " - ");
	    	System.out.print(cus.getAmount() + " - ");
	    	System.out.println(cus.getValidTo());
	    }
	    
	}

}
