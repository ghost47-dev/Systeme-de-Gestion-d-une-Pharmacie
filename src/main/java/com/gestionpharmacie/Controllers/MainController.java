package com.gestionpharmacie.Controllers;


import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.SaleManager;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.Sale;
import com.gestionpharmacie.model.SaleProduct;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.utilities.DatabaseConnection;
import com.gestionpharmacie.model.Client;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class MainController {
   
    @FXML private HBox sidePanelContainer;
    @FXML private VBox sidePanel;
    @FXML private Button toggleButton;
    @FXML private VBox togglePanel;    
    // Navigation buttons
    @FXML private Button saleHistoryBtn;
    @FXML private Button productsBtn;
    @FXML private Button shipmentsBtn;
    @FXML private Button suppliersBtn;
    
    // Pages
    @FXML private VBox saleHistoryPage;
    @FXML private VBox productsPage;
    @FXML private VBox shipmentsPage;
    @FXML private VBox suppliersPage;
    
    @FXML private HBox editingBtns_product;
    @FXML private HBox editingBtns_shipment; 
    
    @FXML private ListView<String> saleHistoryList;
    @FXML private ListView<String> productsList;
    @FXML private ListView<String> shipmentsList;
    @FXML private ListView<String> suppliersList;
    @FXML private Button editSupplierBtn;

    private boolean isPanelVisible = true;
    private static final double PANEL_WIDTH = 200.0;
    
    @FXML
    public void initialize() {
        // Set initial active button
        showSaleHistory();
    }
    
    /**
     * Toggle side panel visibility with smooth animation
     */
    @FXML
    private void toggleSidePanel() {

        double start = isPanelVisible ? PANEL_WIDTH : 0;
        double end   = isPanelVisible ? 0 : PANEL_WIDTH;

        Timeline timeline = new Timeline();

        KeyFrame startFrame = new KeyFrame(
            Duration.ZERO,
            new KeyValue(sidePanel.prefWidthProperty(), start),
            new KeyValue(sidePanel.minWidthProperty(), start),
            new KeyValue(sidePanel.maxWidthProperty(), start)
        );

        KeyFrame endFrame = new KeyFrame(
            Duration.millis(300),
            new KeyValue(sidePanel.prefWidthProperty(), end),
            new KeyValue(sidePanel.minWidthProperty(), end),
            new KeyValue(sidePanel.maxWidthProperty(), end)
        );

        timeline.getKeyFrames().addAll(startFrame, endFrame);
        timeline.play();

        toggleButton.setText(isPanelVisible ? "▶" : "◀");
        isPanelVisible = !isPanelVisible;
    }


    
    /**
     * Show home page
     */
    @FXML
    private void showSaleHistory() {
        
        
        try (Connection connection = DatabaseConnection.getConnection()){ 

            ProductManager productManager = new ProductManager(connection);
            SaleManager saleManager = new SaleManager(productManager,connection);
            
            ArrayList<Integer> sales = saleManager.getSaleIds();
            
            ObservableList<String> items = FXCollections.observableArrayList();
            
            for (int sale_id :sales ){
                
                Sale sale = saleManager.fetchSale(sale_id); 
                int client_id = sale.getClientId();
                Client client = saleManager.fetchClient(client_id);

                ArrayList<SaleProduct> saleproducts = saleManager.getSaleProducts(sale_id);

                String Products_Info = "";
                for (SaleProduct sp : saleproducts){
                    int p_id = sp.getProductId();
                    Product p = productManager.fetchProduct(p_id); 
                    int p_quantity = sp.getQuantity();
                    double p_price = p.getPrice();
                    String p_name = p.getName();
                    
                    Products_Info += "Product: " + p_name + " | Price: $"+ p_price +" | Quantity: "+p_quantity;
                    if (sp != saleproducts.getLast() )Products_Info += "\n";

                }
                
                items.add(
                    "Sale ID: "+ sale_id +"\n" +
                    client.toString() + "\n" +
                    Products_Info
                    );

            }
             

            saleHistoryList.getItems().setAll(items);
            saleHistoryList.refresh();
            
            showPage(saleHistoryPage);
            setActiveButton(saleHistoryBtn);

        }
        catch (SQLException e){
            e.printStackTrace();
        }


    }
    @FXML
    private void addSale(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/gestionpharmacie/saleAdd.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/gestionpharmacie/styles.css").toExternalForm()
            );
            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
            stage.setScene(scene);       
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Show dashboard page
     */
    @FXML
    private void editProductRedirection(ActionEvent event , String product){

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionpharmacie/productEdit.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/gestionpharmacie/styles.css").toExternalForm()
            );
            
            ProductController pc = loader.getController();
            
            pc.showEdit(product);

            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
            stage.setScene(scene);       
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void removeProduct(String product){
        if (product == null) return;
        String tmp = product.substring(13,product.indexOf("\n", 0)).strip();
        int id = 0;
        try {
            id = Integer.parseInt(tmp);
        }
        catch(NumberFormatException e) {
            System.out.println("error while parsing id ");
        }
        ProductManager pm = new ProductManager(DatabaseConnection.getConnection());
        try {
            pm.deleteProduct(id);   
            System.out.println("product deleted !");
            productsList.getItems().remove(product);
            editingBtns_product.getChildren().remove(1);
            editingBtns_product.getChildren().remove(1);
            productsList.refresh();
        }
        catch (ProductNotFoundException e){
            return ;
        }

    }
    @FXML
    public void showProductsPage() {
        
        try (Connection connection = DatabaseConnection.getConnection()){ 

            ProductManager productManager = new ProductManager(connection);
            
            ArrayList<Product> products = productManager.viewStock();
             
            ObservableList<String> items = FXCollections.observableArrayList();
            
            for (Product p : products){
                String name = p.getName();
                int id = p.getId();
                double price = p.getPrice();
                int quantity = p.getQuantity();

                items.add(
                   "Product id : " + id + "\n" +
                   "Product name : " + name + " | Product price : " + price + " | Product quantity " + quantity  
                        );

            } 
             
            
            productsList.getItems().setAll(items);
            productsList.refresh();

            productsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = productsList.getSelectionModel().getSelectedItem();
                        if (selected != null) { 
                            if (editingBtns_product.getChildren().size() == 1){
                                Button editBtn = new Button();
                                editBtn.getStyleClass().add("edit-btn");
                                editBtn.setText("edit");
                                editBtn.setOnAction(editEvent -> {
                                   editProductRedirection(editEvent,selected); 
                                }); 

                                Button removeBtn = new Button();
                                removeBtn.getStyleClass().add("delete-btn");
                                removeBtn.setText("delete");
                                removeBtn.setOnAction(deleteEvent -> {
                                    removeProduct(selected);
                                });    

                                editingBtns_product.getChildren().addAll(editBtn,removeBtn);
                            }
                            if (editingBtns_product.getChildren().size() == 3){
                                Button editBtn = (Button)editingBtns_product.getChildren().get(1);
                                 editBtn.setOnAction(editEvent -> {
                                   editProductRedirection(editEvent,selected);
                                });
                                Button removeBtn = (Button)editingBtns_product.getChildren().getLast();
                                removeBtn.setOnAction(deleteEvent -> {
                                    removeProduct(selected);
                                });
                            }
                            
                        }
                    }
            });


            showPage(productsPage);
            setActiveButton(productsBtn);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void addProduct(ActionEvent event){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/gestionpharmacie/productAdd.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/gestionpharmacie/styles.css").toExternalForm()
            );
            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
            stage.setScene(scene);       
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    

    @FXML
    private void addShipment(ActionEvent event){
         try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/gestionpharmacie/shipmentAdd.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/gestionpharmacie/styles.css").toExternalForm()
            );
            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
            stage.setScene(scene);       
        }
        catch(Exception e) {
            e.printStackTrace();
        }       


    }

    @FXML
    public void showShipmentsPage() {
        try (Connection connection = DatabaseConnection.getConnection()) { 
//
            ShipmentManager sm = new ShipmentManager(connection);

            ArrayList<Shipment> shipments = sm.fetchShipments();
             //
            ObservableList<String> items = FXCollections.observableArrayList();
            //
            for (Shipment s : shipments){

                int supplier_id = s.getSupplierId();
                Date requestDate = s.getRequestDate();
                Date receivalDate = s.getRecievalDate();
                boolean isReceived = s.isReceived();

                Supplier supplier = sm.fetchSupplier(supplier_id);
                String supplierName = supplier.getName();
                int supplierPhone = supplier.getNumerotel();

                ArrayList<ShipmentGood> shipmentGood ; 
                try {
                    shipmentGood = sm.fetchShipmentGood(s.getId());
                }
                catch (ShipmentNotFoundException e){
                    e.printStackTrace();
                    return;
                }
                if (shipmentGood.size() == 0) {System.out.println("shipmentGoodisEmpty"); return;}
                String shipmentGoodInfo = "";
                ProductManager pm = new ProductManager(DatabaseConnection.getConnection());
                for (ShipmentGood sg : shipmentGood){
                    double price = sg.getPrice();  
                    int quantity = sg.getQuantity();
                    
                    Product product = pm.fetchProduct(sg.getProductId());
                    String name = product.getName();
                    shipmentGoodInfo = "Product id : " + product.getId() + "\n" +
                    "Product name : " + name + " | Product price : " + price + "$" + " | Product quantity " + quantity + "\n" ; 
                }
                

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                if (isReceived)
                items.add(
                    "Shipment id : " + s.getId() + "\n" +
                    "Supplier id : " + supplier_id + "\n" +
                    "Supplier name : " + supplierName + " | Supplier phone : " + supplierPhone + "\n" +
                    shipmentGoodInfo +
                    "Date of request : " + formatter.format(requestDate) + " | Date of arrival : " + formatter.format(receivalDate) + "\n" +
                    "Shipment is received ✓"
                );
                else 
                items.add(
                    "Shipment id : " + s.getId() + "\n" +
                    "Supplier id : " + supplier_id + "\n" +
                    "Supplier name : " + supplierName + " | Supplier phone : " + supplierPhone + "\n" +
                    shipmentGoodInfo +
                    "Date of request : " + formatter.format(requestDate) + " | Date of arrival : " + formatter.format(receivalDate) + "\n" + 
                    "Shipment is not received 𐄂"
                );
                 
            } 
             //
            //
            shipmentsList.getItems().setAll(items);
            shipmentsList.refresh();
//
            shipmentsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = shipmentsList.getSelectionModel().getSelectedItem();
                        if (selected != null) { 
                            if (editingBtns_shipment.getChildren().size() == 1){
                                

                                Button editBtn = new Button();
                                editBtn.getStyleClass().add("edit-btn");
                                editBtn.setText("edit");
                                editBtn.setOnAction(editEvent -> {
                                   editShipmentRedirection(editEvent,selected); 
                                }); 


                                Button cancelBtn = new Button();
                                cancelBtn.getStyleClass().add("delete-btn");
                                cancelBtn.setText("cancel");
                                cancelBtn.setOnAction(deleteEvent -> {
                                    removeShipment(selected);
                                });

                                Button receivedBtn = new Button();
                                receivedBtn.getStyleClass().add("add-btn");
                                receivedBtn.setText("mark received");
                                receivedBtn.setOnAction(receivedEvent -> {
                                    markShipmentReceived(selected);
                                }); 

                                editingBtns_shipment.getChildren().addAll(editBtn,cancelBtn,receivedBtn);
                            }
                            if (editingBtns_shipment.getChildren().size() == 3){
                                Button editBtn = (Button)editingBtns_shipment.getChildren().get(1);
                                 editBtn.setOnAction(editEvent -> {
                                   editShipmentRedirection(editEvent,selected);
                                });
                                Button removeBtn = (Button)editingBtns_shipment.getChildren().getLast();
                                removeBtn.setOnAction(deleteEvent -> {
                                    removeShipment(selected);
                                });
                                Button receivedBtn = (Button)editingBtns_shipment.getChildren().getLast();
                                receivedBtn.setOnAction(receivedEvent -> {
                                    markShipmentReceived(selected);
                                }); 
                            }
                            
                        }
                    }
            });
//
//
            showPage(shipmentsPage);
            setActiveButton(shipmentsBtn);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    @FXML 
    private void removeShipment(String shipment){
        if (shipment == null) return ; 
        Pattern p = Pattern.compile("Shipment id : (\\d+)\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+"); 
        Matcher matcher = p.matcher(shipment);

        if (!matcher.find()){
            throw new IllegalArgumentException();  
        }

        int shipment_id = Integer.parseInt(matcher.group(1));
        
        ShipmentManager sm = new ShipmentManager(DatabaseConnection.getConnection());
        try {
            sm.cancelShipment(shipment_id);
            shipmentsList.getItems().remove(shipment);
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            shipmentsList.refresh();
        }
        catch (ShipmentNotFoundException e){
            return ;
        }
    }
    @FXML
    private void editShipmentRedirection(ActionEvent event , String shipment){

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionpharmacie/shipmentEdit.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/gestionpharmacie/styles.css").toExternalForm()
            );
            
            EditShipmentController sc = loader.getController();
            
            sc.showEdit(shipment);

            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
            stage.setScene(scene);       
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    @FXML 
    private void markShipmentReceived(String shipment){
        if (shipment == null) return ; 
        Pattern p = Pattern.compile("Shipment id : (\\d+)\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+"); 
        Matcher matcher = p.matcher(shipment);

        if (!matcher.find()){
            throw new IllegalArgumentException();  
        }

        int shipment_id = Integer.parseInt(matcher.group(1));
        
        ShipmentManager sm = new ShipmentManager(DatabaseConnection.getConnection());
        try {
            sm.receiveShipment(
                    shipment_id,
                    Date.from(
                        LocalDate.now()
                        .atStartOfDay( ZoneId.systemDefault() )
                        .toInstant()
                    )
            );
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            shipmentsList.refresh();
            showShipmentsPage();
        }
        catch (ShipmentNotFoundException e){
            return ;
        } catch (ProductNotFoundException e) {
            return ;
        }

    }

    @FXML
    private void editSupplierRedirection(ActionEvent event , String supplier){
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/gestionpharmacie/supplierEdit.fxml")
            );
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/gestionpharmacie/styles.css").toExternalForm()
            );
            
            EditSupplierController sc = loader.getController();
            
            sc.showEdit(supplier);

            Stage stage = (Stage) ((Node) event.getSource())
                .getScene().getWindow();
            stage.setScene(scene);       
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void showSuppliersPage() {
       try (Connection connection = DatabaseConnection.getConnection()){

            suppliersList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = suppliersList.getSelectionModel().getSelectedItem();
                    
                    if (selected != null){

                        if (!editSupplierBtn.isVisible()){
                            
                            editSupplierBtn.setVisible(true);
                            editSupplierBtn.setOnAction(editEvent -> {
                                editSupplierRedirection(editEvent , selected);
                            });
                        }
                        else {
                             editSupplierBtn.setOnAction(editEvent -> {
                                editSupplierRedirection(editEvent , selected);
                            });
                        }   
                    }
                }
            }); 

            ObservableList<String> items = FXCollections.observableArrayList();
            
            ShipmentManager sm = new ShipmentManager(connection);
            ArrayList<Supplier> suppliers = sm.fetchSuppliers();
            for (Supplier s : suppliers){
                String name = s.getName();
                int phone = s.getNumerotel();

                String supplierInfo = "Supplier id : " + s.getId() + "\n" +
                    "Supplier name : " + name + " | Supplier phone : " + phone + "\n";
                items.add(supplierInfo);
                 
            }

            suppliersList.getItems().setAll(items);
            suppliersList.refresh();
            showPage(suppliersPage);
            setActiveButton(suppliersBtn);
       }
       catch (SQLException e){
        return ;
       }

    }
    
    /**
     * Hide all pages and show the selected one
     */
    private void showPage(VBox pageToShow) {
        
        if (editingBtns_product.getChildren().size() == 3 ){
            editingBtns_product.getChildren().removeLast();
             editingBtns_product.getChildren().removeLast();
        }
        
        if (editingBtns_shipment.getChildren().size() == 4 ){
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
        }
        editSupplierBtn.setVisible(false);
        saleHistoryPage.setVisible(false);
        productsPage.setVisible(false);
        shipmentsPage.setVisible(false);
        suppliersPage.setVisible(false);
        
        pageToShow.setVisible(true);
    }
    
    private void setActiveButton(Button activeBtn) {
        // Remove active class from all buttons
        saleHistoryBtn.getStyleClass().remove("nav-btn-active");
        productsBtn.getStyleClass().remove("nav-btn-active");
        shipmentsBtn.getStyleClass().remove("nav-btn-active");
        suppliersBtn.getStyleClass().remove("nav-btn-active");
        
        // Add active class to selected button
        if (!activeBtn.getStyleClass().contains("nav-btn-active")) {
            activeBtn.getStyleClass().add("nav-btn-active");
        }
    }
}
