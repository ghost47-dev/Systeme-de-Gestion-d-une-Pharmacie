package com.gestionpharmacie.controllers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class Database { // Singleton

    private static Database instance;
    private Connection connection;


    private Database() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/pharmacy",
                    "user",
                    "Strongpassword123"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2️⃣ global access point
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}


