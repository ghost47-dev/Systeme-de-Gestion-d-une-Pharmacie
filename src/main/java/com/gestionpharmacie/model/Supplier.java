package com.gestionpharmacie.model;

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
	public  Supplier(ResultSet rs){
		try{
			this.id = rs.getInt("id");
			this.name = rs.getString("name");
			this.phoneNumber = rs.getInt("phone");
			this.noLateShipments = rs.getInt("no_late_shipments");

		} catch (SQLException e){
			e.printStackTrace();
		}

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

	public int getNoLateShipments() {
		return noLateShipments;
	}

	public void increaseNoLateShipments() {
		this.noLateShipments++;
	}

	public int getTotalNoShipments(Connection con) {
		String query = "SELECT COUNT(*) as nb FROM shipment WHERE supplier_id = ? GROUP BY supplier_id";
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("nb");
			}
		} catch (SQLException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return 0;
	}

	public String toString() {
		return "Supplier's name: " + name + ",Supplier's id :" + id + ",Supplier's phone number" + phoneNumber;
	}
}

