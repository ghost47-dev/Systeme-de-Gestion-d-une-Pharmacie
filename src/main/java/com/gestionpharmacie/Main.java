package com.gestionpharmacie;
import java.util.ArrayList;
import java.util.Scanner;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.User;

public class Main {
    private static Scanner sc;
    static void handleProductRelatedRequest(){
        System.out.println("1. Add.");
        System.out.println("2. Modify.");
        System.out.println("3. Remove.");
        int choice = sc.nextInt();
        if(choice == 1){
        }else if(choice == 2){
        }else if(choice == 3){
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
        sc = new Scanner(System.in);
        System.out.println("Login:");
        String login = sc.nextLine();
        System.out.println("Password:");
        String password = sc.nextLine();

        User u; // tabbasi lazem User manager.
        boolean found = true;
        if(!found){
            System.err.println("User not found!");
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
                if(u.getPrivelige().equals("Admin")){
                    handleAdminRelatedRequest();
                }else{
                    System.err.println("Unpriveliged user!");
                }
            }else{
                System.err.println("Invalid choice!");
            }
        }
    }
}
