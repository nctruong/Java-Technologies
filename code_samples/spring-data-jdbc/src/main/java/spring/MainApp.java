package spring;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import spring.dao.StudentJDBCTemplate;
import spring.model.Student;

public class MainApp {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

		StudentJDBCTemplate jdbcTemplate = (StudentJDBCTemplate) context.getBean("studentJDBCTemplate");

		System.out.println("------Records Creation--------");
		jdbcTemplate.create("Nam", "M", "HCM");
		jdbcTemplate.create("Hue", "F", "HCM");

		System.out.println("------Listing Multiple Records--------");
		
		List<Student> students = jdbcTemplate.listStudents();
		for (Student s : students) {
			System.out.println(s.toString());
		}

		System.out.println("----Updating Record with ID = 2 -----");
		jdbcTemplate.update(2, "Ha Noi");

		System.out.println("----Listing Record with ID = 2 -----");
		Student student = jdbcTemplate.getStudent(2);
		System.out.println(student.toString());
	}
}
