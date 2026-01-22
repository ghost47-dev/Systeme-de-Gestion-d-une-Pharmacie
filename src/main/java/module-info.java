module com.gestionpharmacie {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.sql;


    opens com.gestionpharmacie to javafx.fxml;
    exports com.gestionpharmacie;
    opens com.gestionpharmacie.controllers to javafx.fxml;
    exports com.gestionpharmacie.controllers;
}
