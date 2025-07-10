package com.mycompany.storeapp.view.component.shop.order;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class OrderSummaryPanel extends JPanel {
    private static final Color HEADER_COLOR = new Color(249, 250, 251);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel shippingLabel;
    private JLabel totalLabel;
    private DecimalFormat currencyFormat;
    
    public OrderSummaryPanel() {
        this.currencyFormat = new DecimalFormat("#,###,### ₫");
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new GridLayout(4, 2, 5, 5));
        setBackground(HEADER_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        add(new JLabel("Tạm tính:"));
        subtotalLabel = new JLabel("0 ₫");
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(subtotalLabel);
        
        add(new JLabel("Giảm giá:"));
        discountLabel = new JLabel("0 ₫");
        discountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        discountLabel.setForeground(DANGER_COLOR);
        add(discountLabel);
        
        add(new JLabel("Phí giao hàng:"));
        shippingLabel = new JLabel("0 ₫");
        shippingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(shippingLabel);
        
        JLabel totalLabelText = new JLabel("Tổng cộng:");
        totalLabelText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(totalLabelText);
        totalLabel = new JLabel("0 ₫");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(PRIMARY_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(totalLabel);
    }
    
    public void updateSummary(double subtotal, double discount, double shipping, double total) {
        subtotalLabel.setText(currencyFormat.format(subtotal));
        discountLabel.setText("-" + currencyFormat.format(discount));
        shippingLabel.setText(currencyFormat.format(shipping));
        totalLabel.setText(currencyFormat.format(total));
    }
    
    public void setSubtotal(double subtotal) {
        subtotalLabel.setText(currencyFormat.format(subtotal));
    }
    
    public void setDiscount(double discount) {
        discountLabel.setText("-" + currencyFormat.format(discount));
    }
    
    public void setShipping(double shipping) {
        shippingLabel.setText(currencyFormat.format(shipping));
    }
    
    public void setTotal(double total) {
        totalLabel.setText(currencyFormat.format(total));
    }
}