package com.gestionpharmacie.managers;
import java.sql.*;
import java.util.Date;
import java.util.ArrayList;

import com.gestionpharmacie.Globals;
import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;

public class ShipmentManager {
    private Connection connection;

    public ShipmentManager() {
        this.connection = Globals.database.getConnection();
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

    public int fetchSupplierbyPhone(int phone) {
        String sql = "SELECT id FROM supplier WHERE phone = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return -1;
    }

    public ArrayList<ShipmentGood> fetchShipmentGood (int shipment_id) {
        String sql = "SELECT * FROM shipment_good where shipment_good.shipment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1,shipment_id);
            ResultSet rs = stmt.executeQuery();
            ArrayList<ShipmentGood> out = new ArrayList<>();
            while(rs.next()){
                ShipmentGood sg = new ShipmentGood(rs.getInt("id"),
                        rs.getInt("shipment_id"),
                        rs.getInt("product_id"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"));
                out.add(sg);
            }
            return out;
        }
        catch(SQLException e){
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

    public void updateShipment(int id, int newSId, Date newReqDate, boolean newRec, Date newRecDate ) throws ShipmentNotFoundException {
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
    public void updateShipmentGood(int id_ship_good ,int product_it, int new_quantity , double new_price) throws ProductNotFoundException {
        ProductManager pm = Globals.managers.product;
        if (pm.fetchProduct(product_it) == null ) 
            throw new ProductNotFoundException();
        String query = "UPDATE shipment_good SET quantity = ? , price = ? WHERE id = ? AND product_id = ?" ;
        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setInt(1, new_quantity);
            ps.setDouble(2, new_price);
            ps.setInt(3,id_ship_good);
            ps.setInt(4,product_it);
            ps.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }


    public void cancelShipment(int id) throws ShipmentNotFoundException {
        ArrayList<ShipmentGood> shipmentGood = fetchShipmentGood(id);
        for (ShipmentGood sg : shipmentGood){
            ProductManager productManager = Globals.managers.product;
            try {
                if (productManager.fetchProduct(sg.getProductId()).getQuantity() == 0){
                    productManager.deleteProduct(sg.getProductId()); 
                }
            }
            catch (ProductNotFoundException e){
                e.printStackTrace();
                return;
            }
        }
        // Delete this shipment_supplier
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

    public int receiveShipment(int id, Date d) throws ShipmentNotFoundException,ProductNotFoundException {
        Shipment ship = fetchShipment(id);
        if (ship == null)
            throw new ShipmentNotFoundException("This shipment doesn't exist!");
        Date expectedArrival = ship.getRecievalDate();

        String sql = "UPDATE shipment SET received = TRUE, arrival_date = ? WHERE id = ? ";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(2, id);
            java.sql.Date date = new java.sql.Date(d.getTime());
            stmt.setDate(1, date);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        sql = """
            SELECT shipment_good.product_id, quantity 
            FROM shipment_good JOIN shipment ON shipment_good.shipment_id = shipment.id 
            WHERE shipment.id = ?;
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sql2 = "UPDATE product SET quantity = quantity + ? WHERE id = ?";
                try (PreparedStatement stmt2 = connection.prepareStatement(sql2)) {
                    stmt2.setInt(1, rs.getInt("quantity"));
                    stmt2.setInt(2, rs.getInt("shipment_good.product_id"));
                    stmt2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        if (d.after(expectedArrival)) {
            sql = "UPDATE supplier SET no_late_shipments = no_late_shipments + 1 WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, ship.getSupplierId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        return -1;
    }
    public void updateSupplier(int id ,String new_name,int new_phone){
        String query = "UPDATE supplier SET name = ?,phone = ? WHERE id = ?";
        try(PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1,new_name);
            ps.setInt(2,new_phone);
            ps.setInt(3,id);
            ps.executeUpdate();

        } catch(SQLException e) {
            e.printStackTrace();
        }
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
                    double onTimeDeliveryRate = ((double) (total - supplier.getNoLateShipments()) / total) * 100;
                    output.add(supplier.getId()+": "+supplier.getName() + " - On time delivery rate: " + String.format("%.2f", onTimeDeliveryRate) + "%");
                } else {
                    output.add(supplier.getName() + " - No interactions recorded!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return output;
    }
    public ArrayList<Supplier> fetchSuppliers() {
        String query = "SELECT * FROM supplier";
        try(Statement ps = connection.createStatement()) {
            ResultSet rs = ps.executeQuery(query);
            ArrayList<Supplier> out = new ArrayList<>();
            while(rs.next()) {
                Supplier sup = new Supplier(rs);
                out.add(sup);
            }
            return out ;
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<Shipment> fetchShipments(){
        String query = "SELECT * FROM shipment";
        try(Statement ps = connection.createStatement()) {
            ResultSet rs = ps.executeQuery(query);
            ArrayList<Shipment> out = new ArrayList<>();
            while (rs.next()) {
                Shipment shipment = new Shipment(rs);
                out.add(shipment);
            }
            return out;

        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public int deleteShipmentGood(int shipment_good_id){
        String sql1 = "SELECT COUNT(*) sg FROM shipment_good WHERE shipment_id = (SELECT shipment_id FROM shipment_good WHERE shipment_good.id = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql1)) {
            stmt.setInt(1, shipment_good_id);

            ResultSet rs  = stmt.executeQuery();
            if (!rs.next()) return -1 ;
            else if (rs.getInt("sg") == 1){
                String sql2 = "DELETE FROM shipment WHERE id = (SELECT shipment_id FROM shipment_good WHERE shipment_good.id = ?)"; 
                try (PreparedStatement stmt1 = connection.prepareStatement(sql2)) {
                    stmt1.setInt(1, shipment_good_id);

                    int rows = stmt1.executeUpdate();
                    if (rows == 0) return -1 ;

                } catch (SQLException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            
            }
            else {
                String sql2 = "DELETE FROM shipment_good WHERE id = ?"; 
                try (PreparedStatement stmt3 = connection.prepareStatement(sql2)) {
                    stmt3.setInt(1, shipment_good_id);

                    int rows = stmt3.executeUpdate();
                    if (rows == 0) return -1 ;

                } catch (SQLException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 1;
    }
}


