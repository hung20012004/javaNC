/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.config;

/**
 *
 * @author Manh Hung
 */
import com.mycompany.storeapp.view.layer.GuestLayer;
import com.mycompany.storeapp.view.page.guest.LoginPage;
import javax.swing.JPanel;

public class NavigationManager {
    private static GuestLayer mainFrame;

    public static void init(GuestLayer frame) {
        mainFrame = frame;
        navigateTo("login");
    }

    public static void navigateTo(String page) {
        JPanel content;
        switch (page) {
            case "login":
                content = new LoginPage();
                break;
            case "home":
                // Tạo một trang HomePage nếu cần (hiện tại để trống)
                content = new JPanel();
                content.setLayout(null);
                javax.swing.JLabel label = new javax.swing.JLabel("Trang chủ");
                label.setBounds(150, 100, 100, 30);
                content.add(label);
                break;
            default:
                content = new JPanel();
        }
        mainFrame.setContent(content);
    }
}
