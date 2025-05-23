/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.guest;

/**
 *
 * @author Manh Hung
 */
import com.mycompany.storeapp.controller.guest.LoginController;
import com.mycompany.storeapp.view.component.CustomButton;
import com.mycompany.storeapp.config.NavigationManager;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends javax.swing.JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage() {
        setLayout(null); 

        JLabel usernameLabel = new JLabel("Email:");
        usernameLabel.setBounds(50, 50, 100, 30);
        add(usernameLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 50, 200, 30);
        add(emailField);

        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setBounds(50, 100, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 30);
        add(passwordField);

        CustomButton loginButton = new CustomButton("Đăng nhập");
        loginButton.setBounds(150, 150, 100, 40);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = emailField.getText();
                String password = new String(passwordField.getPassword());
                LoginController loginController = new LoginController();
                String message = loginController.login(username, password);
                JOptionPane.showMessageDialog(null, message);
                if (message.equals("Đăng nhập thành công!")) {
                    NavigationManager.navigateTo("home");
                }
            }
        });
        add(loginButton);
    }

    public String getUsername() {
        return emailField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}