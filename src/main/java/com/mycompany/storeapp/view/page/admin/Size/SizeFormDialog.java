/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Size;

/**
 *
 * @author ADMIN
 */
import com.mycompany.storeapp.model.entity.Size;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import javax.swing.border.EmptyBorder;

public class SizeFormDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JButton saveButton;
    private JButton cancelButton;
    private Size size;
    private boolean isEditMode;
    private boolean confirmed = false;

    public SizeFormDialog(JFrame parent, String title, Size size) {
        super(parent, title, true);
        this.size = size != null ? size : new Size();
        this.isEditMode = (size != null);

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
        mainPanel.add(createFieldPanel("Tên chất liệu *", nameField));
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
                saveSize();
            }
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        if (isEditMode) {
            nameField.setText(size.getName());
            descriptionArea.setText(size.getDescription());
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên chất liệu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (nameField.getText().trim().length() > 100) {
            JOptionPane.showMessageDialog(this, "Tên chất liệu không được vượt quá 100 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (descriptionArea.getText().length() > 500) {
            JOptionPane.showMessageDialog(this, "Mô tả không được vượt quá 500 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveSize() {
        size.setName(nameField.getText().trim());
        size.setDescription(descriptionArea.getText().trim());
        if (!isEditMode) {
            size.setCreated_at(new Date());
            size.setUpdated_at(new Date());
        } else {
            size.setUpdated_at(new Date());
        }
        confirmed = true;
        dispose();
    }

    public Size getsize()
    {
        return size;
    }
    
    
    public boolean isConfirmed() { return confirmed; }
 }

