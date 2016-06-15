package model;

import java.util.List;

import javax.persistence.Column;

import org.hibernate.annotations.Entity;
import org.hibernate.annotations.Table;

@Entity
@Table(appliesTo = "CUSTOMERS")
public class Customer extends User {
	private String name;
	private String address;
	private String email;
	private String creditCardNum;
	private String phone;
	
	//private List Cart;
	//private List Order;
	//private List Payment;
	
	@Column(name="NAME", nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name="CREDITCARDNUM", nullable=false)
	public String getCreditCardNum() {
		return creditCardNum;
	}
	public void setCreditCardNum(String creditCardNum) {
		this.creditCardNum = creditCardNum;
	}
/*	@Column(name="CART", nullable=false)
	public List getCart() {
		return Cart;
	}
	public void setCart(List cart) {
		Cart = cart;
	}
	public List getOrder() {
		return Order;
	}
	public void setOrder(List order) {
		Order = order;
	}
	public List getPayment() {
		return Payment;
	}
	public void setPayment(List payment) {
		Payment = payment;
	}*/
	@Column(name="ADDRESS", nullable=false)
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Column(name="EMAIL", nullable=false)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Column(name="PHONE", nullable=false)
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
