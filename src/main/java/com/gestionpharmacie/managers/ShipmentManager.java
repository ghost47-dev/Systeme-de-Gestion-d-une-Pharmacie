package com.gestionpharmacie.managers;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;

import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;

public class ShipmentManager {
    private Connection connection;

    public ShipmentManager(Connection connection) {
        this.connection = connection;
    }

    public int addSupplier(String name, int phoneNumber) {
        String sql = "INSERT INTO supplier(name, phone) VALUES(?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setInt(2, phoneNumber);

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

    public int addShipment(int sid, Date requestDate, boolean recieved, Date recievalDate)  {
        java.sql.Date request_date = new java.sql.Date(requestDate.getTime());
        java.sql.Date receive_date = new java.sql.Date(recievalDate.getTime());
        String sql = "INSERT INTO shipment(supplier_id, request_date, received, arrival_date) VALUES(?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sid);
            stmt.setDate(2, request_date);
            stmt.setBoolean(3, recieved);
            stmt.setDate(4, receive_date);

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

    public int addShipmentGood(int sid, int pid, double p, int q) throws ShipmentNotFoundException{
        String sql = "INSERT INTO shipment_good(shipment_id, product_id, price, quantity) VALUES(?, ?, ?, ?)";
        if (fetchShipment(sid) == null){
            throw new ShipmentNotFoundException("Shipment Not Found");
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sid);
            stmt.setInt(2, pid);
            stmt.setDouble(3, p);
            stmt.setInt(4, q);

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

    public Supplier fetchSupplier(int id) {
        String sql = "SELECT * FROM supplier WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("phone"),
                        rs.getInt("no_late_shipments")
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
        String sql = "UPDATE shipment SET supplier_id = ?, request_date = ?, received = ?, arrival_date = ? WHERE id = ?";
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
        String sql = "UPDATE shipment SET received = TRUE WHERE id = ?;"+
                "UPDATE product P SET P.quantity = P.quantity + 10 WHERE P.id in "+
                "(Select product_id FROM shipment_good G,shipment S WHERE G.shipment_id = S.id);";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int keys = stmt.executeUpdate();
            if (keys == 0)
                throw new ShipmentNotFoundException("This shipment doesn't exist!");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        Shipment ship = fetchShipment(id);
        if (!d.equals(ship.getRecievalDate())) {
            sql = "UPDATE supplier SET no_late_shipments = no_late_shipments + 1 WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, ship.getSupplierId());
                int keys = stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        ArrayList<ShipmentGood> out = new ArrayList<>();
        sql = "SELECT * FROM shipment_good WHERE shipment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                out.add(new ShipmentGood(
                        rs.getInt("id"),
                        rs.getInt("shipment_id"),
                        rs.getInt("product_id"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return out;
    }

    public ArrayList<String> viewSuppliersPerfermance() {
        ArrayList<String> output = new ArrayList<>();
        String sql = "SELECT * FROM supplier";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Supplier supplier = new Supplier(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("phone"),
                        rs.getInt("no_late_shipments")
                );
                int total = supplier.getTotalNoShipments(connection);
                if (total != 0) {
                    double onTimeDeliveryRate = ((double) (total - supplier.getNoLateShipments()) / total);
                    output.add(supplier.getName() + " - On time delivery rate: " + onTimeDeliveryRate);
                } else {
                    output.add(supplier.getName() + " - No interactions recorded!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return output;
    }
    public void fetchSuppliers() {
        String query = "SELECT * FROM supplier";
        try(Statement ps = connection.createStatement()) {
            ResultSet rs = ps.executeQuery(query);
            if (rs.next()) {
                Supplier sup = new Supplier(rs);
                sup.toString();
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void fetchShipments(){
        String query = "SELECT * FROM shipment";
        try(Statement ps = connection.createStatement()) {
            ResultSet rs = ps.executeQuery(query);
            if (rs.next()) {
                Shipment sup = new Shipment(rs);
                sup.toString();
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
