/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private String url;
    private String username;
    private String password;

    public DatabaseConnection() {
        loadDatabaseProperties();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

     private void loadDatabaseProperties() {
        url = "jdbc:mysql://localhost:3306/clothing_store";
        username = "root";
        password = "20012004";
    }
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Phương thức test kết nối
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Kết nối thành công đến MySQL tại " + new java.util.Date());
            return true;
        } catch (SQLException e) {
            System.out.println("Kết nối thất bại: " + e.getMessage());
            return false;
        }
    }
}
