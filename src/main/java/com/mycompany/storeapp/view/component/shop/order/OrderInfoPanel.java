package com.mycompany.storeapp.view.component.shop.order;

import com.mycompany.storeapp.model.entity.CartItem;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class OrderInfoPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color HEADER_COLOR = new Color(249, 250, 251);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);

    private JLabel orderDateLabel;
    private JComboBox<String> paymentMethodCombo;
    private JComboBox<String> deliveryMethodCombo;
    private JTextField shippingFeeField;
    private JTable orderItemsTable;
    private JLabel subtotalLabel;
    private JLabel discountLabel;
    private JLabel shippingLabel;
    private JLabel totalLabel;

    private List<CartItem> cartItems;
    private double discountPercent;
    private DecimalFormat currencyFormat;
    private ProductVariantController variantController;

    public OrderInfoPanel(List<CartItem> cartItems, double discountPercent) {
        this.cartItems = cartItems;
        this.discountPercent = discountPercent;
        this.currencyFormat = new DecimalFormat("#,###,### ₫");
        this.variantController = new ProductVariantController();
        
        initializePanel();
        setupComponents();
        setupLayout();
        calculateTotals();
    }

    private void initializePanel() {
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            "📋 Thông tin đơn hàng",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
    }

    private void setupComponents() {
        // Ngày đặt
        orderDateLabel = new JLabel(java.time.LocalDate.now().toString());
        orderDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Phương thức thanh toán
        paymentMethodCombo = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản", "Ví điện tử"});
        paymentMethodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Phương thức giao hàng
        deliveryMethodCombo = new JComboBox<>(new String[]{"Tại cửa hàng", "Giao hàng tiêu chuẩn", "Giao hàng nhanh", "Giao hàng hỏa tốc"});
        deliveryMethodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deliveryMethodCombo.addActionListener(e -> {
            updateShippingFee();
            calculateTotals();
        });

        // Phí giao hàng
        shippingFeeField = new JTextField("0");
        shippingFeeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shippingFeeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
        shippingFeeField.addActionListener(e -> calculateTotals());

        // Tạo bảng sản phẩm
        createOrderItemsTable();

        // Tạo các label tổng tiền
        createSummaryLabels();
    }

    private void createOrderItemsTable() {
        String[] columns = {"Sản phẩm", "SL", "Đơn giá", "Thành tiền"};
        String[][] data = new String[cartItems.size()][4];
        
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            ProductVariant variant = variantController.getVariantById(item.getVariantId());
            if (variant != null) {
                data[i][0] = variant.getName();
                data[i][1] = String.valueOf(item.getQuantity());
                data[i][2] = currencyFormat.format(variant.getPrice());
                data[i][3] = currencyFormat.format(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        
        orderItemsTable = new JTable(data, columns);
        orderItemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orderItemsTable.setRowHeight(30);
        orderItemsTable.setEnabled(false);
    }

    private void createSummaryLabels() {
        subtotalLabel = new JLabel("0 ₫");
        subtotalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        discountLabel = new JLabel("0 ₫");
        discountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        discountLabel.setForeground(DANGER_COLOR);
        
        shippingLabel = new JLabel("0 ₫");
        shippingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        totalLabel = new JLabel("0 ₫");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(PRIMARY_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Top section - Order details
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Middle section - Order items table
        JPanel middlePanel = createMiddlePanel();
        add(middlePanel, BorderLayout.CENTER);
        
        // Bottom section - Summary
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Ngày đặt
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Ngày đặt:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(orderDateLabel, gbc);
        
        // Phương thức thanh toán
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Thanh toán:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(paymentMethodCombo, gbc);
        
        // Phương thức giao hàng
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Giao hàng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(deliveryMethodCombo, gbc);
        
        // Phí giao hàng
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Phí giao hàng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(shippingFeeField, gbc);
        
        return panel;
    }

    private JPanel createMiddlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 10, 10, 10));
        
        JScrollPane tableScroll = new JScrollPane(orderItemsTable);
        tableScroll.setPreferredSize(new Dimension(0, 150));
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        panel.add(new JLabel("Sản phẩm đã chọn:"), BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        panel.add(new JLabel("Tạm tính:"));
        panel.add(subtotalLabel);
        
        panel.add(new JLabel("Giảm giá:"));
        panel.add(discountLabel);
        
        panel.add(new JLabel("Phí giao hàng:"));
        panel.add(shippingLabel);
        
        JLabel totalLabelText = new JLabel("Tổng cộng:");
        totalLabelText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(totalLabelText);
        panel.add(totalLabel);
        
        return panel;
    }

    private void updateShippingFee() {
        double shippingFee = getShippingFeeByMethod();
        shippingFeeField.setText(String.valueOf((int)shippingFee));
    }

    public void calculateTotals() {
        double subtotal = cartItems.stream()
            .mapToDouble(item -> {
                ProductVariant variant = variantController.getVariantById(item.getVariantId());
                if (variant != null) {
                    return variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue();
                }
                return 0.0;
            }).sum();
        
        double discount = subtotal * (discountPercent / 100.0);
        
        double shippingFee;
        try {
            shippingFee = Double.parseDouble(shippingFeeField.getText().replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            shippingFee = getShippingFeeByMethod();
            shippingFeeField.setText(String.valueOf((int)shippingFee));
        }
        
        double total = subtotal - discount + shippingFee;
        
        subtotalLabel.setText(currencyFormat.format(subtotal));
        discountLabel.setText("-" + currencyFormat.format(discount));
        shippingLabel.setText(currencyFormat.format(shippingFee));
        totalLabel.setText(currencyFormat.format(total));
    }

    private double getShippingFeeByMethod() {
        String method = (String) deliveryMethodCombo.getSelectedItem();
        switch (method) {
            case "Tại cửa hàng": return 0;
            case "Giao hàng tiêu chuẩn": return 25000;
            case "Giao hàng nhanh": return 40000;
            case "Giao hàng hỏa tốc": return 60000;
            default: return 0;
        }
    }

    // Getters
    public String getPaymentMethod() { return (String) paymentMethodCombo.getSelectedItem(); }
    public String getDeliveryMethod() { return (String) deliveryMethodCombo.getSelectedItem(); }
    public double getShippingFee() {
        try {
            return Double.parseDouble(shippingFeeField.getText().replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            return getShippingFeeByMethod();
        }
    }
    public double getSubtotal() {
        return cartItems.stream()
            .mapToDouble(item -> {
                ProductVariant variant = variantController.getVariantById(item.getVariantId());
                if (variant != null) {
                    return variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue();
                }
                return 0.0;
            }).sum();
    }
    public double getDiscount() {
        return getSubtotal() * (discountPercent / 100.0);
    }
    public double getTotal() {
        return getSubtotal() - getDiscount() + getShippingFee();
    }

    // Setters
    public void setPaymentMethod(String method) { paymentMethodCombo.setSelectedItem(method); }
    public void setDeliveryMethod(String method) { 
        deliveryMethodCombo.setSelectedItem(method);
        updateShippingFee();
        calculateTotals();
    }
    public void setShippingFee(double fee) { 
        shippingFeeField.setText(String.valueOf((int)fee));
        calculateTotals();
    }
    
    // Add listener for delivery method changes
    public void addDeliveryMethodListener(ActionListener listener) {
        deliveryMethodCombo.addActionListener(listener);
    }
}