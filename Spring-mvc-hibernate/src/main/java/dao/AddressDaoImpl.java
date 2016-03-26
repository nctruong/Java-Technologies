package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import model.Address;

@Repository
public class AddressDaoImpl implements AddressDao {
	
	private SessionFactory sessionFactory;

	public AddressDaoImpl(){}
	
	@Override
	public void create(Address address) {
		Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(address);
        tx.commit();
        session.close();
	}

	@Override
	public Address getAddress(Integer id) {
		Session session = getSessionFactory().openSession();
		Address Address = (Address) session.get(Address.class, id);
		session.close();
		return Address;
	}

	@Override
	public List<Address> listAddresss() {
		Session session = getSessionFactory().openSession();
        List<Address> list = session.createQuery("from Address").list();
        session.close();
        return list;
	}

	@Override
	public void delete(Integer id) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Address Address = (Address) session.get(Address.class, id);
		session.delete(Address);
		tx.commit();
		session.close();
	}

	@Override
	public void update(Address address) {
		Session session = getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(address);;
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
