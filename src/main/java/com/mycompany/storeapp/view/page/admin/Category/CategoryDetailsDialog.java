/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Category;

import com.mycompany.storeapp.model.entity.Category;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Dialog hiển thị chi tiết category
 * @author Hi
 */
public class CategoryDetailsDialog extends JDialog {
    private Category category;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public CategoryDetailsDialog(JFrame parent, Category category) {
        super(parent, "Chi tiết danh mục", true);
        this.category = category;
        
        initComponents();
        setupLayout();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        getContentPane().setBackground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Chi tiết danh mục");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel statusLabel = new JLabel();
        if (category.getIsActive()) {
            statusLabel.setText("HOẠT ĐỘNG");
            statusLabel.setBackground(new Color(46, 204, 113));
        } else {
            statusLabel.setText("KHÔNG HOẠT ĐỘNG");
            statusLabel.setBackground(new Color(231, 76, 60));
        }
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        
        // ID
        panel.add(createDetailRow("ID:", String.valueOf(category.getCategoryId())));
        panel.add(Box.createVerticalStrut(15));
        
        // Name
        panel.add(createDetailRow("Tên danh mục:", category.getName()));
        panel.add(Box.createVerticalStrut(15));
        
        // Slug
        panel.add(createDetailRow("Slug:", category.getSlug()));
        panel.add(Box.createVerticalStrut(15));
        
        // Description
        if (category.getDescription() != null && !category.getDescription().trim().isEmpty()) {
            panel.add(createDetailTextArea("Mô tả:", category.getDescription()));
            panel.add(Box.createVerticalStrut(15));
        }
        
        
        // Created date
        if (category.getCreated_at()!= null) {
            panel.add(createDetailRow("Ngày tạo:", dateFormat.format(category.getCreated_at())));
            panel.add(Box.createVerticalStrut(15));
        }
        
        // Updated date
        if (category.getUpdated_at() != null) {
            panel.add(createDetailRow("Ngày cập nhật:", dateFormat.format(category.getUpdated_at())));
        }
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createDetailRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(new Color(52, 73, 94));
        labelComponent.setPreferredSize(new Dimension(120, 30));
        
        JLabel valueComponent = new JLabel(value != null ? value : "");
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(new Color(51, 51, 51));
        
        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(valueComponent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDetailTextArea(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(new Color(52, 73, 94));
        
        JTextArea textArea = new JTextArea(value != null ? value : "");
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(new Color(51, 51, 51));
        textArea.setBackground(new Color(248, 249, 250));
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(3);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(labelComponent, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(236, 240, 241)));
        
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 40));
        closeButton.setBackground(new Color(52, 152, 219));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(new Color(52, 152, 219));
            }
        });
        
        closeButton.addActionListener(e -> dispose());
        
        panel.add(closeButton);
        
        return panel;
    }
}
