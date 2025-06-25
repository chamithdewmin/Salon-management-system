package com.salon.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/salon";
    private static final String USER = "root";
    private static final String PASSWORD = "admin123";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            return null;
        }
    }
}
