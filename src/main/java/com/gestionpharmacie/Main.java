package com.gestionpharmacie;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.SaleManager;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.model.User;
import com.gestionpharmacie.managers.UserManager;
import com.gestionpharmacie.model.Client;
import com.gestionpharmacie.model.Sale;
import com.gestionpharmacie.model.SaleProduct;
import org.w3c.dom.ls.LSOutput;

public class Main {
    private static Scanner sc;
    private static ProductManager pm;
    private static UserManager um;
    private static ShipmentManager sm;
    private static SaleManager slm;

    static void handleProductRelatedRequest(){
        System.out.println("1. Add.");
        System.out.println("2. Modify.");
        System.out.println("3. Remove.");
        int choice = sc.nextInt();
        if(choice == 1){
            System.out.println("Give name:");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.println("Give price:");
            double price = sc.nextDouble();
            System.out.println("Give quantity:");
            int quant = sc.nextInt();
            int id = pm.addProduct(name, price, quant);
            System.out.println("Created product with id " + id);
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
            pm.deleteProduct(id);
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
            System.out.println("Give name:");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.println("Give phone number:");
            int num = sc.nextInt();

            int id = sm.addSupplier(name, num);
            System.out.println("Created supplier with id " + id);
        }else if(choice == 2){
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
            int id;
            if(rec){
                System.out.println("Give recieval date:");
                Date recDate = new Date(sc.nextLong());
                sc.nextLine();

                id = sm.addShipment(sid, reqDate, true, recDate);
            }else{
                id = sm.addShipment(sid, reqDate, false, null);
            }

            System.out.println("Give shipment good:");
            ans = "y";
            while(ans.equals("y")){
                System.out.println("Give product ID:");
                int pid = sc.nextInt();
                if(pm.fetchProduct(pid) == null){
                    System.err.println("Invalid product ID!");
                    continue;
                }
                System.out.println("Give price:");
                double price = sc.nextDouble();
                System.out.println("Give quantity:");
                int quant = sc.nextInt();
                sc.nextLine();

                sm.addShipmentGood(id, pid, price, quant);

                System.out.println("Does this shipment have more goods? (y/N)");
                ans = sc.nextLine();
            }
            System.out.println("Created shipment with id " + id);
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

    static void handleSaleRelatedRequest(){
        System.out.println("1. Add new sale.");
        System.out.println("2. View sales history.");
        int choice = sc.nextInt();
        if(choice == 1){
            System.out.println("Is this a new client? (y/N)");
            sc.nextLine();
            String ans = sc.nextLine();
            int cid;
            if(ans.equals("y")){
                System.out.println("Give name:");
                String name = sc.nextLine();
                System.out.println("Give surname:");
                String surname = sc.nextLine();
                System.out.println("Give phone number:");
                int num = sc.nextInt();

                cid = slm.addClient(name, surname, num);
            }else{
                System.out.println("Give client ID:");
                cid = sc.nextInt();
                if(slm.fetchClient(cid) == null){
                    System.err.println("Invalid client ID!");
                    return;
                }
            }
            int id = slm.addSale(cid);

            System.out.println("Give sale product:");
            ans = "y";
            while(ans.equals("y")){
                System.out.println("Give product ID:");
                int pid = sc.nextInt();
                if(pm.fetchProduct(pid) == null){
                    System.err.println("Invalid product ID!");
                    continue;
                }
                System.out.println("Give quantity:");
                int quant = sc.nextInt();
                sc.nextLine();

                pm.removeFromProduct(pid, quant);

                slm.addSaleProduct(id, pid, quant);

                System.out.println("Does this sale have more products? (y/N)");
                ans = sc.nextLine();
            }
            System.out.println("Created sale with id " + id);
        }else if(choice == 2){
            System.out.println("Select sale id:");
            System.out.println(slm.getSaleIds());
            int id = sc.nextInt();
            Sale s = slm.fetchSale(id);
            Client c = slm.fetchClient(s.getClientId());
            System.out.println(c);
            ArrayList<SaleProduct> sps = slm.getSaleProducts(id);
            for(SaleProduct sp : sps){
                String prodName = pm.fetchProduct(sp.getProductId()).getName();
                System.out.println("Product " + prodName + " * " + sp.getQuantity());
            }
        }else{
            System.err.println("Invalid choice!");
        }
    }

    static void handleAdminRelatedRequest(){
        System.out.println("1. View stocks");
        System.out.println("2. View Revenue");
        System.out.println("3. View suppliers' performance");
        int choice = sc.nextInt();
        if (choice == 1) {
            pm.viewStock();
        }
        if (choice == 2) {
            System.out.println("Total revenue: " + slm.getTotalRevenue());
        }
    }

    public static void main(String[] args) {
        pm = new ProductManager();
        //hardcoded data for testing
        pm.addProduct("aspirin", 4, 200);
        pm.addProduct("doliprane", 7, 120);
        um = new UserManager();
        sm = new ShipmentManager();
        slm = new SaleManager(pm);

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
            System.out.println("3. Sale.");
            System.out.println("4. Analysis.");
            choice = sc.nextInt();
            if(choice == 1){
                handleProductRelatedRequest();
            }else if(choice == 2){
                handleShipmentRelatedRequest();
            }else if(choice == 3){
                handleSaleRelatedRequest();
            }else if(choice == 4){
                if(u.getPrivilege().equals("admin")){
                    handleAdminRelatedRequest();
                }else{
                    System.err.println("Under-privileged user!");
                }
            }else{
                System.err.println("Invalid choice!");
            }
        }
    }
}
