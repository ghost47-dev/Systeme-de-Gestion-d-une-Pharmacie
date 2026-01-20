package com.gestionpharmacie.Controllers;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.SaleManager;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.Sale;
import com.gestionpharmacie.model.SaleProduct;
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
    @FXML private Button settingsBtn;
    @FXML private Button profileBtn;
    
    // Pages
    @FXML private VBox saleHistoryPage;
    @FXML private VBox productsPage;
    @FXML private VBox settingsPage;
    @FXML private VBox profilePage;
    
    @FXML private HBox editingBtns;

    @FXML private ListView<String> saleHistoryList;
    @FXML private ListView<String> productsList;

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
            editingBtns.getChildren().remove(1);
            editingBtns.getChildren().remove(1);
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
                            if (editingBtns.getChildren().size() == 1){
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

                                editingBtns.getChildren().addAll(editBtn,removeBtn);
                            }
                            if (editingBtns.getChildren().size() == 3){
                                Button editBtn = (Button)editingBtns.getChildren().get(1);
                                 editBtn.setOnAction(editEvent -> {
                                   editProductRedirection(editEvent,selected);
                                });
                                Button removeBtn = (Button)editingBtns.getChildren().getLast();
                                removeBtn.setOnAction(deleteEvent -> {
                                    removeProduct(selected);
                                });
                            }
                            
                        }
                    }
            });
            productsList.setOnMouseExited(event -> {
                


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
    
    /**
     * Show settings page
     */
    @FXML
    private void showSettingsPage() {
        showPage(settingsPage);
        setActiveButton(settingsBtn);
    }
    
    /**
     * Show profile page
     */
    @FXML
    private void showProfilePage() {
        showPage(profilePage);
        setActiveButton(profileBtn);
    }
    
    /**
     * Hide all pages and show the selected one
     */
    private void showPage(VBox pageToShow) {
        saleHistoryPage.setVisible(false);
        if (editingBtns.getChildren().size() == 3 ){
            editingBtns.getChildren().removeLast();
             editingBtns.getChildren().removeLast();
        }
        productsPage.setVisible(false);
        settingsPage.setVisible(false);
        profilePage.setVisible(false);
        
        pageToShow.setVisible(true);
    }
    
    /**
     * Set active state for navigation button
     */
    private void setActiveButton(Button activeBtn) {
        // Remove active class from all buttons
        saleHistoryBtn.getStyleClass().remove("nav-btn-active");
        productsBtn.getStyleClass().remove("nav-btn-active");
        settingsBtn.getStyleClass().remove("nav-btn-active");
        profileBtn.getStyleClass().remove("nav-btn-active");
        
        // Add active class to selected button
        if (!activeBtn.getStyleClass().contains("nav-btn-active")) {
            activeBtn.getStyleClass().add("nav-btn-active");
        }
    }
}
