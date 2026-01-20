package com.gestionpharmacie.model;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Supplier {
    private int id;
    private String name;
	private int phoneNumber;
	private int noLateShipments = 0;

    public Supplier(int id, String name, int phoneNumber, int noLateShipments){
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
		this.noLateShipments = noLateShipments;
    }

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumerotel() {
		return phoneNumber;
	}
	public void setNumerotel(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getNoLateShipments(Connection conn) {
		String query = "SELECT no_late_shipments  FROM supplier WHERE id = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setInt(1, id);
				ResultSet res = ps.executeQuery();
				int nb = 0;
				if (res.next()) {
					nb = res.getInt("NoLateShipments");
				}
			} catch(SQLException e){
				e.printStackTrace();
			}
		return noLateShipments;
	}

	

	public int getTotalNoShipments(Connection conn) {
		String query = "SELECT COUNT(*) as nb FROM shipment WHERE supplier_id = ? GROUP BY supplier_id";
		try (PreparedStatement ps = conn.prepareStatement(query)){

				ps.setInt(1, id);
				ResultSet res = ps.executeQuery();
				int nb = 0;
				if (res.next()) {
					nb = res.getInt("nb");
					return nb;
				}

			} catch(SQLException e){
				e.printStackTrace();
			}
		return 0;
	}

	public String toString() {
		return "Supplier's name: " + name;
	}
}
