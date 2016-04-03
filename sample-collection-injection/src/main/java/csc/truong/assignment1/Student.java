package csc.truong.assignment1;

import java.util.Map;

public class Student {
	private String name;
	private Map < String, Integer > score;
	private Address address;
	public Student() {
		// TODO Auto-generated constructor stub
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public Map getScore() {
		return score;
	}
	public void setScore(Map score) {
		this.score = score;
	}
	public void printAddress(){
		getAddress().printAddress();
	}
}
