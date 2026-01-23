package com.gestionpharmacie.controllers;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gestionpharmacie.Globals;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.model.Supplier;

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
    private String privilege;
    public void setPrivilege(String privilege){this.privilege = privilege;}

    private Scene scene;

    public EditSupplierController(){
        scene = Globals.scenes.loadScene("supplierEdit.fxml", this);
    }

    public void show(){
        Globals.stage.setScene(scene);
    }

    @FXML
    public void showEdit(String supplierInfo){
        Pattern pattern = Pattern.compile("(\\d+): (\\S+).*");
        Matcher matcher = pattern.matcher(supplierInfo);

        if (!matcher.find()){
            throw new IllegalArgumentException() ;
        }

        supplier_id = Integer.parseInt(matcher.group(1));
        String name = matcher.group(2);
        ShipmentManager sm = new ShipmentManager(Globals.database.getConnection());
        Supplier supplier = sm.fetchSupplier(supplier_id);

        nameField.setText(name);
        phoneField.setText(String.valueOf(supplier.getNumerotel()));

    }
    @FXML
    private void goBack(ActionEvent event) {
        Globals.controllers.main.show();
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

        try(Connection connection = Globals.database.getConnection()){
            ShipmentManager sm = new ShipmentManager(connection);
            sm.updateSupplier(supplier_id,name,Phone);
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }

}
