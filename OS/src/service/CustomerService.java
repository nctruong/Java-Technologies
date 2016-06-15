package service;

import java.util.List;


import model.Customer;


public interface CustomerService {
	Customer findById(int id);
	
	void saveCustomer(Customer customer);
	
	void updateCustomer(Customer customer);
	
	void deleteCustomerById(int id);

	List<Customer> findAllCustomers(); 
	
}
