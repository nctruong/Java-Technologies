package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

//import com.esotericsoftware.kryo.Kryo;

import model.Customer;

@Repository
public class CustomerDaoImpl implements CustomerDao {
	
	private SessionFactory sessionFactory;
	
	public CustomerDaoImpl(){}
	
	@Override
	public void create(Customer customer) {
		Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(customer);
        tx.commit();
        session.close();
	}

	@Override
	public Customer getCustomer(Integer id) {
		Session session = getSessionFactory().openSession();
		Customer Customer	= (Customer) session.get(Customer.class, id);
		session.close();
		return Customer;
	}

	@Override
	public List<Customer> listCustomers() {
		Session session = getSessionFactory().openSession();
        List<Customer> list = session.createQuery("from CUSTOMERS").list();
        session.close();
        return list;
	}

	@Override
	public void delete(Integer id) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Customer customer = (Customer) session.get(Customer.class, id);
		session.delete(customer);
		tx.commit();
		session.close();
	}

	@Override
	public void update(Customer customer) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(customer);;
		tx.commit();
		session.close();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Session openCurrentSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeCurrentSession() {
		// TODO Auto-generated method stub
		
	}
}
