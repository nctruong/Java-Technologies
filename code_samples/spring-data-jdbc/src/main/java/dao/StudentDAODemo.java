package dao;

import java.util.List;

import dao.dao.StudentDAO;
import dao.dao.StudentDAOImpl;
import dao.model.Student;

public class StudentDAODemo {

	public static void main(String[] args) {
		
		StudentDAO dao = new StudentDAOImpl();
		dao.create("Tran Hao", "M", "Cong Hoa");
		
		
		List<Student> list = dao.listStudents();
		for (Student student : list) {
			System.out.println(student.toString());
		}
		
	}

}
