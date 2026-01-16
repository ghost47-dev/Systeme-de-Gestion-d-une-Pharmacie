package com.gestionpharmacie.model;

public class Product {
    private int id;
    private String name;
	private double price;
    private int quantity;

    public Product(int id, String name, double price, int quant){
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quant;
    }

	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getPrice() {
		return price;
	}
    public void addQuantity(int q){
        quantity += q;
    }
}
