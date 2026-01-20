package com.gestionpharmacie.managers;

import com.gestionpharmacie.exceptions.InsufficientStockException;
import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.PropertyPermission;

public class ProductManager {
    private Connection connection;
    private final int quantityRiskThreshold = 10;

    public ProductManager (Connection connection) {
	this.connection = connection;
    }

    public int addProduct(String name, double price, int quant) throws SQLException{
	String sql = "INSERT INTO product(name, price, quantity) VALUES(?, ?, ?)";
	PreparedStatement stmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
	stmt.setString(1, name);
	stmt.setDouble(2, price);
	stmt.setInt(3, quant);
	stmt.executeUpdate();

	ResultSet keys = stmt.getGeneratedKeys();
	//if keys is empty then it trows an exception
	return keys.getInt(1);
    }

    public Product fetchProduct(int id) throws ProductNotFoundException{
	try{
	    String sql = "SELECT * FROM product WHERE id = ?";
	    PreparedStatement stmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
	    stmt.setInt(1, id);
	    ResultSet rs = stmt.executeQuery();

	    if (rs.next()) {
		return new Product(
			rs.getInt("id"),
			rs.getString("name"),
			rs.getDouble("price"),
			rs.getInt("quantity")
			);
	    }else{
		throw new ProductNotFoundException();
	    }
	}catch (SQLException e){
	    System.err.println("Error: " + e.getMessage());
	}
	//so that the compiler doesn't screem at you
	return null;
    }

    public void updateProduct(int id, String newName, double newPrice, int newQuantity) throws ProductNotFoundException,SQLException {
	String sql = "UPDATE product SET name = ?, price = ?, quantity = ? WHERE id = ?";
	PreparedStatement stmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
	stmt.setString(1, newName);
	stmt.setDouble(2, newPrice);
	stmt.setInt(3, newQuantity);
	stmt.setInt(4, id);

	int rows = stmt.executeUpdate();
	if (rows == 0) throw new ProductNotFoundException("This product doesn't exist!");
    }

    public void deleteProduct(int id) throws ProductNotFoundException {
	String sql = "DELETE FROM product WHERE id = ?";
	try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	    stmt.setInt(1, id);

	    int rows = stmt.executeUpdate();
	    if (rows == 0) throw new ProductNotFoundException("This product doesn't exist!");
	} catch (SQLException e) {
	    System.err.println("Error: " + e.getMessage());
	}
    }

    public void addToProduct(int id, int quant) throws ProductNotFoundException,InsufficientStockException{
	try{
	    removeFromProduct(id,-quant);
	}catch(InsufficientStockException e){
	    System.out.println("impossible");
	}
    }

    //the multiple try catch are uneeded 
    public void removeFromProduct(int id, int quant) throws ProductNotFoundException, InsufficientStockException {
	try{
	    Product p = fetchProduct(id);
	    String sql = "SELECT quantity FROM product WHERE id = ?";
	    PreparedStatement stmt = connection.prepareStatement(sql);
	    stmt.setInt(1, id);

	    ResultSet rs = stmt.executeQuery();
	    if (!rs.next()) {
		throw new ProductNotFoundException("This product doesn't exist!");
	    }
	    if (rs.getInt("quantity") < quant){
		throw new InsufficientStockException("Insufficient stock for " + p.getName() + "only " + rs.getInt("quantity") + " units left");
	    }
	    sql = "UPDATE product SET quantity = quantity - ? WHERE id = ?";
	    stmt = connection.prepareStatement(sql) ;
	    stmt.setInt(1, quant);
	    stmt.setInt(2, id);

	    //we have already check for the products existants
	    stmt.executeUpdate();
	} catch (SQLException e) {
	    System.err.println("Error: " + e.getMessage());
	}
    }
    public ArrayList<Product> viewStock() {
	ArrayList<Product> products = new ArrayList<>();
	String sql = "SELECT * FROM product";
	try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	    ResultSet rs = stmt.executeQuery();
	    while (rs.next()) {
		products.add(new Product(
			    rs.getInt("id"),
			    rs.getString("name"),
			    rs.getDouble("price"),
			    rs.getInt("quantity")
			    ));
	    }
	} catch (SQLException e) {
	    System.err.println("Error: " + e.getMessage());;
	}
	return products;
    }

    public ArrayList<Product> lowStockAlert() {
	ArrayList<Product> products = new ArrayList<>();
	String sql = "SELECT * FROM product WHERE quantity < ?";
	try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	    stmt.setInt(1, quantityRiskThreshold);
	    ResultSet rs = stmt.executeQuery();
	    while (rs.next()) {
		products.add(new Product(
			    rs.getInt("id"),
			    rs.getString("name"),
			    rs.getDouble("price"),
			    rs.getInt("quantity")
			    ));
	    }
	} catch (SQLException e) {
	    System.err.println("Error: " + e.getMessage());
	}
	return products;
    }
}
