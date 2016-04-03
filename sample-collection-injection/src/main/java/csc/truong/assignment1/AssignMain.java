package csc.truong.assignment1;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AssignMain {

	public AssignMain() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

		Student obj = (Student) context.getBean("student");
		
		System.out.println("Name: " + obj.getName());
		System.out.println("Address: " + obj.getAddress().getAddress());
		
		Map score = obj.getScore();
		
		// How to let Spring know that properties's values are int type or float or ... ?
		float finalScore = (Integer.parseInt((String) score.get("Toan")) 
				+ Integer.parseInt((String) score.get("Ly")) 
				+ Integer.parseInt((String) score.get("Hoa")))/3;
		
		System.out.println("Final Score: " + finalScore);
	}
}
