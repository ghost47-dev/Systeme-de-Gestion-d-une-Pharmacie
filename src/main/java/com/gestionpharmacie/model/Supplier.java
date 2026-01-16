package com.gestionpharmacie.model;

public class Supplier {
    private int id;
    private String name;
	private int phoneNumber;

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
}
