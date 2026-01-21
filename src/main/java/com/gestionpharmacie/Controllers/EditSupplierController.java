package com.gestionpharmacie.Controllers;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.utilities.DatabaseConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditSupplierController{
    
    @FXML private TextField nameField , phoneField;
    @FXML private javafx.scene.control.Label errorLabel;
    @FXML private int supplier_id;
    @FXML
    public void showEdit(String supplier){
        Pattern pattern = Pattern.compile("Supplier id : (\\d+)\\nSupplier name : (\\S+) \\| Supplier phone : (\\d+)");
        Matcher matcher = pattern.matcher(supplier);

        if (!matcher.find()){
            throw new IllegalArgumentException() ;
        }
                
        supplier_id = Integer.parseInt(matcher.group(1));
        String name = matcher.group(2);
        String phone = matcher.group(3);
        System.out.println(phone);
        nameField.setText(name);
        phoneField.setText(phone);

    }
    @FXML
    private void goBack(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/gestionpharmacie/main.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();

            mainController.showSuppliersPage();
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
    private void editSupplier(){
        String name = nameField.getText();
        String phone = phoneField.getText();
        int Phone;
       
        try {
            Phone = Integer.parseInt(phone) ;
        }
        catch (NumberFormatException e){
            errorLabel.setVisible(true);
            errorLabel.setText("Invalid phone number !");
            return;
        }
        errorLabel.setVisible(false);

        try(Connection connection = DatabaseConnection.getConnection()){
            ShipmentManager sm = new ShipmentManager(connection);
            sm.updateSupplier(supplier_id,name,Phone);
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }

}
