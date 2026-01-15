package com.gestionpharmacie;

import java.util.ArrayList;

public class ProductManager {
    private ArrayList<Product> products = new ArrayList<>();
    void addProduct(Product p) {
        for (Product product : products) {
            if (product.getId() == p.getId()) {
                System.out.println("This product already exits");
                return;
            }
        }
        products.add(p);
    }
}
