package com.mycompany.storeapp.view.component.shop.cart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class CartActionsComponent extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);

    private JButton discountButton;
    private JButton checkoutButton;

    public CartActionsComponent() {
        setupComponent();
    }

    private void setupComponent() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        discountButton = new JButton("💰 Giảm giá");
        discountButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountButton.setBackground(new Color(251, 191, 36));
        discountButton.setForeground(Color.WHITE);
        discountButton.setBorder(new EmptyBorder(10, 15, 10, 15));
        discountButton.setFocusPainted(false);
        discountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        checkoutButton = new JButton("💳 Thanh toán");
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutButton.setBackground(SUCCESS_COLOR);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setBorder(new EmptyBorder(12, 20, 12, 20));
        checkoutButton.setFocusPainted(false);
        checkoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        buttonPanel.add(discountButton);
        buttonPanel.add(checkoutButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    public void setDiscountListener(ActionListener listener) {
        discountButton.addActionListener(listener);
    }

    public void setCheckoutListener(ActionListener listener) {
        checkoutButton.addActionListener(listener);
    }
}