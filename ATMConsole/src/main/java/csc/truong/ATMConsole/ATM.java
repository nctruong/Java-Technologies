package csc.truong.ATMConsole;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import csc.truong.ATMConsole.helper.FileUtilCommon;

// ATM Background Process
public class ATM {
	// mapping
	private FileUtilCommon f;

	private int smallestAmount;
	private int numCustomer;

	private static DateFormat formatter;
	private Map<String, String> message;
	protected List<String> menu;
	protected List<Customer> cusList = new ArrayList<Customer>();
	private Transaction trans;

	public ATM() throws IOException {
		f = new FileUtilCommon();
		try {
			this.cusList = this.getListCustomer();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<Customer> getListCustomer() throws IOException, ParseException {

		List<Customer> listCustomer = new ArrayList<Customer>();
		List<String> DataLines = f.getListLine();
		for (String DataLine : DataLines) {
			Customer customer = new Customer();
			String line[] = new String[numCustomer];
			line = DataLine.split(",");

			customer.setName(line[0]);
			customer.setPin(Integer.parseInt(line[1]));
			customer.setActivated(Integer.parseInt(line[2]));
			customer.setAmount(Float.parseFloat(line[3]));
			customer.setValidTo(line[4]);
			listCustomer.add(customer);
		}

		return (ArrayList<Customer>) listCustomer;
	}

	public Map<String, Customer> getMapCustomer() throws IOException, ParseException {
		List<Customer> customers = this.getListCustomer();
		Map<String, Customer> map = new HashMap<String, Customer>();
		for (Customer customer : customers) {
			map.put(customer.getName(), customer);
		}

		return map;
	}

	// Username is unique
	public Customer getCustomerByName(String name) throws IOException, ParseException {
		Customer customer = this.getMapCustomer().get(name);
		return customer;
	}

	public void updateCustomerInfo(Customer current, Customer newCus) throws IOException {
		String currentLine = current.getName() + "," + current.getPin() + "," + current.getActivated() + ","
				+ current.getAmount() + "," + current.getValidTo();

		String newLine = newCus.getName() + "," + newCus.getPin() + "," + newCus.getActivated() + ","
				+ newCus.getAmount() + "," + newCus.getValidTo();
		
		f.replace(currentLine, newLine);
	}

	public Map<String, String> getMessage() {
		return message;
	}

	public void setMessage(Map<String, String> message) {
		this.message = message;
	}

	public List<Customer> getCusList() {
		return cusList;
	}

	public void setCusList(List<Customer> cusList) {
		this.cusList = cusList;
	}

	public boolean authenticateCustomer(Customer customer) {

		boolean flag = false;
		for (Customer cus : cusList) {
			if (customer.getName().equals(cus.getName()) && customer.getPin() == cus.getPin()) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public List<String> getMenu() {
		return menu;
	}

	public void setMenu(List<String> menu) {
		this.menu = menu;
	}

	public FileUtilCommon getF() {
		return f;
	}

	public void setF(FileUtilCommon f) {
		this.f = f;
	}

	public Transaction getTrans() {
		return trans;
	}

	public void setTrans(Transaction trans) {
		this.trans = trans;
	}

	public int getSmallestAmount() {
		return smallestAmount;
	}

	public void setSmallestAmount(int smallestAmount) {
		this.smallestAmount = smallestAmount;
	}

	public int getNumCustomer() {
		return numCustomer;
	}

	public void setNumCustomer(int numCustomer) {
		this.numCustomer = numCustomer;
	}

	/*
	 * public void addCustomerList(String username, int pin){ Customer cus = new
	 * Customer(username, pin); cusList.add(cus); }
	 */

}
