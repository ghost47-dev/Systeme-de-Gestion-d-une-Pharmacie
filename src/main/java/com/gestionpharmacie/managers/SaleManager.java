package com.gestionpharmacie.managers;
import java.util.ArrayList;

import com.gestionpharmacie.model.Client;
import com.gestionpharmacie.model.Sale;
import com.gestionpharmacie.model.SaleProduct;

public class SaleManager {
    ArrayList<Client> clients;
    ArrayList<Sale> sales;
    ArrayList<SaleProduct> saleProducts;

    public void addClient(Client c){
        if(fetchClient(c.getId()) != null){
            System.err.println("Client already exists!");
            return;
        }
        clients.add(c);
    }

    public void addSale(Sale s){
        if(fetchSale(s.getId()) != null){
            System.err.println("Sale already exists!");
            return;
        }
        sales.add(s);
    }

    public void addSaleProduct(SaleProduct s){
        if(fetchSaleProduct(s.getId()) != null){
            System.err.println("Sale already exists!");
            return;
        }
        saleProducts.add(s);
    }

    public Client fetchClient(int id){
        for(Client c : clients){
            if(c.getId() == id){
                return c;
            }
        }
        return null;
    }

    public Sale fetchSale(int id){
        for(Sale s : sales){
            if(s.getId() == id){
                return s;
            }
        }
        return null;
    }

    public SaleProduct fetchSaleProduct(int id){
        for(SaleProduct s : saleProducts){
            if(s.getId() == id){
                return s;
            }
        }
        return null;
    }
}
