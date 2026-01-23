module com.gestionpharmacie {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires transitive java.sql;

    opens com.gestionpharmacie to javafx.fxml;
    exports com.gestionpharmacie;
    opens com.gestionpharmacie.controllers to javafx.fxml;
    exports com.gestionpharmacie.controllers;
    exports com.gestionpharmacie.managers;
    exports com.gestionpharmacie.model;
}

