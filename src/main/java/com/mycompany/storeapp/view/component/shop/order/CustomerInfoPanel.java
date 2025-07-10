package com.mycompany.storeapp.view.component.shop.order;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CustomerInfoPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);

    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextField customerEmailField;
    private JComboBox<String> customerTypeCombo;

    public CustomerInfoPanel() {
        initializePanel();
        setupComponents();
        setupLayout();
    }

    private void initializePanel() {
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            "👤 Thông tin khách hàng",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
    }

    private void setupComponents() {
        // Loại khách hàng
        customerTypeCombo = new JComboBox<>(new String[]{"Khách lẻ", "Khách VIP", "Khách doanh nghiệp"});
        customerTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Tên khách hàng
        customerNameField = new JTextField("Khách lẻ");
        customerNameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));

        // Số điện thoại
        customerPhoneField = new JTextField();
        customerPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerPhoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));

        // Email
        customerEmailField = new JTextField();
        customerEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerEmailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Loại khách hàng
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Loại khách hàng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(customerTypeCombo, gbc);

        // Tên khách hàng
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(customerNameField, gbc);

        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(customerPhoneField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(customerEmailField, gbc);
    }

    // Getters
    public String getCustomerName() { return customerNameField.getText().trim(); }
    public String getCustomerPhone() { return customerPhoneField.getText().trim(); }
    public String getCustomerEmail() { return customerEmailField.getText().trim(); }
    public String getCustomerType() { return (String) customerTypeCombo.getSelectedItem(); }

    // Setters
    public void setCustomerName(String name) { customerNameField.setText(name); }
    public void setCustomerPhone(String phone) { customerPhoneField.setText(phone); }
    public void setCustomerEmail(String email) { customerEmailField.setText(email); }
    public void setCustomerType(String type) { customerTypeCombo.setSelectedItem(type); }

    // Validation
    public boolean validateFields() {
        if (customerNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            customerNameField.requestFocus();
            return false;
        }
        return true;
    }

    public JTextField getCustomerNameField() { return customerNameField; }
}