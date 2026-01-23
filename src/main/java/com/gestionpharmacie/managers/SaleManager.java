package com.gestionpharmacie.managers;
import java.sql.*;
import java.util.ArrayList;

import com.gestionpharmacie.model.Client;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.Sale;
import com.gestionpharmacie.model.SaleProduct;
import com.gestionpharmacie.Globals;
import com.gestionpharmacie.exceptions.InsufficientStockException;
import com.gestionpharmacie.exceptions.ProductNotFoundException;

public class SaleManager {
    ProductManager productManager;
    Connection connection;

    public SaleManager() {
        this.connection = Globals.database.getConnection();
        productManager = Globals.managers.product;
    }

    public int addClient(String name, String surname, int phoneNumber) {
        String sql = "INSERT INTO client(name, surname, phone) VALUES(?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setInt(3, phoneNumber);

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

    public int addSale(int cid) {
        String sql = "INSERT INTO sale(client_id) VALUES(?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cid);

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

    public int addSaleProduct(int sid, int pid, int quant) throws InsufficientStockException ,ProductNotFoundException{
	    ProductManager p = Globals.managers.product;
        Product product = p.fetchProduct(pid);
        if (product.getQuantity() < quant )
            throw new InsufficientStockException("Insufficient stock !");

        String sql = "INSERT INTO sale_product(sale_id, product_id, quantity) VALUES(?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sid);
            stmt.setInt(2, pid);
            stmt.setInt(3, quant);

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                p.removeFromProduct(pid,quant);
                return keys.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(sp.quantity * price) as total " +
                "FROM sale_product sp, product p " +
                "WHERE sp.product_id = p.id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0.0;
    }

    public Client fetchClient(int id) {
        String sql = "SELECT * FROM client WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Client(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getInt("phone")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    public Sale fetchSale(int id) {
        String sql = "SELECT * FROM sale WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Sale(
                        rs.getInt("id"),
                        rs.getInt("client_id")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

//    public SaleProduct fetchSaleProduct(int id){
//        for(SaleProduct s : saleProducts){
//            if(s.getId() == id){
//                return s;
//            }
//        }
//        return null;
//    }

    public ArrayList<Integer> getSaleIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        String sql = "SELECT id FROM sale";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return ids;
    }

    public ArrayList<SaleProduct> getSaleProducts(int id) {
        ArrayList<SaleProduct> out = new ArrayList<>();
        String sql = "SELECT * FROM sale_product WHERE sale_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                out.add(new SaleProduct(
                        rs.getInt("id"),
                        rs.getInt("sale_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return out;
    }

}
