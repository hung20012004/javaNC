package com.mycompany.storeapp;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.view.layer.LayerManager;
import javax.swing.SwingUtilities;

public class StoreApp {
    public static void main(String[] args) {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (!dbConn.testConnection()) {
            System.out.println("Không thể khởi động ứng dụng do lỗi kết nối.");
            return;
        }
        SwingUtilities.invokeLater(() -> new LayerManager());
    }
}