/**
 * 
 */
package csc.truong.ATMConsole;

import java.util.Date;

/**
 * @author truong
 *
 */
public class Customer {
	
	private String name;
	private int pin;
	private int activated;
	private float amount;
	private String validTo;
	
	public Customer(){}
	public Customer(String name,int pin) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.pin  = pin;
	}
	
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public int getActivated() {
		return activated;
	}

	public void setActivated(int activated) {
		this.activated = activated;
	}

	
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public String getValidTo() {
		return validTo;
	}
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}

	public void setProperties(Customer cus){
		this.name		= cus.getName();
		this.pin		= cus.getPin();
		this.activated	= cus.getActivated();
		this.amount		= cus.getAmount();
		this.validTo	= cus.getValidTo();
	}
	
	
}
