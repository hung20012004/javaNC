package com.mycompany.storeapp.view.page.admin.Role;

import com.mycompany.storeapp.model.entity.Role;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class RoleFormDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    private Role role;
    private boolean isEditMode;
    private boolean confirmed = false;

    public RoleFormDialog(JFrame parent, String title, Role role) {
        super(parent, title, true);
        this.role = role != null ? role : new Role();
        this.isEditMode = (role != null);

        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(0, 35));
        descriptionArea = new JTextArea(3, 0);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        saveButton = new JButton(isEditMode ? "Cập nhật" : "Thêm mới");
        saveButton.setBackground(new java.awt.Color(52, 152, 219));
        saveButton.setForeground(java.awt.Color.WHITE);
        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new java.awt.Color(149, 165, 166));
        cancelButton.setForeground(java.awt.Color.WHITE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(createFieldPanel("Tên vai trò *", nameField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Mô tả", new JScrollPane(descriptionArea)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(mainPanel, BorderLayout.CENTER);
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
                saveRole();
            }
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        if (isEditMode) {
            nameField.setText(role.getName());
            descriptionArea.setText(role.getDescription());
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên vai trò không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (nameField.getText().trim().length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên vai trò không được vượt quá 50 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (descriptionArea.getText().length() > 500) {
            JOptionPane.showMessageDialog(this, "Mô tả không được vượt quá 500 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveRole() {
        role.setName(nameField.getText().trim());
        role.setDescription(descriptionArea.getText().trim());
        confirmed = true;
        dispose();
    }

    public Role getRole() { return role; }
    public boolean isConfirmed() { return confirmed; }
}