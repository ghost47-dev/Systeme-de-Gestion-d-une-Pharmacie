package com.gestionpharmacie.exceptions;
import java.sql.SQLException;

public class ProductNotFoundException extends SQLException {
    public ProductNotFoundException(String message) {
        super(message);
    }
    public ProductNotFoundException() {
        super();
    }
}
