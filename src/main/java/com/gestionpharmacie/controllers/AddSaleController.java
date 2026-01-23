package com.gestionpharmacie.controllers;

import java.sql.Connection;

import com.gestionpharmacie.Globals;
import com.gestionpharmacie.exceptions.InsufficientStockException;
import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.SaleManager;

import javafx.collections.ObservableList;
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

public class AddSaleController {
    @FXML
    private Label errorLabel;

    @FXML
    private TextField clientName, clientSurname, clientPhone;

    @FXML
    private VBox productsContainer;

    private int productCount = 0;

    private String privilege;
    public void setPrivilege(String privilege){this.privilege = privilege;}

    private Scene scene;

    public AddSaleController(){
        scene = Globals.scenes.loadScene("saleAdd.fxml", this);
    }

    public void show(){
        Globals.stage.setScene(scene);
    }

    @FXML
    public void initialize() {
        addProductRow();
    }

    @FXML
    private void addProductRow() {
        productCount++;

        HBox productRow = new HBox(10);
        productRow.setAlignment(Pos.CENTER);

        TextField productId = new TextField();
        productId.setPromptText("Product id");

        TextField productQuantity = new TextField();
        productQuantity.setPromptText("Product quantity");

        productRow.getChildren().addAll(
                new Label("Product " + productCount + ":"),
                productId, productQuantity
        );

        productsContainer.getChildren().add(productRow);
    }

    @FXML
    private void saveSale(ActionEvent event) {
        if ( !clientName.getText().isEmpty() &&
                !clientPhone.getText().isEmpty() &&
                !clientSurname.getText().isEmpty()  ){

            try {
                int phoneNumber;
                try{
                    phoneNumber = Integer.parseInt(clientPhone.getText());
                    if (clientPhone.getText().length() != 8)
                        throw new NumberFormatException();
                }
                catch (NumberFormatException e){
                    errorLabel.setVisible(true);
                    errorLabel.setText("Invalid number !");
                    clientPhone.clear();
                    return;
                }
                errorLabel.setVisible(false);

                ProductManager pm = Globals.managers.product;
                SaleManager sm = Globals.managers.sale;

                ObservableList<Node> productLabels = productsContainer.getChildren();

                for (Node n : productLabels){
                    HBox productRow = (HBox)n;

                    int productId,productQuantity;
                    TextField id , quantity;
                    try{
                        id = (TextField)productRow.getChildren().get(1);
                        productId = Integer.parseInt(id.getText());
                         errorLabel.setVisible(false);
                    }
                    catch (NumberFormatException e){
                        errorLabel.setText("Invalid id !");
                        errorLabel.setVisible(true);
                        return;
                    }

                    try {
                        quantity = (TextField)productRow.getChildren().get(2);
                        productQuantity = Integer.parseInt(quantity.getText());
                        errorLabel.setVisible(false);
                    }
                    catch (NumberFormatException ex) {
                        errorLabel.setText("Invalid quantity !");
                        errorLabel.setVisible(true);
                        return;
                    }

                    try{
                        pm.fetchProduct(productId);
                        int client_id = sm.addClient(clientName.getText(), clientSurname.getText(), phoneNumber);
                        int sale_id = sm.addSale(client_id);
                        sm.addSaleProduct(sale_id,productId,productQuantity);
                    }
                    catch (InsufficientStockException e ){
                        errorLabel.setVisible(true);
                        errorLabel.setText("Insufficient stock !");
                        quantity.clear();
                        return;
                    }
                    catch (ProductNotFoundException e){
                        errorLabel.setVisible(true);
                        id.clear();
                        errorLabel.setText("This product is not found !");
                        return;
                    }
                }
                goBack(event);


            }
            catch (NumberFormatException e){
                 errorLabel.setText("Invalid phone number !");
                 errorLabel.setVisible(true);
            }
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        Globals.controllers.main.show();
    }
}

