package com.csc.training.hibernate.main;

import org.hibernate.Session;

import com.csc.training.hibernate.model.Student;
import com.csc.training.hibernate.util.HibernateUtil;


public class MainApp {
	public static void main(String[] args) {
		Student student = new Student();
		student.setFullName("Ngo Binh");
		student.setSex("M");
		student.setAddress("Dong Khoi");
		
		//Get Session
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		//start transaction
		session.beginTransaction();
		
		//Save the Model object
		session.save(student);
		
		//Commit transaction
		session.getTransaction().commit();
		
		System.out.println("Employee: " + student.getFullName());
		
		//terminate session factory, otherwise program won't end
		HibernateUtil.getSessionFactory().close();
	}

}
