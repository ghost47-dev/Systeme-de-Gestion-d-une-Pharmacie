package com.gestionpharmacie;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import com.gestionpharmacie.exceptions.*;
import com.gestionpharmacie.model.*;
import com.gestionpharmacie.managers.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== PHARMACY DATABASE TEST ===\n");

        try {
            // Connect to database
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pharmacy", "malek", "password");
            System.out.println("✓ Database connected!\n");

            // Initialize managers
            ProductManager pm = new ProductManager(conn);
            SaleManager slm = new SaleManager(pm, conn);
            ShipmentManager sm = new ShipmentManager(conn);

            // Test 1: Add Products
            System.out.println("TEST 1: Adding Products");
            System.out.println("------------------------");
            int prod1 = pm.addProduct("Paracetamol 500mg", 5.50, 100);
            int prod2 = pm.addProduct("Aspirin 100mg", 7.00, 50);
            int prod3 = pm.addProduct("Doliprane 1000mg", 8.50, 5);  // Low stock!
            System.out.println("✓ Added product IDs: " + prod1 + ", " + prod2 + ", " + prod3);
            System.out.println();

            // Test 2: View Stock
            System.out.println("TEST 2: View Stock");
            System.out.println("------------------");
            ArrayList<Product> stock = pm.viewStock();
            for (Product p : stock) {
                System.out.println(p);
            }
            System.out.println();

            // Test 3: Low Stock Alert
            System.out.println("TEST 3: Low Stock Alert");
            System.out.println("-----------------------");
            ArrayList<Product> lowStock = pm.lowStockAlert();
            if (lowStock.isEmpty()) {
                System.out.println("All products have sufficient stock");
            } else {
                for (Product p : lowStock) {
                    System.out.println("⚠️  " + p.getName() + ": only " + p.getQuantity() + " left!");
                }
            }
            System.out.println();

            // Test 4: Update Product
            System.out.println("TEST 4: Update Product");
            System.out.println("----------------------");
            try {
                pm.updateProduct(prod1, "Paracetamol 500mg (Updated)", 6.00, 150);
                System.out.println("✓ Updated product " + prod1);
                Product updated = pm.fetchProduct(prod1);
                System.out.println("  New details: " + updated);
            } catch (ProductNotFoundException e) {
                System.out.println("✗ " + e.getMessage());
            }
            System.out.println();

            // Test 5: Add Suppliers
            System.out.println("TEST 5: Adding Suppliers");
            System.out.println("------------------------");
            int sup1 = sm.addSupplier("Semah Pharma", 12345678);
            int sup2 = sm.addSupplier("Awss Medical", 98765432);
            System.out.println("✓ Added supplier IDs: " + sup1 + ", " + sup2);
            System.out.println();

            // Test 6: Create Shipment
            System.out.println("TEST 6: Creating Shipment");
            System.out.println("-------------------------");
            Date today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date nextWeek = cal.getTime();

            int shipment1 = sm.addShipment(sup1, today, false, nextWeek);
            System.out.println("✓ Created shipment ID: " + shipment1);

            // Add goods to shipment
            sm.addShipmentGood(shipment1, prod1, 4.00, 50);
            sm.addShipmentGood(shipment1, prod2, 5.50, 30);
            System.out.println("✓ Added 2 items to shipment");
            System.out.println();

            // Test 7: Receive Shipment (updates stock)
            System.out.println("TEST 7: Receiving Shipment");
            System.out.println("--------------------------");
            System.out.println("Stock before receiving:");
            Product before1 = pm.fetchProduct(prod1);
            Product before2 = pm.fetchProduct(prod2);
            System.out.println("  " + before1.getName() + ": " + before1.getQuantity());
            System.out.println("  " + before2.getName() + ": " + before2.getQuantity());

            try {
                ArrayList<ShipmentGood> goods = sm.receiveShipment(shipment1, today);
                for (ShipmentGood sg : goods) {
                    pm.addToProduct(sg.getProductId(), sg.getQuantity());
                }
                System.out.println("✓ Shipment received, stock updated");
            } catch (ShipmentNotFoundException | ProductNotFoundException e) {
                System.out.println("✗ " + e.getMessage());
            }

            System.out.println("Stock after receiving:");
            Product after1 = pm.fetchProduct(prod1);
            Product after2 = pm.fetchProduct(prod2);
            System.out.println("  " + after1.getName() + ": " + after1.getQuantity() + " (+" + (after1.getQuantity() - before1.getQuantity()) + ")");
            System.out.println("  " + after2.getName() + ": " + after2.getQuantity() + " (+" + (after2.getQuantity() - before2.getQuantity()) + ")");
            System.out.println();

            // Test 8: Add Client
            System.out.println("TEST 8: Adding Client");
            System.out.println("---------------------");
            int client1 = slm.addClient("Ahmed", "Ben Ali", 55123456);
            int client2 = slm.addClient("Fatma", "Gharbi", 22987654);
            System.out.println("✓ Added client IDs: " + client1 + ", " + client2);
            System.out.println();

            // Test 9: Make Sale
            System.out.println("TEST 9: Making Sale");
            System.out.println("-------------------");
            int sale1 = slm.addSale(client1);
            System.out.println("✓ Created sale ID: " + sale1);

            System.out.println("Stock before sale:");
            Product beforeSale = pm.fetchProduct(prod1);
            System.out.println("  " + beforeSale.getName() + ": " + beforeSale.getQuantity());

            try {
                // Sell 10 units
                pm.removeFromProduct(prod1, 10);
                slm.addSaleProduct(sale1, prod1, 10);
                System.out.println("✓ Sold 10 units of " + beforeSale.getName());
            } catch (ProductNotFoundException | InsufficientStockException e) {
                System.out.println("✗ " + e.getMessage());
            }

            System.out.println("Stock after sale:");
            Product afterSale = pm.fetchProduct(prod1);
            System.out.println("  " + afterSale.getName() + ": " + afterSale.getQuantity() + " (-10)");
            System.out.println();

            // Test 10: Test Insufficient Stock Exception
            System.out.println("TEST 10: Testing Insufficient Stock");
            System.out.println("------------------------------------");
            try {
                pm.removeFromProduct(prod1, 9999);  // Way too much!
                System.out.println("✗ Should have thrown exception!");
            } catch (ProductNotFoundException e) {
                System.out.println("✗ Wrong exception: " + e.getMessage());
            } catch (InsufficientStockException e) {
                System.out.println("✓ Correctly threw exception: " + e.getMessage());
            }
            System.out.println();

            // Test 11: View Sales History
            System.out.println("TEST 11: View Sales History");
            System.out.println("---------------------------");
            ArrayList<Integer> saleIds = slm.getSaleIds();
            System.out.println("Found " + saleIds.size() + " sale(s)");
            for (Integer saleId : saleIds) {
                Sale s = slm.fetchSale(saleId);
                Client c = slm.fetchClient(s.getClientId());
                System.out.println("Sale #" + saleId + c);

                ArrayList<SaleProduct> items = slm.getSaleProducts(saleId);
                for (SaleProduct sp : items) {
                    Product p = pm.fetchProduct(sp.getProductId());
                    System.out.println("  - " + p.getName() + " x" + sp.getQuantity());
                }
            }
            System.out.println();

            // Test 12: Total Revenue
            System.out.println("TEST 12: Total Revenue");
            System.out.println("----------------------");
            double revenue = slm.getTotalRevenue();
            System.out.println("Total Revenue: " + String.format("%.2f DT", revenue));
            System.out.println();

            // Test 13: Supplier Performance
            System.out.println("TEST 13: Supplier Performance");
            System.out.println("-----------------------------");
            sm.viewSuppliersPerfermance();
            System.out.println();

            // Test 14: Delete Product (should fail if used in sales)
            System.out.println("TEST 14: Delete Product");
            System.out.println("-----------------------");
            try {
                pm.deleteProduct(prod3);
                System.out.println("✓ Deleted product " + prod3);
            } catch (ProductNotFoundException e) {
                System.out.println("✗ " + e.getMessage());
            }
            System.out.println();

            // Test 15: Cancel Shipment
            System.out.println("TEST 15: Cancel Shipment");
            System.out.println("------------------------");
            int shipment2 = sm.addShipment(sup2, today, false, nextWeek);
            System.out.println("Created shipment ID: " + shipment2);
            try {
                sm.cancelShipment(shipment2);
                System.out.println("✓ Cancelled shipment " + shipment2);
            } catch (ShipmentNotFoundException e) {
                System.out.println("✗ " + e.getMessage());
            }
            System.out.println();

            System.out.println("=========================");
            System.out.println("ALL TESTS COMPLETED!");
            System.out.println("=========================");

            conn.close();

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}