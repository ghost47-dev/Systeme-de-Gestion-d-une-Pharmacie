package com.gestionpharmacie.Controllers;
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

import com.gestionpharmacie.managers.UserManager;
import com.gestionpharmacie.model.User;
import com.gestionpharmacie.utilities.DatabaseConnection;
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML
    private void handleLogin(ActionEvent event) {
        // Simple example check
        if (!usernameField.getText().isEmpty()
                && !passwordField.getText().isEmpty()) {

            try {
                // Login Logic
                
                String username = new String(usernameField.getText());
                String password = new String(passwordField.getText());
                
                UserManager usermanager = new UserManager(DatabaseConnection.getConnection());
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
                 

                Parent root = FXMLLoader.load(
                        getClass().getResource("/com/gestionpharmacie/main.fxml")
                );

                Scene scene = new Scene(root, 900, 600);
                scene.getStylesheets().add(
                        getClass().getResource("/com/gestionpharmacie/styles.css").toExternalForm()
                );

                Stage stage = (Stage) ((Node) event.getSource())
                        .getScene().getWindow();

                stage.setTitle("Navigation Panel Application");
                stage.setScene(scene);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

