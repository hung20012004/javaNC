/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.storeapp;
/**
 *
 * @author Manh Hung
 */
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.config.NavigationManager;
import com.mycompany.storeapp.view.layer.AdminLayer;
import com.mycompany.storeapp.view.layer.GuestLayer;

public class StoreApp {
    public static void main(String[] args) {
        DatabaseConnection dbConn = DatabaseConnection.getInstance();
        if (dbConn.testConnection()) {
            System.out.println("Sẵn sàng sử dụng ứng dụng!");
        } else {
            System.out.println("Không thể khởi động ứng dụng do lỗi kết nối.");
            return;
        }

        AdminLayer frame = new AdminLayer();
//        GuestLayer frame = new GuestLayer();
//        NavigationManager.init(frame);
        frame.setVisible(true);

        dbConn.closeConnection();
    }
}
