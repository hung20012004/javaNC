package com.mycompany.storeapp.view.page.guest;

import com.mycompany.storeapp.controller.guest.RegisterController;
import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.session.Session;
import com.mycompany.storeapp.view.layer.GuestLayer;
import com.mycompany.storeapp.view.layer.LayerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RegisterPage extends JPanel {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private final GuestLayer guestLayer;
    private final LayerManager layerManager;

    public RegisterPage(GuestLayer guestLayer, LayerManager layerManager) {
        this.guestLayer = guestLayer;
        this.layerManager = layerManager;
        setLayout(new GridBagLayout());
        setBackground(new Color(248, 250, 252));
        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("ĐĂNG KÝ TÀI KHOẢN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(59, 130, 246));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 15, 30, 15);
        add(titleLabel, gbc);

        // Reset insets cho các components khác
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridwidth = 1;

        // Name label và field
        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(nameField, gbc);

        // Email label và field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(emailField, gbc);

        // Password label và field
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(passwordField, gbc);

        // Confirm password label và field
        JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        add(confirmPasswordField, gbc);

        // Register button
        JButton registerButton = new JButton("Đăng ký");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setBackground(new Color(59, 130, 246));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegister());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 15, 10, 15);
        add(registerButton, gbc);

        // Back to login button
        JButton backToLoginButton = new JButton("Quay lại đăng nhập");
        backToLoginButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backToLoginButton.setBackground(new Color(229, 231, 235));
        backToLoginButton.setFocusPainted(false);
        backToLoginButton.setPreferredSize(new Dimension(150, 30));
        backToLoginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backToLoginButton.addActionListener(e -> guestLayer.showLoginPage());

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 15, 0, 15);
        add(backToLoginButton, gbc);

        // Thêm KeyListener để có thể Enter để submit
        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleRegister();
                }
            }
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        };

        nameField.addKeyListener(enterKeyListener);
        emailField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (name.length() < 2) {
            showErrorMessage("Tên phải có ít nhất 2 ký tự!");
            nameField.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            showErrorMessage("Email không hợp lệ!");
            emailField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showErrorMessage("Mật khẩu phải có ít nhất 6 ký tự!");
            passwordField.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showErrorMessage("Mật khẩu xác nhận không khớp!");
            confirmPasswordField.requestFocus();
            return;
        }

        try {
            RegisterController registerController = new RegisterController();
            String message = registerController.register(name, email, password, null);

            if (message.equals("Đăng ký thành công!")) {
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                User currentUser = Session.getInstance().getCurrentUser();
                if (currentUser != null) {
                    layerManager.login(currentUser); 
                }
            } else {
                showErrorMessage(message);
            }
        } catch (Exception e) {
            showErrorMessage("Có lỗi xảy ra khi đăng ký: " + e.getMessage());
        }

        // Clear password fields for security
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        if (nameField != null) {
            SwingUtilities.invokeLater(() -> nameField.requestFocus());
        }
    }
}