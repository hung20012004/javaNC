/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.admin;

/**
 * Header Component - Thanh header của admin - Updated Version
 * @author Hi
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class HeaderComponent extends JPanel {
    private JButton logoutButton;
    private JLabel titleLabel;
    private JLabel userLabel;
    
    // Constructor mặc định
    public HeaderComponent() {
        this("🏪 Hệ thống quản lý cửa hàng", "Admin");
    }
    
    // Constructor với tham số
    public HeaderComponent(String title, String username) {
        initializeComponent(title, username);
    }
    
    private void initializeComponent(String title, String username) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        setPreferredSize(new Dimension(0, 70));
        
        // Logo và tiêu đề bên trái
        JPanel titlePanel = createTitlePanel(title);
        add(titlePanel, BorderLayout.WEST);
        
        // User info bên phải
        JPanel userPanel = createUserPanel(username);
        add(userPanel, BorderLayout.EAST);
    }
    
    private JPanel createTitlePanel(String title) {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(15, 20, 15, 0));
        
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(17, 24, 39));
        titlePanel.add(titleLabel);
        
        return titlePanel;
    }
    
    private JPanel createUserPanel(String username) {
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(Color.WHITE);
        userPanel.setBorder(new EmptyBorder(15, 0, 15, 20));
        
        // User info
        userLabel = new JLabel("👤 " + username);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(75, 85, 99));
        
        // Logout button
        logoutButton = createLogoutButton();
        
        userPanel.add(userLabel);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(logoutButton);
        
        return userPanel;
    }
    
    private JButton createLogoutButton() {
        JButton btn = new JButton("🚪 Đăng xuất");
        Color logoutColor = new Color(239, 68, 68);
        
        btn.setBackground(logoutColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        
        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(logoutColor.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(logoutColor);
            }
        });
        
        return btn;
    }
    
    // Phương thức để thêm action listener cho logout button
    public void addLogoutActionListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
    
    // Phương thức để set logout action (functional interface style)
    public void setLogoutAction(Runnable action) {
        logoutButton.addActionListener(e -> action.run());
    }
    
    // Phương thức để cập nhật thông tin user
    public void setUserInfo(String username) {
        userLabel.setText("👤 " + username);
    }
    
    // Phương thức để cập nhật title
    public void updateTitle(String newTitle) {
        titleLabel.setText(newTitle);
    }
    
    // Phương thức để lấy title hiện tại
    public String getCurrentTitle() {
        return titleLabel.getText();
    }
    
    // Phương thức để lấy username hiện tại
    public String getCurrentUsername() {
        String text = userLabel.getText();
        return text.replace("👤 ", "");
    }
}