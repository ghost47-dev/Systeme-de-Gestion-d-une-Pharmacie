package com.gestionpharmacie.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.Node;
import javafx.event.ActionEvent;


import com.gestionpharmacie.Globals;
import com.gestionpharmacie.managers.UserManager;
import com.gestionpharmacie.model.User;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    private Scene scene;

    public LoginController(){
        scene = Globals.scenes.loadScene("login.fxml", this);
    }

    public void show(){
        Globals.stage.setTitle("Login");
        Globals.stage.setScene(scene);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Simple example check
        if (!usernameField.getText().isEmpty()
                && !passwordField.getText().isEmpty()) {

            try {
                // Login Logic

                String username = new String(usernameField.getText());
                String password = new String(passwordField.getText());

                UserManager usermanager = new UserManager(Globals.database.getConnection());
                User user = usermanager.fetchUser(username);

                if (user == null){
                    errorLabel.setText("this username does not exist !");
                    errorLabel.setVisible(true);

                    usernameField.clear();
                    passwordField.clear();
                    return ;
                }
                else if (!user.getPassword().equals(password)){
                    errorLabel.setText("Password is wrong !");
                    errorLabel.setVisible(true);

                    passwordField.clear();
                    return ;
                }
                errorLabel.setVisible(false);

                Globals.privilege = user.getPrivilege();
                Globals.controllers.main.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

