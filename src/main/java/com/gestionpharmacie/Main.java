package com.gestionpharmacie;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.model.User;
import com.gestionpharmacie.managers.UserManager;

public class Main {
    private static Scanner sc;
    private static ProductManager pm;
    private static UserManager um;
    private static ShipmentManager sm;

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

    static void handleShipmentRelatedRequest(){
        System.out.println("1. Add supplier.");
        System.out.println("2. Add shipment.");
        System.out.println("3. Modify shipment.");
        System.out.println("4. Cancel shipment.");
        System.out.println("5. Receive shipment.");
        int choice = sc.nextInt();
        
        if(choice == 1){
            System.out.println("Give ID:");
            int id = sc.nextInt();
            System.out.println("Give name:");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.println("Give phone number:");
            int num = sc.nextInt();

            Supplier s = new Supplier(id, name, num);
            sm.addSupplier(s);
        }else if(choice == 2){
            System.out.println("Give ID:");
            int id = sc.nextInt();
            System.out.println("Give supplier ID:");
            int sid = sc.nextInt();
            if(sm.fetchSupplier(sid) == null){
                System.err.println("Invalid supplier ID!");
                return;
            }
            System.out.println("Give request date:");
            Date reqDate = new Date(sc.nextLong());
            System.out.println("Is this shipment already recieved? (y/N)");
            sc.nextLine();
            String ans = sc.nextLine();
            boolean rec = false;
            if(ans.equals("y")){
                rec = true;
            }
            if(rec){
                System.out.println("Give recieval date:");
                Date recDate = new Date(sc.nextLong());
                sc.nextLine();

                sm.addShipment(new Shipment(id, sid, reqDate, true, recDate));
            }else{
                sm.addShipment(new Shipment(id, sid, reqDate, false, null));
            }

            System.out.println("Does this shipment have goods? (y/N)");
            ans = sc.nextLine();
            while(ans.equals("y")){
                System.out.println("Give ID:");
                int gid = sc.nextInt();
                System.out.println("Give product ID:");
                int pid = sc.nextInt();
                System.out.println("Give price:");
                double price = sc.nextDouble();
                System.out.println("Give quantity:");
                int quant = sc.nextInt();
                sc.nextLine();

                sm.addShipmentGood(new ShipmentGood(gid, id, pid, price, quant));

                System.out.println("Does this shipment have more goods? (y/N)");
                ans = sc.nextLine();
            }
        }else if(choice == 3){
            System.out.println("Give ID:");
            int id = sc.nextInt();
            System.out.println("Give new supplier ID:");
            int sid = sc.nextInt();
            if(sm.fetchSupplier(sid) == null){
                System.err.println("Invalid supplier ID!");
                return;
            }
            System.out.println("Give new request date:");
            Date reqDate = new Date(sc.nextLong());
            System.out.println("Give new recieved: (y/N)");
            sc.nextLine();
            String ans = sc.nextLine();
            boolean rec = false;
            if(ans.equals("y")){
                rec = true;
            }
            if(rec){
                System.out.println("Give new recieval date:");
                Date recDate = new Date(sc.nextLong());
                sc.nextLine();

                sm.updateShipment(id, sid, reqDate, true, recDate);
            }else{
                sm.updateShipment(id, sid, reqDate, false, null);
            }
        }else if(choice == 4){
            System.out.println("Give ID:");
            int id = sc.nextInt();
            sm.cancelShipment(id);
        }else if(choice == 5){
            System.out.println("Give ID:");
            int id = sc.nextInt();
            System.out.println("Give date:");
            Date d = new Date(sc.nextLong());
            ArrayList<ShipmentGood> sgs = sm.receiveShipment(id, d);
            for(ShipmentGood sg : sgs){
                pm.addToProduct(sg.getProductId(), sg.getQuantity());
            }
        }else{
            System.err.println("Invalid choice!");
        }
    }

    static void handleClientRelatedRequest(){
    }

    static void handleAdminRelatedRequest(){
    }

    public static void main(String[] args) {
        pm = new ProductManager();
        um = new UserManager();
        sm = new ShipmentManager();

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
                handleShipmentRelatedRequest();
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
