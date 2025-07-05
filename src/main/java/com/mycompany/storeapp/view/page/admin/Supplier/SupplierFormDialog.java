/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Supplier;

/**
 *
 * @author ADMIN
 */
import com.mycompany.storeapp.model.entity.Supplier;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.border.EmptyBorder;

public class SupplierFormDialog extends JDialog {
    private JTextField nameField, contactNameField, phoneField, emailField, addressField;
    private JTextArea descriptionArea;
    private JCheckBox isActiveCheckBox;
    private JButton saveButton, cancelButton;
    private Supplier supplier;
    private boolean isEditMode;
    private boolean confirmed = false;

    public SupplierFormDialog(JFrame parent, String title, Supplier supplier) {
        super(parent, title, true);
        this.supplier = supplier != null ? supplier : new Supplier();
        this.isEditMode = (supplier != null);

        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        nameField = new JTextField();
        contactNameField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextField();
        descriptionArea = new JTextArea(3, 0);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        isActiveCheckBox = new JCheckBox("Kích hoạt");

        saveButton = new JButton(isEditMode ? "Cập nhật" : "Thêm mới");
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(createFieldPanel("Tên nhà cung cấp *", nameField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Tên liên hệ", contactNameField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Số điện thoại", phoneField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Email", emailField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Địa chỉ", addressField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Mô tả", new JScrollPane(descriptionArea)));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(isActiveCheckBox);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.add(new JLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                saveSupplier();
            }
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        if (isEditMode) {
            nameField.setText(supplier.getName() != null ? supplier.getName() : "");
            contactNameField.setText(supplier.getContactName() != null ? supplier.getContactName() : "");
            phoneField.setText(supplier.getPhone() != null ? supplier.getPhone() : "");
            emailField.setText(supplier.getEmail() != null ? supplier.getEmail() : "");
            addressField.setText(supplier.getAddress() != null ? supplier.getAddress() : "");
            descriptionArea.setText(supplier.getDescription() != null ? supplier.getDescription() : "");
            isActiveCheckBox.setSelected(supplier.getIsActive() != null ? supplier.getIsActive() : false);
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhà cung cấp không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (nameField.getText().trim().length() > 100) {
            JOptionPane.showMessageDialog(this, "Tên nhà cung cấp không được vượt quá 100 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (phoneField.getText().trim().length() > 0 && !phoneField.getText().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải là 10 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (emailField.getText().trim().length() > 0 && !emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (descriptionArea.getText().length() > 500) {
            JOptionPane.showMessageDialog(this, "Mô tả không được vượt quá 500 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveSupplier() {
        supplier.setName(nameField.getText().trim());
        supplier.setContactName(contactNameField.getText().trim());
        supplier.setPhone(phoneField.getText().trim());
        supplier.setEmail(emailField.getText().trim());
        supplier.setAddress(addressField.getText().trim());
        supplier.setDescription(descriptionArea.getText().trim());
        supplier.setIsActive(isActiveCheckBox.isSelected());
        if (!isEditMode) {
            supplier.setCreatedAt(new Date());
            supplier.setUpdatedAt(new Date());
        } else {
            supplier.setUpdatedAt(new Date());
        }
        confirmed = true;
        dispose();
    }

    public Supplier getSupplier() { return supplier; }
    public boolean isConfirmed() { return confirmed; }
}
