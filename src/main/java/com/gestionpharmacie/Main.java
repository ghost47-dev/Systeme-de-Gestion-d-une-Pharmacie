package com.gestionpharmacie;
import java.util.ArrayList;
import java.util.Scanner;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.model.User;
import com.gestionpharmacie.managers.UserManager;

public class Main {
    private static Scanner sc;
    private static ProductManager pm;
    private static UserManager um;
    static void handleProductRelatedRequest(){
        System.out.println("1. Add.");
        System.out.println("2. Modify.");
        System.out.println("3. Remove.");
        int choice = sc.nextInt();
        if(choice == 1){
            System.out.println("Give ID:");
            int id = sc.nextInt();
            System.out.println("Give name:");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.println("Give price:");
            double price = sc.nextDouble();
            System.out.println("Give quantity:");
            int quant = sc.nextInt();
            Product p = new Product(id, name, price, quant);
            pm.addProduct(p);
        }else if(choice == 2){
            System.out.println("Give ID:");
            int id = sc.nextInt();
            System.out.println("Give new name:");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.println("Give new price:");
            double price = sc.nextDouble();
            System.out.println("Give new quantity:");
            int quant = sc.nextInt();
            pm.updateProduct(id, name, price, quant);
        }else if(choice == 3){
            System.out.println("Give ID:");
            int id = sc.nextInt();
            System.out.println("Give name:");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.println("Give price:");
            double price = sc.nextDouble();
            System.out.println("Give quantity:");
            int quant = sc.nextInt();
            Product p = new Product(id, name, price, quant);
            pm.deleteProduct(p);
        }else{
            System.err.println("Invalid choice!");
        }
    }

    static void handleSupplierRelatedRequest(){
        System.out.println("1. Add.");
        System.out.println("2. Modify.");
        System.out.println("3. Remove.");
    }

    static void handleClientRelatedRequest(){
    }

    static void handleAdminRelatedRequest(){
    }

    public static void main(String[] args) {
        pm = new ProductManager();
        um = new UserManager();

        um.addUser(new User("salah", "123", "notadmin"));
        um.addUser(new User("admin", "admin", "admin"));

        sc = new Scanner(System.in);
        System.out.println("Login:");
        String login = sc.nextLine();

        User u = um.fetchUser(login);
        if(u == null){
            System.err.println("User not found!");
            return;
        }

        System.out.println("Password:");
        String password = sc.nextLine();

        if(!password.equals(u.getPassword())){
            System.err.println("Incorrect password!");
            return;
        }

        while(true){
            int choice;
            System.out.println("1. Product.");
            System.out.println("2. Supplier.");
            System.out.println("3. Client.");
            System.out.println("4. Analysis.");
            choice = sc.nextInt();
            if(choice == 1){
                handleProductRelatedRequest();
            }else if(choice == 2){
                handleSupplierRelatedRequest();
            }else if(choice == 3){
                handleClientRelatedRequest();
            }else if(choice == 4){
                if(u.getPrivilege().equals("admin")){
                    handleAdminRelatedRequest();
                }else{
                    System.err.println("Unprivileged user!");
                }
            }else{
                System.err.println("Invalid choice!");
            }
        }
    }
}
