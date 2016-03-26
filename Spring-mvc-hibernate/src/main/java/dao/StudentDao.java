package dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import model.Student;

public interface StudentDao extends Serializable{
	
	public Session openCurrentSession();
	
	public void closeCurrentSession();
	/**
	 * This is the method to be used to create a record in the Student table.
	 */
	public void create(Student student);

	/**
	 * This is the method to be used to list down a record from the Student
	 * table corresponding to a passed student id.
	 */
	public Student getStudent(Integer id);

	/**
	 * This is the method to be used to list down all the records from the
	 * Student table.
	 */
	public List<Student> listStudents();

	/**
	 * This is the method to be used to delete a record from the Student table
	 * corresponding to a passed student id.
	 */
	public void delete(Integer id);

	/**
	 * This is the method to be used to update a record into the Student table.
	 */
	public void update(Student student);
}
