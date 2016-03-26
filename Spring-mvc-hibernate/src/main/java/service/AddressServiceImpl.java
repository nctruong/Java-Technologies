package service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dao.AddressDao;
import model.Address;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	@Qualifier("addressDao") AddressDao dao;
	
	public AddressServiceImpl(){}
	
	@Override
	public Address findById(int id) {
		return dao.getAddress(id);
	}

	@Override
	public void saveAddress(Address address) {
		dao.create(address);
	}

	@Override
	public void updateAddress(Address address) {
		dao.update(address);
	}

	@Override
	public void deleteAddressById(int id) {
		dao.delete(id);
	}

	@Override
	public List<Address> findAllAddresss() {
		return dao.listAddresss();
	}

}
