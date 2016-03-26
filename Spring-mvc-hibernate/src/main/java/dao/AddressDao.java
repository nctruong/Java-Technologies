package dao;

import java.io.Serializable;
import java.util.List;

import model.Address;

public interface AddressDao extends Serializable{

	/**
	 * This is the method to be used to create a record in the Address table.
	 */
	public void create(Address address);

	/**
	 * This is the method to be used to list down a record from the Address
	 * table corresponding to a passed Address id.
	 */
	public Address getAddress(Integer id);

	/**
	 * This is the method to be used to list down all the records from the
	 * Address table.
	 */
	public List<Address> listAddresss();

	/**
	 * This is the method to be used to delete a record from the Address table
	 * corresponding to a passed Address id.
	 */
	public void delete(Integer id);

	/**
	 * This is the method to be used to update a record into the Address table.
	 */
	public void update(Address address);
}
