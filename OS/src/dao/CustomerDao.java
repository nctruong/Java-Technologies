package dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import model.Customer;

public interface CustomerDao extends Serializable{
	
	public Session openCurrentSession();
	
	public void closeCurrentSession();
	/**
	 * This is the method to be used to create a record in the Customer table.
	 */
	public void create(Customer customer);

	/**
	 * This is the method to be used to list down a record from the Customer
	 * table corresponding to a passed Customer id.
	 */
	public Customer getCustomer(Integer id);

	/**
	 * This is the method to be used to list down all the records from the
	 * Customer table.
	 */
	public List<Customer> listCustomers();

	/**
	 * This is the method to be used to delete a record from the Customer table
	 * corresponding to a passed Customer id.
	 */
	public void delete(Integer id);

	/**
	 * This is the method to be used to update a record into the Customer table.
	 */
	public void update(Customer customer);
}
