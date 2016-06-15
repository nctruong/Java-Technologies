package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dao.CustomerDao;
import dao.CustomerDaoImpl;
import model.Customer;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired @Qualifier("CustomerDao") private CustomerDao dao;
	public CustomerServiceImpl(){}
	
	@Override
	public Customer findById(int id) {
		Customer Customer = dao.getCustomer(id);
		return Customer;
	}

	@Override
	public void saveCustomer(Customer Customer) {
		dao.create(Customer);
	}

	@Override
	public void updateCustomer(Customer Customer) {
		dao.update(Customer);
	}

	@Override
	public void deleteCustomerById(int id) {
		dao.delete(id);
	}

	@Override
	public List<Customer> findAllCustomers() {
		return dao.listCustomers();
	}

}
