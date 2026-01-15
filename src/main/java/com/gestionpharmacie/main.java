package com.gestionpharmacie;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        User u = new User("semah", "123");
        users.add(u);
        ProductManager products = new ProductManager();
        Scanner sc = new Scanner(System.in);
        System.out.println("Login: ");
        String login = sc.nextLine();
        System.out.println("Password: ");
        String password = sc.nextLine();
        for (User user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                while (true) {
                    System.out.println("1. Add product");
                    System.out.println("2. Update product");
                    System.out.println("3. Delete product");
                    System.out.println("4. Exit");
                    int choice = sc.nextInt();
                    if (choice == 1) {
                        System.out.println("Product id: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        System.out.println("Product name: ");
                        String name = sc.nextLine();
                        Product p = new Product(id, name);
                        products.addProduct(p);
                    } else if (choice == 2) {

                    } else if (choice == 3) {

                    }
                    else if (choice == 4) {
                        break;
                    } else {
                        System.out.println("Invalid choice");
                    }
                }
                return;
            }
        }
        System.out.println("Invalid credentials");
    }
}
