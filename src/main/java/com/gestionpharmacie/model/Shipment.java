package com.gestionpharmacie.model;
import java.util.Date;

public class Shipment {
    private int id;
	private int supplierId;
    private Date requestDate;
    private boolean recieved;
    private Date recievalDate;

    public Shipment(int id, int sid, Date reqDate, boolean rec, Date recDate) {
        this.id = id;
        supplierId = sid;
        requestDate = reqDate;
        recieved = rec;
        recievalDate = recDate;
    }

    public void receive(Date d){
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

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public boolean isRecieved() {
		return recieved;
	}

	public void setRecieved(boolean recieved) {
		this.recieved = recieved;
	}

	public Date getRecievalDate() {
		return recievalDate;
	}

	public void setRecievalDate(Date recievalDate) {
		this.recievalDate = recievalDate;
	}
}
