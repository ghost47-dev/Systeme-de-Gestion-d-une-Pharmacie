package com.gestionpharmacie.managers;
import java.util.ArrayList;
import java.util.Date;

import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;

public class ShipmentManager {
    private ArrayList<Supplier> suppliers;
    private ArrayList<Shipment> shipments;
    private ArrayList<ShipmentGood> shipmentGoods;

    public void addSupplier(Supplier s){
        if(fetchSupplier(s.getId()) != null){
            System.err.println("Shipment id already exists!");
        }
        suppliers.add(s);
    }

    public void addShipment(Shipment s){
        if(fetchShipment(s.getId()) != null){
            System.err.println("Shipment id already exists!");
        }
        shipments.add(s);
    }

    public void addShipmentGood(ShipmentGood sg){
        if(fetchShipmentGood(sg.getId()) != null){
            System.err.println("ShipmentGood id already exists!");
        }
        shipmentGoods.add(sg);
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
        ship.receive(d);
        ArrayList<ShipmentGood> out = new ArrayList<>();
        for(ShipmentGood sg : shipmentGoods){
            if(sg.getShipmentId() == id){
                out.add(sg);
            }
        }
        return out;
    }
}
