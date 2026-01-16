module com.gestionpharmacie {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens com.gestionpharmacie to javafx.fxml;
    exports com.gestionpharmacie;
}