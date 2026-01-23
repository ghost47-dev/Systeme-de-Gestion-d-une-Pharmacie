package com.gestionpharmacie.model;

public class ShipmentGood {
    private int id;
    private int shipmentId;
	private int productId;
    private double price;
	private int quantity;

    public ShipmentGood(int id, int sid, int pid, double p, int q){
        this.id = id;
        shipmentId = sid;
        productId = pid;
        price = p;
        quantity = q;
    }

    public int getId() {
		return id;
	}

    public int getProductId() {
		return productId;
	}

    public int getQuantity() {
		return quantity;
	}

    public double getPrice(){
        return price;
    }
}

