package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dao.StudentDao;
import dao.StudentDaoImpl;
import model.Student;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	@Qualifier("studentDao") private StudentDao dao;
	//private static StudentDaoImpl dao;
	public StudentServiceImpl(){
		//dao = new StudentDaoImpl();
	}
	
	@Override
	public Student findById(int id) {
		Student student = dao.getStudent(id);
		return student;
	}

	@Override
	public void saveStudent(Student student) {
		dao.create(student);
	}

	@Override
	public void updateStudent(Student student) {
		dao.update(student);
	}

	@Override
	public void deleteStudentById(int id) {
		dao.delete(id);
	}

	@Override
	public List<Student> findAllStudents() {
		return dao.listStudents();
	}

}
