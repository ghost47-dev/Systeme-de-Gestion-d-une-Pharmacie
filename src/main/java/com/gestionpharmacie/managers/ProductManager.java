package com.gestionpharmacie.managers;

import com.gestionpharmacie.model.Product;

import java.util.ArrayList;

public class ProductManager {
    private ArrayList<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        if (fetchProduct(product.getId()) != null) {
            System.out.println("This product already exits!");
        }
        products.add(product);
    }
    public Product fetchProduct(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }
}
