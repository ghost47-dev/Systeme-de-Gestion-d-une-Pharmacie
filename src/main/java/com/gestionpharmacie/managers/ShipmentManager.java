package com.gestionpharmacie.managers;
import java.util.Date;
import java.util.ArrayList;

import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;

public class ShipmentManager {
    private ArrayList<Supplier> suppliers;
    private ArrayList<Shipment> shipments;
    private ArrayList<ShipmentGood> shipmentGoods;

    public ShipmentManager() {
        suppliers = new ArrayList<>();
        shipments = new ArrayList<>();
        shipmentGoods = new ArrayList<>();
    }

    public int addSupplier(String name, int phoneNumber){
        int id = suppliers.size();
        suppliers.add(new Supplier(id, name, phoneNumber));
        return id;
    }

    public int addShipment(int sid, Date requestDate, boolean recieved, Date recievalDate){
        int id = shipments.size();
        shipments.add(new Shipment(id, sid, requestDate, recieved, recievalDate));
        return id;
    }

    public int addShipmentGood(int sid, int pid, double p, int q){
        int id = shipmentGoods.size();
        shipmentGoods.add(new ShipmentGood(id, sid, pid, p, q));
        return id;
    }

    public Supplier fetchSupplier(int id){
        for(Supplier s : suppliers){
            if(s.getId() == id){
                return s;
            }
        }
        return null;
    }

    public Shipment fetchShipment(int id){
        for(Shipment s : shipments){
            if(s.getId() == id){
                return s;
            }
        }
        return null;
    }

    public ShipmentGood fetchShipmentGood(int id){
        for(ShipmentGood s : shipmentGoods){
            if(s.getId() == id){
                return s;
            }
        }
        return null;
    }

    public void updateShipment(int id, int newSId, Date newReqDate, boolean newRec, Date newRecDate) {
        Shipment ship = fetchShipment(id);
        if (ship == null) {
            System.err.println("This shipment doesn't exist!"); // turn to exception!
            return;
        }
        ship.setSupplierId(newSId);
        ship.setRequestDate(newReqDate);
        ship.setRecieved(newRec);
        ship.setRecievalDate(newRecDate);
    }

    public void cancelShipment(int id){
        Shipment ship = fetchShipment(id);
        if (ship == null) {
            System.err.println("This shipment doesn't exist!"); // turn to exception!
            return;
        }
        shipments.remove(ship);
        ArrayList<ShipmentGood> bad = new ArrayList<>();
        for(ShipmentGood sg : shipmentGoods){
            if(sg.getShipmentId() == id){
                bad.add(sg);
            }
        }
        shipmentGoods.removeAll(bad);
    }

    public ArrayList<ShipmentGood> receiveShipment(int id, Date d){
        Shipment ship = fetchShipment(id);
        if (ship == null) {
            System.err.println("This shipment doesn't exist!"); // turn to exception!
            return null;
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
