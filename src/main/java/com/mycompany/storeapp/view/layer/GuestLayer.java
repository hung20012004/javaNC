package com.mycompany.storeapp.view.layer;

import com.mycompany.storeapp.view.page.guest.LoginPage;
import com.mycompany.storeapp.view.page.guest.RegisterPage;
import javax.swing.*;
import java.awt.*;

public class GuestLayer extends JFrame {
    private JPanel contentPanel;
    private LayerManager layerManager;

    public GuestLayer(LayerManager layerManager) {
        this.layerManager = layerManager;
        initializeFrame();
        initializeComponents();
    }

    private void initializeFrame() {
        setTitle("Hệ thống quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(248, 250, 252));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(248, 250, 252));
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        showLoginPage();
    }

    public void showLoginPage() {
        SwingUtilities.invokeLater(() -> {
            contentPanel.removeAll();
            LoginPage loginPage = new LoginPage(this, layerManager);
            contentPanel.add(loginPage, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            loginPage.requestFocusInWindow();
        });
    }

    public void showRegisterPage() {
        SwingUtilities.invokeLater(() -> {
            contentPanel.removeAll();
            RegisterPage registerPage = new RegisterPage(this, layerManager);
            contentPanel.add(registerPage, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
            registerPage.requestFocusInWindow();
        });
    }
}