package com.gestionpharmacie.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.gestionpharmacie.Globals;
import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.model.Product;

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
    private String privilege;
    public void setPrivilege(String privilege){this.privilege=privilege;}
    @FXML
    public void initialize (){
        addGoodRow();
    }

    private Scene scene;

    public AddShipmentController(){
        scene = Globals.scenes.loadScene("shipmentAdd.fxml", this);
    }

    public void show(){
        Globals.stage.setScene(scene);
    }

    @FXML
    private void addGoodRow() {
        shipmentGoodCount++;

        HBox goodRow = new HBox(10);
        goodRow.setAlignment(Pos.CENTER);

        TextField pName = new TextField();
        pName.setPromptText("Product name");

        TextField pQuantity = new TextField();
        pQuantity.setPromptText("Product quantity");

        TextField pPrice = new TextField();
        pPrice.setPromptText("Product price");

        goodRow.getChildren().addAll(
                new Label("Product " + shipmentGoodCount + ":"),
                pName ,pQuantity ,pPrice
        );

        shipmentsGoodContainer.getChildren().add(goodRow);
    }


    @FXML
    private void saveShipment(ActionEvent event) {
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
                if (phone.length() != 8)
                    throw new NumberFormatException();
            }
            catch (NumberFormatException e){
               errorLabel.setText("Invalid phone number !");
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


            ProductManager productManager = Globals.managers.product;
            ShipmentManager shipmentManager = Globals.managers.shipment;


           for (Node row : shipmentsGoodContainer.getChildren()){

                HBox goodRow =(HBox) row;

                int productQuantity;
                double productPrice;

                TextField Name = (TextField)goodRow.getChildren().get(1);
                if (Name.getText().isEmpty()){Name.clear();return;}

                TextField quantity = null;
                try {
                    quantity = (TextField)goodRow.getChildren().get(2);
                    if (quantity.getText().isEmpty()) {quantity.clear();return;}
                    productQuantity = Integer.parseInt(quantity.getText());
                    errorLabel.setVisible(false);
                }
                catch (NumberFormatException ex) {
                    errorLabel.setText("Invalid quantity !");
                    quantity.clear();
                    errorLabel.setVisible(true);
                    return;
                }
                TextField price = null;
                try {
                    price = (TextField)goodRow.getChildren().get(3);
                    if (price.getText().isEmpty()) {price.clear();return;}
                    productPrice = Double.parseDouble(price.getText());
                    errorLabel.setVisible(false);
                }
                catch (NumberFormatException ex) {
                    System.out.println(price.getText());
                    errorLabel.setText("Invalid price !");
                    price.clear();
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

                    try {
                        Product product = productManager.fetchProduct(Name.getText());
                        shipmentManager.addShipmentGood(shipment_id, product.getId(), productPrice, productQuantity);
                    }
                    catch (ProductNotFoundException e){
                        int product_id = productManager.addProduct(Name.getText(), productPrice, 0);
                        shipmentManager.addShipmentGood(shipment_id, product_id, productPrice, productQuantity);
                    }

                }
                catch (ShipmentNotFoundException e){
                    e.printStackTrace();
                    return ;
                }
           }
            goBack(event);
        }
    }

    @FXML
    private void goBack(javafx.event.ActionEvent event) {
        Globals.controllers.main.show();
    }
}
