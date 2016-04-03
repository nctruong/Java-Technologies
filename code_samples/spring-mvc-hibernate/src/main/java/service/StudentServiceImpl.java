package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dao.StudentDao;
import model.Student;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	@Qualifier("studentDao")
	private StudentDao dao;
	
	@Override
	public Student findById(int id) {
		return dao.getStudent(id);
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
