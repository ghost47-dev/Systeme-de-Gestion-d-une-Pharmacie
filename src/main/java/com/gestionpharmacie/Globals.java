package com.gestionpharmacie;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import com.gestionpharmacie.controllers.*;
import com.gestionpharmacie.managers.*;

public class Globals {
    private static Globals instance;

    public static Config config;
    public static Scenes scenes;
    public static Database database;
    public static Stage stage;
    public static Controllers controllers;
    public static Managers managers;
    public static Tab currentTab;

    public static String privilege;

    public static void init(){
        if(instance == null){
            instance = new Globals();
        }
    }

    private Globals() {
        config = new Config();
        scenes = new Scenes();
        database = new Database();
        controllers = new Controllers();
        managers = new Managers();
        currentTab = Tab.SALE;
    }

    public class Config {
        public String resourcePath;
        public String databaseUser;
        public String databasePassword;
    }

    public class Scenes {
        private String styles;

        public Scene loadScene(String name, Object cont){
            String path = Globals.config.resourcePath + name;
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                loader.setController(cont);
                Scene s = new Scene(loader.load());
                s.getStylesheets().add(styles);
                return s;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        public void loadStyles(){
            styles = getClass().getResource(Globals.config.resourcePath + "styles.css").toExternalForm();
        }
    }

    public class Database {
        private boolean connected = false;
        private Connection connection;

        public void connect(){
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/pharmacy",
                        Globals.config.databaseUser,
                        Globals.config.databasePassword
                );
                System.out.println("Successfully connected to DB!");
                connected = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public Connection getConnection() {
            if(!connected){
                System.err.println("The DB is not connected yet!");
                return null;
            }
            return connection;
        }
    }

    public class Controllers {
        public AddShipmentController addShipment;
        public EditShipmentController editShipment;
        public EditSupplierController editSupplier;
        public LoginController login;
        public MainController main;
        public AddProductController addProduct;
        public EditProductController editProduct;
        public AddSaleController addSale;

        void init(){
            addShipment  = new AddShipmentController();
            editShipment = new EditShipmentController();
            editSupplier = new EditSupplierController();
            login        = new LoginController();
            main         = new MainController();
            addProduct   = new AddProductController();
            editProduct  = new EditProductController();
            addSale      = new AddSaleController();
        }
    }

    public class Managers {
        public ProductManager product;
        public SaleManager sale;
        public ShipmentManager shipment;
        public UserManager user;

        void init(){
            product  = new ProductManager();
            sale     = new SaleManager();
            shipment = new ShipmentManager();
            user     = new UserManager();
        }
    }

    public enum Tab {
        SALE,
        STOCK,
        SHIPMENTS,
        SUPPLIERS,
        REVENUE,
        ACCOUNT,
    }
}
