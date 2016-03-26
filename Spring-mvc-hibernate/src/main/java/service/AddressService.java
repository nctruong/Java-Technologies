package service;

import java.util.List;

import model.Address;


public interface AddressService {
	Address findById(int id);
	
	void saveAddress(Address address);
	
	void updateAddress(Address address);
	
	void deleteAddressById(int id);

	List<Address> findAllAddresss(); 
	
}
