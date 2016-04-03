import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import dao.StudentDAO;
import model.Student;

public class MainApp {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-database.xml");
		
		StudentDAO studentDao = (StudentDAO) context.getBean("studentDao");
		
		Student student = new Student();
		student.setFullName("Nguyen Thi Hong");
		student.setSex("F");
		student.setAddress("HCM");
		studentDao.create(student);
		
		List<Student> students = studentDao.listStudents();
		for (Student s : students) {
			System.out.println(s.toString());
		}

		Student s1 = studentDao.getStudent(3);
		System.out.println(s1.toString());
		
		s1.setFullName("X Men");
		studentDao.update(s1);
		
		studentDao.delete(6);
		students = studentDao.listStudents();
		for (Student s : students) {
			System.out.println(s.toString());
		}
	}

}
