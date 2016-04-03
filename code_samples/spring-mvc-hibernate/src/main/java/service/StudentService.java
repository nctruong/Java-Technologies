package service;

import java.util.List;

import model.Student;


public interface StudentService {
	Student findById(int id);
	
	void saveStudent(Student student);
	
	void updateStudent(Student student);
	
	void deleteStudentById(int id);

	List<Student> findAllStudents(); 
	
}
