package com.gestionpharmacie.model;

public class SaleProduct {
    private int id;
    private int saleId;
    private int productId;
    private int quantity;

    public SaleProduct(int id, int sid, int pid, int quant){
        this.id = id;
        saleId = sid;
        productId = pid;
        quantity = quant;
    }

    public int getId(){
        return id;
    }

    public int getProductId(){
        return productId;
    }

    public int getQuantity(){
        return quantity;
    }
}

