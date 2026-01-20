package com.gestionpharmacie.Controllers;


import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.utilities.DatabaseConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProductController {

    @FXML
    private TextField productName, productPrice, productQuantity;
    @FXML
    private Label errorLabel;


    @FXML
    private void saveProduct() {
        String name = productName.getText();
        String price = productPrice.getText();
        String quantity = productQuantity.getText();
        int Quantity;
        double Price;
        if (!name.isEmpty() && 
            !price.isEmpty() &&
            !quantity.isEmpty() ){
            
            try {
                Price = Double.parseDouble(price);
            }
            catch (NumberFormatException e){
               errorLabel.setText("Invalid price !");
               errorLabel.setVisible(true); 
               return;
            }

            try{
                Quantity = Integer.parseInt(quantity);
            }
            catch(NumberFormatException e){
                errorLabel.setText("Invalid quantity !");   
                errorLabel.setVisible(true);
                return;
            }
            
            ProductManager productManager = new ProductManager(DatabaseConnection.getConnection());
            productManager.addProduct(name,Price,Quantity);

            productQuantity.clear();
            productPrice.clear();
            productName.clear();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        // load your other fxml (e.g., main page)
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionpharmacie/main.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();

            mainController.showProductsPage();
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

