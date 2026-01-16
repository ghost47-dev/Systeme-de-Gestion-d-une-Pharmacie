package com.gestionpharmacie.model;
import java.time.LocalDate;

public class Shipment {
    private int id;
	private int supplierId;
    private LocalDate requestDate;
    private boolean recieved;
    private LocalDate recievalDate;

    public Shipment(int id, int sid, LocalDate reqDate, boolean rec, LocalDate recDate) {
        this.id = id;
        supplierId = sid;
        requestDate = reqDate;
        recieved = rec;
        recievalDate = recDate;
    }

    public void receive(LocalDate d){
        recieved = true;
        recievalDate = d;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public LocalDate getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(LocalDate requestDate) {
		this.requestDate = requestDate;
	}

	public boolean isRecieved() {
		return recieved;
	}

	public void setRecieved(boolean recieved) {
		this.recieved = recieved;
	}

	public LocalDate getRecievalDate() {
		return recievalDate;
	}

	public void setRecievalDate(LocalDate recievalDate) {
		this.recievalDate = recievalDate;
	}
}
