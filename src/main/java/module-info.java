module com.example.gestionpharmacie {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gestionpharmacie to javafx.fxml;
    exports com.example.gestionpharmacie;
}