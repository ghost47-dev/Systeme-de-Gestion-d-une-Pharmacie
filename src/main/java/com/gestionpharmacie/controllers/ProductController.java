package com.gestionpharmacie.controllers;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gestionpharmacie.exceptions.ProductNotFoundException;
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
    @FXML TextField nameField , priceField , quantityField;

    private String privilege;
    private int id;
    private String product ;
    public void setPrivilege(String privilege){this.privilege = privilege;}
    @FXML
    private void saveProduct() {
        String name = productName.getText();
        String price = productPrice.getText();
        String quantity = productQuantity.getText();
        int Quantity;
        double Price;
        if (!name.isEmpty() &&
            !price.isEmpty() &&
            !quantity.isEmpty()){

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
    @FXML
    public void showEdit(String product){

        this.product = product;
        Pattern pattern = Pattern.compile(
                "Product id\\s*:\\s*(\\d+)\\s*\\n" +
                "Product name\\s*:\\s*(.*?)\\s*\\|\\s*" +
                "Product price\\s*:\\s*([\\d.]+)\\s*\\|\\s*" +
                "Product quantity\\s*(\\d+)"
        );

        Matcher matcher = pattern.matcher(product);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid product format");
        }

        id = Integer.parseInt(matcher.group(1));
        String name = matcher.group(2);
        double price = Double.parseDouble(matcher.group(3));
        int quantity = Integer.parseInt(matcher.group(4));


        nameField.setText(name);
        priceField.setText(Double.toString(price));
        quantityField.setText(Integer.toString(quantity));

    }
    @FXML
    private void editProduct(ActionEvent event ){
        if (product == null) return;


        String name = nameField.getText();
        String price = priceField.getText();
        String quantity = quantityField.getText();
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

            try {
                productManager.updateProduct(id ,name ,Price ,Quantity);
                goBack(event);
            }
            catch (ProductNotFoundException e){
                return ;
            }

            quantityField.clear();
            priceField.clear();
            nameField.clear();

        }
    }


}

