package com.mycompany.storeapp.view.component.shop.cart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class CartHeaderComponent extends JPanel {
    private static final Color HEADER_COLOR = new Color(249, 250, 251);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);
    private static final Color SECONDARY_TEXT_COLOR = new Color(107, 114, 128);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);

    private JLabel headerLabel;
    private JLabel itemCountLabel;
    private JButton clearCartButton;

    public CartHeaderComponent() {
        setupComponent();
    }

    private void setupComponent() {
        setLayout(new BorderLayout());
        setBackground(HEADER_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        headerLabel = new JLabel("🛒 Giỏ hàng");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(TEXT_COLOR);

        itemCountLabel = new JLabel("0 sản phẩm");
        itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemCountLabel.setForeground(SECONDARY_TEXT_COLOR);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(headerLabel, BorderLayout.NORTH);
        leftPanel.add(itemCountLabel, BorderLayout.SOUTH);

        clearCartButton = new JButton("🗑️");
        clearCartButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        clearCartButton.setBackground(DANGER_COLOR);
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.setBorder(new EmptyBorder(8, 12, 8, 12));
        clearCartButton.setFocusPainted(false);
        clearCartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearCartButton.setToolTipText("Xóa tất cả sản phẩm");

        add(leftPanel, BorderLayout.WEST);
        add(clearCartButton, BorderLayout.EAST);
    }

    public void updateItemCount(int count) {
        itemCountLabel.setText(count + " sản phẩm");
    }

    public void setClearCartListener(ActionListener listener) {
        clearCartButton.addActionListener(listener);
    }
}