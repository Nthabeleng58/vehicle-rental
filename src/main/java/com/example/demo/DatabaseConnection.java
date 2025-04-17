package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/vehicle_rental",
                    "root",
                    "56568537"
            );
            System.out.println("✅ Database connection successful!");
            return connection;
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to the database.");
            e.printStackTrace();
            return null;
        }
    }
}
