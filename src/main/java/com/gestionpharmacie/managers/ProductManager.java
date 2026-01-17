package com.gestionpharmacie.managers;

import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.model.Product;

import java.util.ArrayList;

public class ProductManager {
    private ArrayList<Product> products;

    private ArrayList<Integer> riskyProducts; // maybe this one should stay like this
                                                     // cause this class won't exist in the
                                                     // database so it can have an arraylist

    private final int quantityRiskThreshold = 10;

    public ProductManager () {
        products = new ArrayList<>();
        riskyProducts = new ArrayList<>();
    }

    public int addProduct(String name, double price, int quant) {
        int id = products.size();
        products.add(new Product(id, name, price, quant));
        return id;
    }

    public Product fetchProduct(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    public void updateProduct(int id, String newName, double newPrice, int newQuantity) throws ProductNotFoundException {
        Product product = fetchProduct(id);
        if (product == null) {
            throw new ProductNotFoundException("This product doesn't exist!");
        }
        product.setName(newName);
        product.setPrice(newPrice);
        product.setQuantity(newQuantity);
    }

    public void deleteProduct(int id) throws ProductNotFoundException {
        Product p = fetchProduct(id);
        if (p == null) {
            throw new ProductNotFoundException("This product doesn't exist!");
        }
        products.remove(p);
    }

    public void addToProduct(int id, int quant) throws ProductNotFoundException {
        Product p = fetchProduct(id);
        if(p == null){
            throw new ProductNotFoundException("This product doesn't exists!");
        }
        p.addQuantity(quant);
        if(p.getQuantity() > quantityRiskThreshold && riskyProducts.contains(id)){
            riskyProducts.remove((Integer)id);
        }
    }

    public void removeFromProduct(int id, int quant) throws ProductNotFoundException {
        Product p = fetchProduct(id);
        if(p == null){
            throw new ProductNotFoundException("This product doesn't exists!");
        }
        if(p.getQuantity() < quant){
            System.err.println("Invalid quantity!");
            return;
        }
        p.removeQuantity(quant); // this should throw something
        if(p.getQuantity() < quantityRiskThreshold && !riskyProducts.contains(id)){
            riskyProducts.add(id);
        }
    }
    public void viewStock() {
        for (Product product : products) {
            System.out.println(product);
        }
    }
    public void lowStockAlert() {
        System.out.println("===LOW STOCK PRODUCTS===");
        boolean found = false;
        for (Product product : products) {
            if (product.getQuantity() < quantityRiskThreshold) {
                found = true;
                System.out.println(" - " + product.getName() + ": " + product.getQuantity() + " units left!");
            }
        }
        if (!found) {
            System.out.println("All stocks are full!");
        }
    }
}
