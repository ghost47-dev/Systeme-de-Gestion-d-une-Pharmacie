package com.gestionpharmacie.model;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public  Shipment(ResultSet rs) {
		try {
			this.id = rs.getInt("id");
			this.supplierId = rs.getInt("supplier_id");

			java.sql.Date sqlDate = rs.getDate("request_date"); //
			java.util.Date utilDate = new java.util.Date(sqlDate.getTime()); // casting sql date to java date
			this.requestDate = utilDate; //

			java.sql.Date sqlDate2 = rs.getDate("recieval_date"); //
			java.util.Date utilDate2 = new java.util.Date(sqlDate2.getTime()); // casting sql date to java date
			this.recievalDate = utilDate2;

			this.recieved = rs.getBoolean("received");


		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	public String toString(){
		return "Shipment's id: " +id +" Supplier's id :" + supplierId + ",Shipment's request date" + requestDate + "Shipment State : "+recieved+ "Shipment reciept date: "+ recievalDate;
	}
}
