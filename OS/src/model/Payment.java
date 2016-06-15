package model;

public class Payment {
	private int id;
	private boolean paid;
	private float total;
	private String details;
	private PaymentType payTyp;
	//relation
	private Customer custommer;
	private Order order;
}
