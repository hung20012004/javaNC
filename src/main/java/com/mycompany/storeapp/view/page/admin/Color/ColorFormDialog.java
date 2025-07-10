/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Color;

/**
 *
 * @author ADMIN
 */
import com.mycompany.storeapp.model.entity.ProductColor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.border.EmptyBorder;

public class ColorFormDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    private ProductColor color;
    private boolean isEditMode;
    private boolean confirmed = false;

    public ColorFormDialog(JFrame parent, String title, ProductColor color) {
        super(parent, title, true);
        this.color = color != null ? color : new Color(46, 204, 113);
        this.isEditMode = (color != null);

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
        mainPanel.add(createFieldPanel("Tên màu *", nameField));
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
                saveColor();
            }
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        if (isEditMode) {
            nameField.setText(color.getName());
            descriptionArea.setText(color.getDescription());
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên màu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (nameField.getText().trim().length() > 100) {
            JOptionPane.showMessageDialog(this, "Tên màu không được vượt quá 100 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (descriptionArea.getText().length() > 500) {
            JOptionPane.showMessageDialog(this, "Mô tả không được vượt quá 500 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveColor() {
        color.setName(nameField.getText().trim());
        color.setDescription(descriptionArea.getText().trim());
        if (!isEditMode) {
            color.setCreated_at(new Date());
            color.setUpdated_at(new Date());
        } else {
            color.setUpdated_at(new Date());
        }
        confirmed = true;
        dispose();
    }

    public ProductColor getColor() { return color; }
    public boolean isConfirmed() { return confirmed; }
}
