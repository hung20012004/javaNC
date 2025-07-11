/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Tag;

/**
 *
 * @author Hi
 */
import com.mycompany.storeapp.model.entity.Tag;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class TagFormDialog extends JDialog {
    private JTextField nameField;
    private JTextField slugField;
    private JButton saveButton;
    private JButton cancelButton;
    private Tag tag;
    private boolean isEditMode;
    private boolean confirmed = false;

    public TagFormDialog(JFrame parent, String title, Tag tag) {
        super(parent, title, true);
        this.tag = tag != null ? tag : new Tag();
        this.isEditMode = (tag != null);

        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 250);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(0, 35));
        
        slugField = new JTextField();
        slugField.setPreferredSize(new Dimension(0, 35));
        slugField.setEnabled(false); // Slug sẽ được tự động tạo từ name
        slugField.setBackground(new Color(240, 240, 240));

        saveButton = new JButton(isEditMode ? "Cập nhật" : "Thêm mới");
        saveButton.setBackground(new java.awt.Color(52, 152, 219));
        saveButton.setForeground(java.awt.Color.WHITE);
        saveButton.setPreferredSize(new Dimension(120, 35));
        
        cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new java.awt.Color(149, 165, 166));
        cancelButton.setForeground(java.awt.Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(80, 35));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add name field
        mainPanel.add(createFieldPanel("Tên tag *", nameField));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Add slug field
        mainPanel.add(createFieldPanel("Slug (tự động tạo)", slugField));
        mainPanel.add(Box.createVerticalStrut(15));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> {
            if (validateForm()) {
                saveTag();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        // Auto-generate slug when name changes
        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                generateSlugFromName();
            }
        });
    }

    private void loadData() {
        if (isEditMode) {
            nameField.setText(tag.getName());
            slugField.setText(tag.getSlug());
        }
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên tag không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (name.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên tag không được vượt quá 50 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        return true;
    }

    private void saveTag() {
        tag.setName(nameField.getText().trim());
        tag.setSlug(slugField.getText().trim());
        
        confirmed = true;
        dispose();
    }

    private void generateSlugFromName() {
        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            String slug = generateSlug(name);
            slugField.setText(slug);
            tag.setSlug(slug);
        }
    }

    /**
     * Tạo slug từ tên tag
     * @param name Tên tag
     * @return Slug string
     */
    private String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        
        return name.trim()
                .toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    public Tag getTag() { 
        return tag; 
    }
    
    public boolean isConfirmed() { 
        return confirmed; 
    }
}