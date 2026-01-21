package com.gestionpharmacie.Controllers;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.utilities.DatabaseConnection;

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

public class AddShipmentController {

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

    @FXML
    private VBox shipmentsGoodContainer;
    private int shipmentGoodCount = 0;
    
    
    @FXML
    public void initialize (){
        addGoodRow();
    }

    @FXML
    private void addGoodRow() {
        shipmentGoodCount++;

        HBox goodRow = new HBox(10);
        goodRow.setAlignment(Pos.CENTER);

        TextField pId = new TextField();
        pId.setPromptText("Product id");
        
        TextField pName = new TextField();
        pName.setPromptText("Product name");

        TextField pQuantity = new TextField();
        pQuantity.setPromptText("Product quantity");

        TextField pPrice = new TextField();
        pPrice.setPromptText("Product price");

        goodRow.getChildren().addAll(
                new Label("Product " + shipmentGoodCount + ":"),
                pId ,pName ,pQuantity ,pPrice
        );

        shipmentsGoodContainer.getChildren().add(goodRow);
    }


    @FXML
    private void saveShipment() {
        String name = supplierName.getText();
        LocalDate arrivalDateTmp = arrivalDate.getValue();
        LocalDate requestDateTmp = requestDate.getValue();
        String phone = supplierPhone.getText();
        
        
        Date  arrival , request;
        int Phone;
        if (!name.isEmpty() && 
            !phone.isEmpty() &&
            arrivalDateTmp != null &&
            requestDateTmp != null &&
            ( isReceived.isSelected() ^ notReceived.isSelected() )
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

            errorLabel.setVisible(false);  

            

            ShipmentManager shipmentManager = new ShipmentManager(DatabaseConnection.getConnection());

            
           for (Node row : shipmentsGoodContainer.getChildren()){

                HBox goodRow =(HBox) row;
               
                int productId,productQuantity;
                double productPrice;

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
                TextField Name = (TextField)goodRow.getChildren().get(2);

                try {
                    TextField quantity = (TextField)goodRow.getChildren().get(3);
                    productQuantity = Integer.parseInt(quantity.getText());                   
                    errorLabel.setVisible(false);
                }
                catch (NumberFormatException ex) {
                    errorLabel.setText("Invalid quantity !");
                    errorLabel.setVisible(true);
                    return;
                } 
                try {
                    TextField price = (TextField)goodRow.getChildren().get(4);
                    productPrice = Double.parseDouble(price.getText());                   
                    errorLabel.setVisible(false);
                }
                catch (NumberFormatException ex) {
                    errorLabel.setText("Invalid price !");
                    errorLabel.setVisible(true);
                    return;
                }       
                try { 
                    int supplier_id = shipmentManager.addSupplier(name,Phone);
                    int shipment_id = shipmentManager.addShipment(supplier_id,
                            request,
                            isReceived.isSelected() && !notReceived.isSelected() ,
                            arrival
                    );
                    int shipment_manager = shipmentManager.addShipmentGood(shipment_id, productId, productPrice, productQuantity);
                }
                catch (ShipmentNotFoundException e){
                    e.printStackTrace();
                    return ;
                }
           }   



        }
    }
    @FXML
    private void goBack(javafx.event.ActionEvent event) {
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
} 
