package com.gestionpharmacie.managers;
import java.util.ArrayList;

import com.gestionpharmacie.model.Client;
import com.gestionpharmacie.model.Sale;
import com.gestionpharmacie.model.SaleProduct;

public class SaleManager {
    ArrayList<Client> clients;
    ArrayList<Sale> sales;
    ArrayList<SaleProduct> saleProducts;

    public int addClient(String name, String surname, int phoneNumber){
        int id = clients.size();
        clients.add(new Client(id, name, surname, phoneNumber));
        return id;
    }

    public int addSale(int cid){
        int id = sales.size();
        sales.add(new Sale(id, cid));
        return id;
    }

    public int addSaleProduct(int sid, int pid, int quant){
        int id = saleProducts.size();
        saleProducts.add(new SaleProduct(id, sid, pid, quant));
        return id;
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
