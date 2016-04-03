package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import model.Student;

@Repository
public class StudentDaoImpl implements StudentDao {
	
	private SessionFactory sessionFactory;

	@Override
	public void create(Student student) {
		Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(student);
        tx.commit();
        session.close();
	}

	@Override
	public Student getStudent(Integer id) {
		Session session = getSessionFactory().openSession();
		Student student = (Student) session.get(Student.class, id);
		session.close();
		return student;
	}

	@Override
	public List<Student> listStudents() {
		Session session = getSessionFactory().openSession();
        List<Student> list = session.createQuery("from Student").list();
        session.close();
        return list;
	}

	@Override
	public void delete(Integer id) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Student student = (Student) session.get(Student.class, id);
		session.delete(student);
		tx.commit();
		session.close();
	}

	@Override
	public void update(Student student) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(student);;
		tx.commit();
		session.close();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
