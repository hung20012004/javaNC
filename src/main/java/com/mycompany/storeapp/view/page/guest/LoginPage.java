package com.mycompany.storeapp.view.page.guest;

import com.mycompany.storeapp.controller.guest.LoginController;
import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.session.Session;
import com.mycompany.storeapp.view.layer.GuestLayer;
import com.mycompany.storeapp.view.layer.LayerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private final GuestLayer guestLayer;
    private final LayerManager layerManager;

    public LoginPage(GuestLayer guestLayer, LayerManager layerManager) {
        this.guestLayer = guestLayer;
        this.layerManager = layerManager;
        setLayout(new GridBagLayout());
        setBackground(new Color(248, 250, 252));
        setPreferredSize(new Dimension(400, 350));
        initializeComponents();
        setFocusToEmailField();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(59, 130, 246));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        JButton registerButton = new JButton("Đăng ký");
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerButton.setBackground(new Color(229, 231, 235));
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guestLayer.showRegisterPage();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(registerButton, gbc);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        LoginController loginController = new LoginController();
        String message = loginController.login(email, password);
        JOptionPane.showMessageDialog(this, message);

        if (message.equals("Đăng nhập thành công!")) {
            User currentUser = Session.getInstance().getCurrentUser();
            if (currentUser != null) {
                layerManager.login(currentUser);
            }
        }
    }

    private void setFocusToEmailField() {
        SwingUtilities.invokeLater(() -> {
            if (emailField != null) {
                emailField.requestFocusInWindow();
            }
        });
    }
}