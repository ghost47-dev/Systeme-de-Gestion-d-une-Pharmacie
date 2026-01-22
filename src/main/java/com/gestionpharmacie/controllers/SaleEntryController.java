package com.gestionpharmacie.controllers;



import java.sql.Connection;

import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.SaleManager;
import com.gestionpharmacie.utilities.DatabaseConnection;

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

public class SaleEntryController {

    @FXML
    private Label errorLabel;

    @FXML
    private TextField clientName, clientSurname, clientPhone;
    
    @FXML
    private VBox productsContainer;

    private int productCount = 0;

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
    private void saveSale() {
        if ( !clientName.getText().isEmpty() &&
                !clientPhone.getText().isEmpty() &&
                !clientSurname.getText().isEmpty()  ){
            
            try {
                
                int phoneNumber = Integer.parseInt(clientPhone.getText());
                errorLabel.setVisible(false);
                
                Connection connection = DatabaseConnection.getConnection();
                ProductManager pm = new ProductManager(connection);
                SaleManager sm = new SaleManager(pm, connection);
                
                int client_id = sm.addClient(clientName.getText(), clientSurname.getText(), phoneNumber);
                
                int sale_id = sm.addSale(client_id);
                ObservableList<Node> productLabels = productsContainer.getChildren();
                
                for (Node n : productLabels){
                    HBox productRow = (HBox)n;
                    
                    int productId,productQuantity;
                    try{
                        TextField id = (TextField)productRow.getChildren().get(1);
                        productId = Integer.parseInt(id.getText());
                         errorLabel.setVisible(false);
                    }
                    catch (NumberFormatException e){
                        errorLabel.setText("Invalid id !");
                        errorLabel.setVisible(true);
                        return;
                    }

                    try {
                        TextField quantity = (TextField)productRow.getChildren().get(2);
                        productQuantity = Integer.parseInt(quantity.getText());                   
                        errorLabel.setVisible(false);
                    }
                    catch (NumberFormatException ex) {
                        errorLabel.setText("Invalid quantity !");
                        errorLabel.setVisible(true);
                        return;
                    }
                    
                    int sp_id = sm.addSaleProduct(sale_id,productId,productQuantity); 
                }
                        
                 
            }
            catch (NumberFormatException e){
                 errorLabel.setText("Invalid phone number !");
                 errorLabel.setVisible(true);
            }
        }
            
            


            
    }

    @FXML
    private void goBack(ActionEvent event) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/com/gestionpharmacie/main.fxml"));
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

