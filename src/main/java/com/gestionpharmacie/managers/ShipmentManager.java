package com.gestionpharmacie.managers;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;

import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;

public class ShipmentManager {
    private ArrayList<Supplier> suppliers;
    private ArrayList<Shipment> shipments;
    private ArrayList<ShipmentGood> shipmentGoods;
    private Connection connection;

    public ShipmentManager(Connection connection) {
        this.connection = connection;
        suppliers = new ArrayList<>();
        shipments = new ArrayList<>();
        shipmentGoods = new ArrayList<>();
    }

    public int addSupplier(String name, int phoneNumber) {
        String sql = "INSERT INTO supplier(name, phone) VALUES(?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setInt(2, phoneNumber);

            ResultSet keys = stmt.executeQuery();
            if (keys.next()) {
                return keys.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }

        return -1;
    }

    public int addShipment(int sid, Date requestDate, boolean recieved, Date recievalDate) {
        String sql = "INSERT INTO shipment(supplier_id, request_date, received, arrival_date) VALUES(?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sid);
            stmt.setDate(2, requestDate);
            stmt.setBoolean(3, recieved);
            stmt.setDate(4, recievalDate);

            ResultSet keys = stmt.executeQuery();
            if (keys.next()) {
                return keys.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public int addShipmentGood(int sid, int pid, double p, int q) {
        String sql = "INSERT INTO shipment_good(shipment_id, product_id, price, quantity) VALUES(?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sid);
            stmt.setInt(2, pid);
            stmt.setDouble(3, p);
            stmt.setInt(4, q);

            ResultSet keys = stmt.executeQuery();
            if (keys.next()) {
                return keys.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public Supplier fetchSupplier(int id) {
        String sql = "SELECT * FROM supplier WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("phone")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

    public Shipment fetchShipment(int id) {
        String sql = "SELECT * FROM shipment WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Shipment(
                        rs.getInt("id"),
                        rs.getInt("supplier_id"),
                        rs.getDate("request_date"),
                        rs.getBoolean("received"),
                        rs.getDate("arrival_date")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

//    public ShipmentGood fetchShipmentGood(int id){
//        for(ShipmentGood s : shipmentGoods){
//            if(s.getId() == id){
//                return s;
//            }
//        }
//        return null;
//    }

    public void updateShipment(int id, int newSId, Date newReqDate, boolean newRec, Date newRecDate) throws ShipmentNotFoundException {
        java.sql.Date request_date = new java.sql.Date(newReqDate.getTime());
        java.sql.Date receive_date = new java.sql.Date(newRecDate.getTime());
        String sql = "UPDATE shipment SET supplier_id = ?, request_id = ?, received = ?, arrival_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newSId);
            stmt.setDate(2, request_date);
            stmt.setBoolean(3, newRec);
            stmt.setDate(4, receive_date);
            stmt.setInt(5, id);

            int rows = stmt.executeUpdate();
            if (rows == 0)
                throw new ShipmentNotFoundException("This shipment doesn't exist!");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void cancelShipment(int id) throws ShipmentNotFoundException {
        String sql = "DELETE FROM shipment WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0)
                throw new ShipmentNotFoundException("This shipment doesn't exist!");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public ArrayList<ShipmentGood> receiveShipment(int id, Date d) throws ShipmentNotFoundException {

        Shipment ship = fetchShipment(id);
        if (ship == null) {
            throw new ShipmentNotFoundException("This shipment doesn't exist!");
        }
        fetchSupplier(ship.getSupplierId()).increaseTotalNoShipments();
        if (!d.equals(ship.getRecievalDate())) {
            fetchSupplier(ship.getSupplierId()).increaseNoLateShipments();
        }
        ship.receive(d);
        ArrayList<ShipmentGood> out = new ArrayList<>();
        for(ShipmentGood sg : shipmentGoods){
            if(sg.getShipmentId() == id){
                out.add(sg);
            }
        }
        return out;
    }

    public void viewSuppliersPerfermance() {
        for (Supplier supplier : suppliers) {
            int total = supplier.getTotalNoShipments();
            if (total != 0) {
                double onTimeDeliveryRate = ((double) (total - supplier.getNoLateShipments()) / total) * 100;
                System.out.println(supplier + " - On time delivery rate: " + onTimeDeliveryRate);
            } else {
                System.out.println(supplier + " - No interactions recorded!");
            }
        }
    }
}
