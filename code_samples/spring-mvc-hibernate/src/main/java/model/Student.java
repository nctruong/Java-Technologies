package model;

import javax.validation.constraints.Size;

public class Student {
	private int id;
	
	@Size(min=3, max=50)
	private String fullName;
	
	@Size(min=1, max=1)
	private String sex;
	
	@Size(min=10, max=100)
	private String address;

	public Student() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Student [id=" + id + ", fullName=" + fullName + ", sex=" + sex + ", address=" + address + "]";
	}
}
