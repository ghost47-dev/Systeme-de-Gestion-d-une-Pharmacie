package com.gestionpharmacie.model;

import java.sql.ResultSet;
import java.sql.SQLException;

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
    public Product(ResultSet rs){
	try{
	    this.id=rs.getInt("id");
	    this.name=rs.getString("name");
	    this.price=rs.getDouble("price");
	    this.quantity=rs.getInt("quantity");
	}catch(SQLException e){
	    System.out.println(e.getMessage()); 
	}
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
    public void removeQuantity(int q){ // this should throw something probably
	quantity -= q;
    }

    public String toString() {
	return "id: " + id + " - name: " + name + " - quantity: " + quantity;
    }
}
