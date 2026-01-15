package com.gestionpharmacie.managers;

import com.gestionpharmacie.model.Product;

import java.util.ArrayList;

public class ProductManager {
    private ArrayList<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        if (fetchProduct(product.getId()) != null) {
            System.out.println("This product already exits!"); // will become an exception soon !!
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
    public void updateProduct(int id, String newName, double newPrice, int newQuantity) {
        Product product = fetchProduct(id);
        if (product == null) {
            System.out.println("This product doesn't exist!"); // turn to exception!
        }
        product.setName(newName);
        product.setPrice(newPrice);
        product.setQuantity(newQuantity);
    }
}
