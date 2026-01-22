package com.gestionpharmacie.managers;

import com.gestionpharmacie.exceptions.InsufficientStockException;
import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.model.Product;

import java.sql.*;
import java.util.ArrayList;

public class ProductManager {
    private Connection connection;
    private final int quantityRiskThreshold = 10;

    public ProductManager (Connection connection) {
        this.connection = connection;
    }

    public int addProduct(String name, double price, int quant) {
        String sql = "INSERT INTO product(name, price, quantity) VALUES(?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, quant);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public Product fetchProduct(int id) throws ProductNotFoundException{
        String sql = "SELECT * FROM product WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Product(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        throw new ProductNotFoundException("this product does not exist !");
    }
    public Product fetchProduct(String name) throws ProductNotFoundException {
        String sql = "SELECT * FROM product WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Product(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        throw new ProductNotFoundException("this product does not exist !");
    }

    public void updateProduct(int id, String newName, double newPrice, int newQuantity) throws ProductNotFoundException {
        String sql = "UPDATE product SET name = ?, price = ?, quantity = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newName);
            stmt.setDouble(2, newPrice);
            stmt.setInt(3, newQuantity);
            stmt.setInt(4, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new ProductNotFoundException("This product doesn't exist!");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
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

    public void addToProduct(int id, int quant) throws ProductNotFoundException {
        String sql = "UPDATE product SET quantity = quantity + ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quant);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new ProductNotFoundException("This product doesn't exist!");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void removeFromProduct(int id, int quant) throws ProductNotFoundException, InsufficientStockException {
        Product p = fetchProduct(id);
        if(p == null)
            throw new ProductNotFoundException("This product doesn't exists!");

        if (p.getQuantity() < quant )
            throw new InsufficientStockException("there is a shortage of this product !");

        String sql = "UPDATE product SET quantity = quantity - ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quant);
            stmt.setInt(2, id);

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new ProductNotFoundException("This product doesn't exist!");
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
                products.add(new Product(rs));
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
                products.add(new Product(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return products;
    }
}
