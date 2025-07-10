/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.admin.ContentComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Component header tái sử dụng cho các form quản lý
 * @author Hi
 */
public class ContentHeader extends JPanel {
    private JLabel titleLabel;
    private JButton addButton;
    private JTextField searchField;
    private JButton searchButton;
    private JButton exportExcelButton;
    
    public ContentHeader(String title) {
        initComponents(title);
        setupLayout();
        setupStyling();
    }
    
    private void initComponents(String title) {
        // Title label
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        
        // Add button
        addButton = new JButton("Thêm mới");
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.setBackground(new Color(52, 152, 219));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Search field
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Search button
        searchButton = new JButton("Tìm kiếm");
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.setBackground(new Color(46, 204, 113));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(false);
        searchButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        exportExcelButton = new JButton("Xuất Excel");
        exportExcelButton.setPreferredSize(new Dimension(120, 35));
        exportExcelButton.setBackground(new Color(230, 126, 34));
        exportExcelButton.setForeground(Color.WHITE);
        exportExcelButton.setFocusPainted(false);
        exportExcelButton.setBorderPainted(false);
        exportExcelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        exportExcelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Left panel với title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        // Right panel với search và add button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(searchField);
        rightPanel.add(searchButton);
        rightPanel.add(exportExcelButton);
        rightPanel.add(addButton);
        
        
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void setupStyling() {
        // Hover effects for buttons
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButton.setBackground(new Color(52, 152, 219));
            }
        });
        
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(new Color(46, 204, 113));
            }
        });
        
        exportExcelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exportExcelButton.setBackground(new Color(211, 84, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exportExcelButton.setBackground(new Color(230, 126, 34));
            }
        });
    }
    
    // Getter methods để truy cập components từ bên ngoài
    public JButton getAddButton() {
        return addButton;
    }
    
    public JButton getSearchButton() {
        return searchButton;
    }
    
    public JTextField getSearchField() {
        return searchField;
    }
    
    public String getSearchText() {
        return searchField.getText().trim();
    }
    
    public JButton getExportExcelButton() {
        return exportExcelButton;
    }
    
    public void clearSearch() {
        searchField.setText("");
    }
    
    // Methods để set event listeners
    public void addAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }
    
    public void addSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }
    
    public void addExportExcelButtonListener(ActionListener listener) {
        exportExcelButton.addActionListener(listener);
    }
    
    public void addSearchFieldListener(java.awt.event.KeyListener listener) {
        searchField.addKeyListener(listener);
    }
}
