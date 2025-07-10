package com.mycompany.storeapp.view.component.shop.order;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class AddressPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);

    private JTextField addressField;
    private JTextField wardField;
    private JTextField districtField;
    private JTextField cityField;
    private JTextArea noteField;

    public AddressPanel() {
        initializePanel();
        setupComponents();
        setupLayout();
    }

    private void initializePanel() {
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            "📍 Địa chỉ giao hàng",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14),
            TEXT_COLOR
        ));
    }

    private void setupComponents() {
        // Địa chỉ chi tiết
        addressField = new JTextField();
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));

        // Phường/Xã
        wardField = new JTextField();
        wardField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        wardField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));

        // Quận/Huyện
        districtField = new JTextField();
        districtField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        districtField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));

        // Thành phố
        cityField = new JTextField();
        cityField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));

        // Ghi chú
        noteField = new JTextArea(3, 20);
        noteField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noteField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
        noteField.setLineWrap(true);
        noteField.setWrapStyleWord(true);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Địa chỉ chi tiết
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Địa chỉ chi tiết:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(addressField, gbc);

        // Phường/Xã
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Phường/Xã:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(wardField, gbc);

        // Quận/Huyện
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Quận/Huyện:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(districtField, gbc);

        // Thành phố
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Thành phố:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cityField, gbc);

        // Ghi chú
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        JScrollPane noteScroll = new JScrollPane(noteField);
        noteScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(noteScroll, gbc);
    }

    // Getters
    public String getAddress() { return addressField.getText().trim(); }
    public String getWard() { return wardField.getText().trim(); }
    public String getDistrict() { return districtField.getText().trim(); }
    public String getCity() { return cityField.getText().trim(); }
    public String getNote() { return noteField.getText().trim(); }

    // Setters
    public void setAddress(String address) { addressField.setText(address); }
    public void setWard(String ward) { wardField.setText(ward); }
    public void setDistrict(String district) { districtField.setText(district); }
    public void setCity(String city) { cityField.setText(city); }
    public void setNote(String note) { noteField.setText(note); }

    // Validation
    public boolean validateFields(String deliveryMethod) {
        if (addressField.getText().trim().isEmpty() && !deliveryMethod.equals("Tại cửa hàng")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ giao hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            addressField.requestFocus();
            return false;
        }
        return true;
    }

    public JTextField getAddressField() { return addressField; }
    
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        if (!getAddress().isEmpty()) fullAddress.append(getAddress());
        if (!getWard().isEmpty()) fullAddress.append(", ").append(getWard());
        if (!getDistrict().isEmpty()) fullAddress.append(", ").append(getDistrict());
        if (!getCity().isEmpty()) fullAddress.append(", ").append(getCity());
        return fullAddress.toString();
    }
}