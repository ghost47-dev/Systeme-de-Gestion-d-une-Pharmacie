package com.gestionpharmacie;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        ProductManager products;
        Scanner sc = new Scanner("cin");
        System.out.println("Login: ");
        String login = sc.nextLine();
        System.out.println("Password: ");
        String password = sc.nextLine();
        for (User user : users) {
            if (user.getLogin() == login && user.getPassword() == password) {
                while (true) {
                    System.out.println("1. Add product");
                    System.out.println("2. Update product");
                    System.out.println("3. Delete product");
                    int choice = sc.nextInt();
                    if (choice == 1) {

                    } else if (choice == 2) {

                    } else if (choice == 3) {

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
