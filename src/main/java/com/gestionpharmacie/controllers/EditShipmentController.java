package com.gestionpharmacie.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gestionpharmacie.Globals;
import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;

public class EditShipmentController {
    @FXML
    private TextField supplierId ;
    @FXML
    private DatePicker requestDate , arrivalDate;
    @FXML
    private CheckBox isReceived , notReceived;
    @FXML
    private Label errorLabel;

    private VBox shipmentsGoodContainer;

    private int shipmentId ;

    private Scene scene;

    public EditShipmentController(){
        scene = Globals.scenes.loadScene("shipmentEdit.fxml", this);
    }

    public void show(){
        Globals.stage.setScene(scene);
    }

    @FXML
    private void goBack(ActionEvent event) {
        Globals.controllers.main.show();
    }
    @FXML
    public void showEdit(String shipment){

        Pattern pattern = Pattern.compile(
                "(?s)Shipment id : (\\d+)\\n.*" 
        );


        Matcher matcher = pattern.matcher(shipment);

        if (!matcher.find()) {
            System.out.println(shipment);
            throw new IllegalArgumentException("Invalid shipment format");
        }

        ShipmentManager shipmentManager = Globals.managers.shipment;
        Shipment sh = shipmentManager.fetchShipment(shipmentId);
        ArrayList<ShipmentGood> shipmentGood = shipmentManager.fetchShipmentGood(shipmentId);
        
        int supplier_id = sh.getSupplierId();
        Date request = sh.getRequestDate();
        Date arrival = sh.getRecievalDate();
        boolean Received = sh.isReceived();
        
        supplierId.setText(String.valueOf(supplier_id));

        this.requestDate.setValue(
                 ((java.sql.Date) request).toLocalDate()
        );

        this.arrivalDate.setValue(
                ((java.sql.Date) arrival).toLocalDate()
        );
        this.isReceived.setSelected(Received);
        this.notReceived.setSelected(!Received);

        for (ShipmentGood sg : shipmentGood){
            
            int product_Id = sg.getProductId();
            double productPrice = sg.getPrice();
            int productQuantity = sg.getQuantity();

            addGoodRow(product_Id, productQuantity, productPrice);
        }
    }
    @FXML
    private void editShipment(ActionEvent event ){
        LocalDate arrivalDateTmp = arrivalDate.getValue();
        LocalDate requestDateTmp = requestDate.getValue();
        String supplier_id = supplierId.getText();
        ObservableList<Node> productRows = shipmentsGoodContainer.getChildren();

        Date  arrival , request;
        int supId;
        ShipmentManager shipmentManager = Globals.managers.shipment;
        if (!supplier_id.isEmpty() &&
            arrivalDateTmp != null &&
            requestDateTmp != null &&
            (isReceived.isSelected() ^ notReceived.isSelected()) 
            ){

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
               supplierId.clear();
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
                shipmentManager.updateShipment(shipmentId ,
                        supId,
                        request,
                        isReceived.isSelected() && !notReceived.isSelected(),
                        arrival );
            }
            catch (ShipmentNotFoundException e){
                return;
            }
        }else{
            errorLabel.setVisible(true);
            errorLabel.setText("Please fill all fields !");
            return;
        }
        ArrayList<ShipmentGood> sgs = shipmentManager.fetchShipmentGood(shipmentId);
        int i = 0;
        for (Node n : productRows){
            HBox current_row = (HBox)n;

            TextField priceField = (TextField)current_row.getChildren().get(2);
            TextField quantityField = (TextField)current_row.getChildren().get(1);
            String price = priceField.getText();
            String quantity = quantityField.getText();
            int productQuantity;
            double productPrice;
            try {
                if (price.isEmpty())
                    throw new NumberFormatException();
                productQuantity = Integer.parseInt(quantity);                   
                errorLabel.setVisible(false);
            }
            catch (NumberFormatException ex) {
                errorLabel.setText("Invalid quantity !");
                quantityField.clear();
                errorLabel.setVisible(true);
                return;
            } 

            try {
                if (price.isEmpty()) 
                    throw new NumberFormatException();
                productPrice = Double.parseDouble(price);                   
                errorLabel.setVisible(false);
            }
            catch (NumberFormatException ex) {
                System.out.println(price);
                errorLabel.setText("Invalid price !");
                priceField.clear();
                errorLabel.setVisible(true);
                return;
            }
            ShipmentGood sg = sgs.get(i++);
            try {
                shipmentManager.updateShipmentGood(sg.getId() , sg.getProductId() , productQuantity , productPrice);
            }
            catch (ProductNotFoundException e){
                e.printStackTrace();
                return;
            } 
        }
        errorLabel.setVisible(false);  
        goBack(event);
    }
    @FXML
    private void addGoodRow(int id , int quantity , double price) {

        HBox goodRow = new HBox(10);
        goodRow.setAlignment(Pos.CENTER);

        Label idLabel = new Label("Product " + String.valueOf(id) + " : ");
        idLabel.getStyleClass().add("text");

        TextField pQuantity = new TextField();
        pQuantity.setText(String.valueOf(quantity));

        TextField pPrice = new TextField();
        pPrice.setText(String.valueOf(price));

        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("close-button");

        goodRow.getChildren().addAll(
                idLabel ,pQuantity ,pPrice , closeBtn
        );

        shipmentsGoodContainer.getChildren().add(goodRow);
    }
}
