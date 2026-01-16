package com.gestionpharmacie.managers;

import com.gestionpharmacie.model.Product;

import java.util.ArrayList;

public class ProductManager {
    private ArrayList<Product> products = new ArrayList<>();

    private static ArrayList<Integer> riskyProducts; // maybe this one should stay like this
                                                     // cause this class won't exist in the
                                                     // database so it can have an arraylist

    private int quantityRiskThreshold = 10;

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

    public void updateProduct(int id, String newName, double newPrice, int newQuantity) {
        Product product = fetchProduct(id);
        if (product == null) {
            System.err.println("This product doesn't exist!"); // turn to exception!
            return;
        }
        product.setName(newName);
        product.setPrice(newPrice);
        product.setQuantity(newQuantity);
    }

    public void deleteProduct(int id) {
        Product p = fetchProduct(id);
        if (p == null) {
            System.err.println("This product doesn't exists!");
            return;
        }
        products.remove(p);
    }

    public void addToProduct(int id, int quant){
        Product p = fetchProduct(id);
        if(p == null){
            System.err.println("This product doesn't exists!");
            return;
        }
        p.addQuantity(quant);
        if(p.getQuantity() > quantityRiskThreshold && riskyProducts.contains(id)){
            riskyProducts.remove((Integer)id);
        }
    }

    public void removeFromProduct(int id, int quant){
        Product p = fetchProduct(id);
        if(p == null){
            System.err.println("This product doesn't exists!");
            return;
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
}
