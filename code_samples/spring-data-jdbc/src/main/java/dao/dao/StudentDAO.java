package dao.dao;

import java.util.List;

import dao.model.Student;


public interface StudentDAO {
	/**
	 * This is the method to be used to create a record in the Student table.
	 */
	public void create(String fullName, String sex, String address);

	/**
	 * This is the method to be used to list down all the records from the
	 * Student table.
	 */
	public List<Student> listStudents();

}
