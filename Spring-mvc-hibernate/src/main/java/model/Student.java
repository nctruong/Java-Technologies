package model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

public class Student implements Cloneable {
	private Integer id;

	@Size(min = 3, max = 30)
	private String firstName;

	@Size(min = 3, max = 30)
	private String lastName;

	@Size(min = 1, max = 3)
	private Integer age;

	@Size(min = 1, max = 1)
	private String sex;

	@Size(min = 10, max = 30)
	private String username;

	@Size(min = 10, max = 30)
	private String password;

	private Address address;
	private List<Score> scores = new ArrayList<Score>(0);

	public Student() {
		super();
	}
	
	public Student(Student student){
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Score> getScores() {
		return scores;
	}

	public void setScores(List<Score> scores) {
		this.scores = scores;
	}

	protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	
}
