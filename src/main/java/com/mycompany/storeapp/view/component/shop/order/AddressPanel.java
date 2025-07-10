package com.mycompany.storeapp.view.component.shop.order;

import com.mycompany.storeapp.controller.shop.ShippingAddressController;
import com.mycompany.storeapp.model.entity.ShippingAddress;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class AddressPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);
    private static final Color BUTTON_COLOR = new Color(59, 130, 246);
    private static final Color DELETE_COLOR = new Color(239, 68, 68);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);

    // Controllers
    private ShippingAddressController addressController;
    
    // Components for address selection
    private JComboBox<AddressComboItem> addressComboBox;
    private JButton btnNewAddress;
    private JButton btnEditAddress;
    private JButton btnDeleteAddress;
    
    // Components for address form
    private JTextField recipientNameField;
    private JTextField phoneField;
    private JTextField addressField;
    private JTextField wardField;
    private JTextField districtField;
    private JTextField cityField;
    private JTextArea noteField;
    private JCheckBox defaultAddressCheckBox;
    
    // Form buttons
    private JButton btnSave;
    private JButton btnCancel;
    
    // Current state
    private boolean isEditing = false;
    private ShippingAddress currentEditingAddress = null;
    
    // Store form components for easy access
    private List<Component> formComponents = new ArrayList<>();
    private List<Component> formLabels = new ArrayList<>();
    
    public AddressPanel() {
        this.addressController = new ShippingAddressController();
        initializePanel();
        setupComponents();
        setupLayout();
        setupEventListeners();
        loadAddresses();
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
        // Address selection components
        addressComboBox = new JComboBox<>();
        addressComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressComboBox.setPreferredSize(new Dimension(300, 30));
        
        btnNewAddress = createButton("Thêm mới", BUTTON_COLOR);
        btnEditAddress = createButton("Sửa", BUTTON_COLOR);
        btnDeleteAddress = createButton("Xóa", DELETE_COLOR);
        
        // Form components
        recipientNameField = createTextField();
        phoneField = createTextField();
        addressField = createTextField();
        wardField = createTextField();
        districtField = createTextField();
        cityField = createTextField();
        
        noteField = new JTextArea(3, 20);
        noteField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noteField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
        noteField.setLineWrap(true);
        noteField.setWrapStyleWord(true);
        
        defaultAddressCheckBox = new JCheckBox("Đặt làm địa chỉ mặc định");
        defaultAddressCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        defaultAddressCheckBox.setBackground(BACKGROUND_COLOR);
        
        // Form buttons
        btnSave = createButton("Lưu", SUCCESS_COLOR);
        btnCancel = createButton("Hủy", new Color(156, 163, 175));
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
        return field;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 30));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Address selection section
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Chọn địa chỉ:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(addressComboBox, gbc);
        
        // Button panel for address actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(btnNewAddress);
        buttonPanel.add(btnEditAddress);
        buttonPanel.add(btnDeleteAddress);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(buttonPanel, gbc);
        
        // Form section (initially hidden)
        gbc.gridwidth = 1; gbc.weightx = 0;
        
        // Recipient name
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        JLabel recipientLabel = new JLabel("Tên người nhận:");
        add(recipientLabel, gbc);
        formLabels.add(recipientLabel);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(recipientNameField, gbc);
        formComponents.add(recipientNameField);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        add(phoneLabel, gbc);
        formLabels.add(phoneLabel);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(phoneField, gbc);
        formComponents.add(phoneField);

        // Address
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel addressLabel = new JLabel("Địa chỉ chi tiết:");
        add(addressLabel, gbc);
        formLabels.add(addressLabel);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(addressField, gbc);
        formComponents.add(addressField);

        // Ward
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel wardLabel = new JLabel("Phường/Xã:");
        add(wardLabel, gbc);
        formLabels.add(wardLabel);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(wardField, gbc);
        formComponents.add(wardField);

        // District
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel districtLabel = new JLabel("Quận/Huyện:");
        add(districtLabel, gbc);
        formLabels.add(districtLabel);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(districtField, gbc);
        formComponents.add(districtField);

        // City
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel cityLabel = new JLabel("Thành phố:");
        add(cityLabel, gbc);
        formLabels.add(cityLabel);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        add(cityField, gbc);
        formComponents.add(cityField);

        // Note
        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel noteLabel = new JLabel("Ghi chú:");
        add(noteLabel, gbc);
        formLabels.add(noteLabel);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JScrollPane noteScroll = new JScrollPane(noteField);
        noteScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(noteScroll, gbc);
        formComponents.add(noteScroll);
        
        // Default checkbox
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; gbc.weighty = 0; gbc.weightx = 0;
        add(defaultAddressCheckBox, gbc);
        formComponents.add(defaultAddressCheckBox);
        
        // Form buttons
        JPanel formButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        formButtonPanel.setBackground(BACKGROUND_COLOR);
        formButtonPanel.add(btnCancel);
        formButtonPanel.add(btnSave);
        
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        add(formButtonPanel, gbc);
        formComponents.add(formButtonPanel);
        
        // Initially hide form components
        setFormVisible(false);
    }
    
    private void setupEventListeners() {
        // Address combo box selection
        addressComboBox.addActionListener(e -> {
            if (!isEditing) {
                AddressComboItem selected = (AddressComboItem) addressComboBox.getSelectedItem();
                if (selected != null && selected.getAddress() != null) {
                    displayAddressInfo(selected.getAddress());
                }
            }
        });
        
        // New address button
        btnNewAddress.addActionListener(e -> showAddressForm(null));
        
        // Edit address button
        btnEditAddress.addActionListener(e -> {
            AddressComboItem selected = (AddressComboItem) addressComboBox.getSelectedItem();
            if (selected != null && selected.getAddress() != null) {
                showAddressForm(selected.getAddress());
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn địa chỉ để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Delete address button
        btnDeleteAddress.addActionListener(e -> {
            AddressComboItem selected = (AddressComboItem) addressComboBox.getSelectedItem();
            if (selected != null && selected.getAddress() != null) {
                deleteAddress(selected.getAddress());
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn địa chỉ để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Save button
        btnSave.addActionListener(e -> saveAddress());
        
        // Cancel button
        btnCancel.addActionListener(e -> cancelEdit());
    }
    
    private void loadAddresses() {
        addressComboBox.removeAllItems();
        addressComboBox.addItem(new AddressComboItem("Chọn địa chỉ giao hàng", null));
        
        List<ShippingAddress> addresses = addressController.getUserShippingAddresses();
        if (addresses != null && !addresses.isEmpty()) {
            for (ShippingAddress address : addresses) {
                addressComboBox.addItem(new AddressComboItem(address));
            }
        }
    }
    
    private void showAddressForm(ShippingAddress address) {
        isEditing = true;
        currentEditingAddress = address;
        
        if (address == null) {
            // New address
            clearForm();
        } else {
            // Edit existing address
            populateForm(address);
        }
        
        setFormVisible(true);
        setSelectionControlsEnabled(false);
    }
    
    private void populateForm(ShippingAddress address) {
        recipientNameField.setText(address.getRecipientName());
        phoneField.setText(address.getPhone());
        addressField.setText(address.getStreetAddress());
        wardField.setText(address.getWard());
        districtField.setText(address.getDistrict());
        cityField.setText(address.getProvince());
        noteField.setText(""); // Note is not in ShippingAddress entity
        defaultAddressCheckBox.setSelected(address.isDefault());
    }
    
    private void clearForm() {
        recipientNameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        wardField.setText("");
        districtField.setText("");
        cityField.setText("");
        noteField.setText("");
        defaultAddressCheckBox.setSelected(false);
    }
    
    private void saveAddress() {
        try {
            // Validate form data first
            if (!validateFormData()) {
                return;
            }
            
            // Create address from form
            ShippingAddress address = addressController.createShippingAddress(
                recipientNameField.getText().trim(),
                phoneField.getText().trim(),
                addressField.getText().trim(),
                wardField.getText().trim(),
                districtField.getText().trim(),
                cityField.getText().trim(),
                defaultAddressCheckBox.isSelected()
            );
            
            boolean success;
            if (currentEditingAddress == null) {
                // New address
                success = addressController.addShippingAddress(address);
            } else {
                // Update existing address
                address.setAddressId(currentEditingAddress.getAddressId());
                success = addressController.updateShippingAddress(address);
            }
            
            if (success) {
                loadAddresses();
                cancelEdit();
                JOptionPane.showMessageDialog(this, 
                    currentEditingAddress == null ? "Thêm địa chỉ thành công!" : "Cập nhật địa chỉ thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    currentEditingAddress == null ? "Thêm địa chỉ thất bại!" : "Cập nhật địa chỉ thất bại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateFormData() {
        // Check recipient name
        if (recipientNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người nhận!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            recipientNameField.requestFocus();
            return false;
        }
        
        // Check phone
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }
        
        // Check phone format
        String phone = phoneField.getText().trim();
        if (!phone.matches("^[0-9]{10,11}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10-11 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            phoneField.requestFocus();
            return false;
        }
        
        // Check address
        if (addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ chi tiết!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            addressField.requestFocus();
            return false;
        }
        
        // Check ward
        if (wardField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập phường/xã!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            wardField.requestFocus();
            return false;
        }
        
        // Check district
        if (districtField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập quận/huyện!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            districtField.requestFocus();
            return false;
        }
        
        // Check city
        if (cityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thành phố!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            cityField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void cancelEdit() {
        isEditing = false;
        currentEditingAddress = null;
        setFormVisible(false);
        setSelectionControlsEnabled(true);
        clearForm();
    }
    
    private void deleteAddress(ShippingAddress address) {
        if (addressController.deleteShippingAddress(address.getAddressId())) {
            loadAddresses();
        }
    }
    
    private void setFormVisible(boolean visible) {
        for (Component component : formComponents) {
            if (component != null) {
                component.setVisible(visible);
            }
        }
        
        for (Component label : formLabels) {
            if (label != null) {
                label.setVisible(visible);
            }
        }
        
        revalidate();
        repaint();
    }
    
    private void setSelectionControlsEnabled(boolean enabled) {
        addressComboBox.setEnabled(enabled);
        btnNewAddress.setEnabled(enabled);
        btnEditAddress.setEnabled(enabled);
        btnDeleteAddress.setEnabled(enabled);
    }
    
    private void displayAddressInfo(ShippingAddress address) {
        // This could be used to show address details in a separate info panel
        // For now, we'll just ensure the combo box shows the selected address
    }
    
    // Public methods for external access
    public ShippingAddress getSelectedAddress() {
        AddressComboItem selected = (AddressComboItem) addressComboBox.getSelectedItem();
        return selected != null ? selected.getAddress() : null;
    }
    
    public String getFullAddress() {
        ShippingAddress address = getSelectedAddress();
        return address != null ? addressController.getFormattedAddress(address) : "";
    }
    
    public boolean validateFields(String deliveryMethod) {
        if (getSelectedAddress() == null && !deliveryMethod.equals("Tại cửa hàng")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn địa chỉ giao hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    // Getters for backward compatibility
    public String getAddress() {
        ShippingAddress address = getSelectedAddress();
        return address != null ? address.getStreetAddress() : "";
    }
    
    public String getWard() {
        ShippingAddress address = getSelectedAddress();
        return address != null ? address.getWard() : "";
    }
    
    public String getDistrict() {
        ShippingAddress address = getSelectedAddress();
        return address != null ? address.getDistrict() : "";
    }
    
    public String getCity() {
        ShippingAddress address = getSelectedAddress();
        return address != null ? address.getProvince() : "";
    }
    
    public String getNote() {
        return noteField.getText().trim();
    }
    
    public JTextField getAddressField() {
        return addressField;
    }
    
    // Helper class for combo box items
    private static class AddressComboItem {
        private String displayText;
        private ShippingAddress address;
        
        public AddressComboItem(String displayText, ShippingAddress address) {
            this.displayText = displayText;
            this.address = address;
        }
        
        public AddressComboItem(ShippingAddress address) {
            this.address = address;
            this.displayText = formatAddressForDisplay(address);
        }
        
        private String formatAddressForDisplay(ShippingAddress address) {
            StringBuilder sb = new StringBuilder();
            if (address.getRecipientName() != null) {
                sb.append(address.getRecipientName());
                sb.append(" - ");
            }
            if (address.getStreetAddress() != null) {
                sb.append(address.getStreetAddress());
            }
            if (address.getWard() != null && !address.getWard().isEmpty()) {
                sb.append(", ").append(address.getWard());
            }
            if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
                sb.append(", ").append(address.getDistrict());
            }
            if (address.getProvince() != null && !address.getProvince().isEmpty()) {
                sb.append(", ").append(address.getProvince());
            }
            return sb.toString();
        }
        
        public ShippingAddress getAddress() {
            return address;
        }
        
        @Override
        public String toString() {
            return displayText;
        }
    }
}