package com.gestionpharmacie.model;

public class Sale {
    private int id;
    private int clientId;

    public Sale(int id, int clientId){
        this.id = id;
        this.clientId = clientId;
    }

    public int getId(){
        return id;
    }

    public int getClientId(){
        return clientId;
    }
}
