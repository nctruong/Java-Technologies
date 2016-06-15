package com.java.basic.cover;

public class Worker extends Client {
	private int id;
	private String name;
	private int age;
	public Worker(int id, String name, int age) {
		super(id, name, age);
		// TODO Auto-generated constructor stub
		this.id = id;
		this.name = name;
		this.age = age;
	}


	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}


	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
	}


	@Override
	public int getAge() {
		// TODO Auto-generated method stub
		return age;
	}


	@Override
	public void setAge(int age) {
		// TODO Auto-generated method stub
		this.age = age;
	}
	
}
