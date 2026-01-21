package com.gestionpharmacie.Controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.utilities.DatabaseConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;



public class editShipmentController {

    @FXML
    private TextField supplierPhone , supplierName ;
    @FXML
    private DatePicker requestDate , arrivalDate;
    @FXML 
    private CheckBox isReceived , notReceived; 
    @FXML 
    private TextField productId, productQuantity, productPrice;
    @FXML
    private Label errorLabel;

    private int shipmentId ;
    private int supplierId;
    private String shipment ;
    @FXML
    private void goBack(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionpharmacie/main.fxml"));
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
       
        this.shipment = shipment;
        Pattern pattern = Pattern.compile(
            "Shipment id : (\\d+)\\s+" +
            "Supplier id : (\\d+)\\s+" +
            "Supplier name : (.+?) \\| Supplier phone : (\\d+)\\s+" +
            "Product id : (\\d+)\\s+" +
            "Product name : (.+?) \\| Product price : (\\d+)\\$ \\| Product quantity (\\d+)\\s+" +
            "Date of request : (\\d{1,2}/\\d{1,2}/\\d{4}) \\| Date of arrival(\\d{1,2}/\\d{1,2}/\\d{4})"
        );


        Matcher matcher = pattern.matcher(shipment);
        
        this.shipment = shipment ;

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid shipment format");
        }

        this.shipmentId = Integer.parseInt(matcher.group(1));
        this.supplierId = Integer.parseInt(matcher.group(2));
        String supplierName = matcher.group(3);
        String supplierPhone = matcher.group(4);

        String productId = matcher.group(5);
        String productPrice = matcher.group(7);
        String productQuantity = matcher.group(8);

        String requestDate = matcher.group(9);
        String arrivalDate = matcher.group(10);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("d/M/yyyy");
        this.supplierName.setText(supplierName);
        this.supplierPhone.setText(supplierPhone);
        this.requestDate.setValue(LocalDate.parse(requestDate,formatter));
        this.arrivalDate.setValue(LocalDate.parse(arrivalDate,formatter));
        if(this.arrivalDate.getValue().isAfter(this.requestDate.getValue())){
            this.isReceived.setSelected(false);
            this.notReceived.setSelected(true);
        }
        else {
            this.isReceived.setSelected(true);
            this.notReceived.setSelected(false);
        }
        this.productId.setText(productId);
        this.productPrice.setText(productPrice);
        this.productQuantity.setText(productQuantity);
        
    }
    @FXML 
    private void editShipment(ActionEvent event ){
        String name = supplierName.getText();
        LocalDate arrivalDateTmp = arrivalDate.getValue();
        LocalDate requestDateTmp = requestDate.getValue();
        String phone = supplierPhone.getText();
        String pQuantity = productQuantity.getText();
        String pPrice = productPrice.getText();
        String pId = productId.getText();
        
        Date  arrival , request;
        int Phone , id , quantity;
        double price;
        if (!name.isEmpty() && 
            !phone.isEmpty() &&
            arrivalDateTmp != null &&
            requestDateTmp != null &&
            (isReceived.isSelected() ^ notReceived.isSelected()) &&
            !pQuantity.isEmpty() &&
            !pPrice.isEmpty() &&
            !pId.isEmpty()
            ){
            
            try {
                Phone = Integer.parseInt(phone);
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
                 id = Integer.parseInt(pId);
            }
            catch (NumberFormatException e){
               errorLabel.setText("Invalid id !");
               errorLabel.setVisible(true); 
               return;
            }
            try {
                 quantity = Integer.parseInt(pQuantity);
            }
            catch (NumberFormatException e){
               errorLabel.setText("Invalid quantity !");
               errorLabel.setVisible(true); 
               return;
            }
            try {
                 price = Integer.parseInt(pPrice);
            }
            catch (NumberFormatException e){
               errorLabel.setText("Invalid price!");
               errorLabel.setVisible(true); 
               return;
            }           
            errorLabel.setVisible(false);  

            ShipmentManager shipmentManager = new ShipmentManager(DatabaseConnection.getConnection());
            
            try {
            shipmentManager.updateShipment(shipmentId ,
                    supplierId ,
                    request,
                    isReceived.isSelected() && !notReceived.isSelected(),
                    arrival ,
                    id,
                    quantity ,
                    price
            );
            }
            catch (ShipmentNotFoundException e){
                return;
            }
            int supplier_id = shipmentManager.addSupplier(name,Phone);
            
            int shipment_id = shipmentManager.addShipment(supplier_id
                    , request
                    , isReceived.isSelected() && !notReceived.isSelected() 
                    , arrival
                    );
           for (Node row : shipmentsGoodContainer.getChildren()){

                HBox goodRow =(HBox) row;
               
                int productId,productQuantity,productPrice;

                try{
                    TextField id = (TextField)goodRow.getChildren().get(1);
                    productId = Integer.parseInt(id.getText());
                     errorLabel.setVisible(false);
                }
                catch (NumberFormatException e){
                    errorLabel.setText("Invalid id !");
                    errorLabel.setVisible(true);
                    return;
                }

                try {
                    TextField quantity = (TextField)goodRow.getChildren().get(2);
                    productQuantity = Integer.parseInt(quantity.getText());                   
                    errorLabel.setVisible(false);
                }
                catch (NumberFormatException ex) {
                    errorLabel.setText("Invalid quantity !");
                    errorLabel.setVisible(true);
                    return;
                } 
                try {
                    TextField price = (TextField)goodRow.getChildren().get(3);
                    productPrice = Integer.parseInt(price.getText());                   
                    errorLabel.setVisible(false);
                }
                catch (NumberFormatException ex) {
                    errorLabel.setText("Invalid price !");
                    errorLabel.setVisible(true);
                    return;
                }       
                
                int shipment_manager = shipmentManager.addShipmentGood(shipment_id, productId, productPrice, productQuantity);
                
           } 
    }


}
