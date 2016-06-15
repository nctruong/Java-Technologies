package model;

public class Item {
	private int quantity;
	private float unitCost;
	private float subtotal;
	//relation
	private Product product;
	private Cart cart;
	private OrderDetail orderDetail;
}
