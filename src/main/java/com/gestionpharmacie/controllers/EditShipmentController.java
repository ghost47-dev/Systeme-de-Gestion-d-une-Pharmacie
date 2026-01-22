package com.gestionpharmacie.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.utilities.DatabaseConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;



public class EditShipmentController {

    @FXML
    private TextField supplierId ;
    @FXML
    private DatePicker requestDate , arrivalDate;
    @FXML 
    private CheckBox isReceived , notReceived; 
    @FXML 
    private TextField  productQuantity, productPrice;
    @FXML
    private Label errorLabel;
    
    private int product_Id;
    private int shipmentId ;
    private String privilege;
    public void setPrivilege(String privilege){this.privilege = privilege;}
    @FXML
    private void goBack(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionpharmacie/main.fxml"));
            loader.setControllerFactory(type -> {
                if (type == MainController.class) {
                    return new MainController(privilege);
                }
                return null;
            });
            Parent root = loader.load();
            MainController mainController = loader.getController();

            mainController.showShipmentsPage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/com/gestionpharmacie/styles.css").toExternalForm()
            );
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    public void showEdit(String shipment){
       
        Pattern pattern = Pattern.compile(   
                "Shipment id : (\\d+)\\n" +
                "Supplier id : (\\d+)\\n" +
                "Supplier name : ([^|]+) \\| Supplier phone : (\\d+)\\n" +

                // Product block
                "Product id : (\\d+)\\n" +
                "Product name : ([^|]+) \\| Product price : ([0-9]+(?:\\.[0-9]+)?)\\$ \\| Product quantity (\\d+)\\n" +

                // Dates
                "Date of request : ([0-9/\\-]+) \\| Date of arrival : ([0-9/\\-]+)\\n" +

                // Status
                "Shipment is (received ✓|not received 𐄂)"
        );


        Matcher matcher = pattern.matcher(shipment);
        
        if (!matcher.find()) {
            System.out.println(shipment);
            throw new IllegalArgumentException("Invalid shipment format");
        }

        this.shipmentId = Integer.parseInt(matcher.group(1));
        String supplier_id = matcher.group(2);

        product_Id = Integer.parseInt(matcher.group(5));
        String productPrice = matcher.group(7);
        String productQuantity = matcher.group(8);

        String requestDate = matcher.group(9);
        String arrivalDate = matcher.group(10);
        boolean Received = matcher.group(11).equals("received ✓") ? true : false ;
        System.out.println(isReceived);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("d/M/yyyy");
        this.supplierId.setText(supplier_id);
        this.requestDate.setValue(LocalDate.parse(requestDate,formatter));
        this.arrivalDate.setValue(LocalDate.parse(arrivalDate,formatter));
        this.isReceived.setSelected(Received);
        this.notReceived.setSelected(!Received);
        this.productPrice.setText(productPrice);
        this.productQuantity.setText(productQuantity);
        
    }
    @FXML 
    private void editShipment(ActionEvent event ){
        LocalDate arrivalDateTmp = arrivalDate.getValue();
        LocalDate requestDateTmp = requestDate.getValue();
        String supplier_id = supplierId.getText();
        String pQuantity = productQuantity.getText();
        String pPrice = productPrice.getText();
        
        Date  arrival , request;
        int supId ,  quantity;
        double price;
        if (!supplier_id.isEmpty() &&
            arrivalDateTmp != null &&
            requestDateTmp != null &&
            (isReceived.isSelected() ^ notReceived.isSelected()) &&
            !pQuantity.isEmpty() &&
            !pPrice.isEmpty() 
            ){
            ShipmentManager shipmentManager = new ShipmentManager(DatabaseConnection.getConnection());
            
            try {
                supId = Integer.parseInt(supplier_id);
                Supplier supplier = shipmentManager.fetchSupplier(supId);
                if (supplier == null){
                    errorLabel.setText("Invalid supplier id !");
                    errorLabel.setVisible(true);
                    return ;
                }
            }
            catch (NumberFormatException e){
               errorLabel.setText("Invalid price !");
               errorLabel.setVisible(true); 
               return;
            }

            if (requestDateTmp == null || arrivalDateTmp == null) {
                errorLabel.setText("Please select both dates!");
                errorLabel.setVisible(true);
                return;
            }

            if (!arrivalDateTmp.isAfter(requestDateTmp)) {
                errorLabel.setText("Arrival date must be AFTER request date!");
                errorLabel.setVisible(true);
                return;
            }
            
            request = Date.from(requestDateTmp.atStartOfDay(ZoneId.systemDefault()).toInstant());
            arrival = Date.from(arrivalDateTmp.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            try {
                 quantity = Integer.parseInt(pQuantity);
            }
            catch (NumberFormatException e){
               errorLabel.setText("Invalid quantity !");
               errorLabel.setVisible(true); 
               return;
            }
            try {
                 price = Double.parseDouble(pPrice);
            }
            catch (NumberFormatException e){
               errorLabel.setText("Invalid price!");
               errorLabel.setVisible(true); 
               return;
            }           
            errorLabel.setVisible(false);  

            
            try {
                shipmentManager.updateShipment(shipmentId ,
                        supId, 
                        request,
                        isReceived.isSelected() && !notReceived.isSelected(),
                        arrival );
                shipmentManager.updateShipmentGood(product_Id , quantity , price);
            }
            catch (ShipmentNotFoundException e){
                return;
            }

        }
    }

}
