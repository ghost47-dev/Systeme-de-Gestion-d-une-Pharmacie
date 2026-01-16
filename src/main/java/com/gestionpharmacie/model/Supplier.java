package com.gestionpharmacie.model;

public class Supplier {
    private int id;
    private String name;
	private int phoneNumber;

	private int noLateShipments = 0;
	private int totalNoShipments = 0;

    public Supplier(int id, String name, int phoneNumber){
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
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

	public int getTotalNoShipments() {
		return totalNoShipments;
	}

	public void increaseTotalNoShipments() {
		this.totalNoShipments++;
	}
	public String toString() {
		return "Supplier's name: " + name;
	}
}
