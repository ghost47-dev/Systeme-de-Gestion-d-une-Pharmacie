module com.gestionpharmacie {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.gestionpharmacie to javafx.fxml;
    exports com.gestionpharmacie;
}