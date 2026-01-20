package com.gestionpharmacie.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    private static final String URL = "jdbc:mysql://localhost:3306/pharmacy";
    private static final String USER = "pharma";
    private static final String PASSWORD = "StrongPassword123";

    public static Connection getConnection() {
            try {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Connected to DB successfully");
                }
            } catch (SQLException e) {
                System.out.println(DriverManager.getDrivers().hasMoreElements());
                System.err.println("Error: " + e.getMessage());
            }
        return connection;
    }

}
