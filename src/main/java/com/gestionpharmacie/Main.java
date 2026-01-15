package com.gestionpharmacie;
import java.util.ArrayList;
import java.util.Scanner;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.User;

public class Main {
    void printCommands(){
        System.out.println("1. :w
                );
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
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
            int choice = sc.nextInt();
            System.out.println();
            switch (choice) {
                case 1:
                    break;
                default:
                    System.err.println("Invalid command!");
                    break;
            }
        }
    }
}
