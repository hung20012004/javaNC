package com.mycompany.storeapp.view.component.shop.cart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class CartSummaryComponent extends JPanel {
    private static final Color HEADER_COLOR = new Color(249, 250, 251);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);

    private JLabel subtotalValue;
    private JLabel discountLabel;
    private JLabel totalLabel;
    private DecimalFormat currencyFormat;

    public CartSummaryComponent() {
        currencyFormat = new DecimalFormat("#,###,### ₫");
        setupComponent();
    }

    private void setupComponent() {
        setLayout(new BorderLayout());
        setBackground(HEADER_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel summaryContent = new JPanel(new GridLayout(4, 2, 5, 8));
        summaryContent.setOpaque(false);

        JLabel subtotalLabel = new JLabel("Tạm tính:");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtotalLabel.setForeground(TEXT_COLOR);

        subtotalValue = new JLabel("0 ₫");
        subtotalValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtotalValue.setForeground(TEXT_COLOR);
        subtotalValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel discountLabelText = new JLabel("Giảm giá:");
        discountLabelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountLabelText.setForeground(TEXT_COLOR);

        discountLabel = new JLabel("0 ₫");
        discountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountLabel.setForeground(DANGER_COLOR);
        discountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel taxLabel = new JLabel("Thuế (0%):");
        taxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taxLabel.setForeground(TEXT_COLOR);

        JLabel taxValue = new JLabel("0 ₫");
        taxValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taxValue.setForeground(TEXT_COLOR);
        taxValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel totalLabelText = new JLabel("Tổng cộng:");
        totalLabelText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabelText.setForeground(TEXT_COLOR);

        totalLabel = new JLabel("0 ₫");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        summaryContent.add(subtotalLabel);
        summaryContent.add(subtotalValue);
        summaryContent.add(discountLabelText);
        summaryContent.add(discountLabel);
        summaryContent.add(taxLabel);
        summaryContent.add(taxValue);
        summaryContent.add(totalLabelText);
        summaryContent.add(totalLabel);

        add(summaryContent, BorderLayout.CENTER);
    }

    public void updateSummary(double subtotal, double discount, double total) {
        subtotalValue.setText(currencyFormat.format(subtotal));
        discountLabel.setText("-" + currencyFormat.format(discount));
        totalLabel.setText(currencyFormat.format(total));
    }
}