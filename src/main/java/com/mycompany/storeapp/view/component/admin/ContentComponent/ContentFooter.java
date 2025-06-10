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
 * Component footer tái sử dụng cho phân trang
 * @author Hi
 */
public class ContentFooter extends JPanel {
    private JLabel infoLabel;
    private JButton firstButton;
    private JButton prevButton;
    private JLabel currentPageLabel;
    private JButton nextButton;
    private JButton lastButton;
    private JComboBox<Integer> pageSizeComboBox;
    
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalItems = 0;
    private int pageSize = 12;
    private int startItem = 0;
    private int endItem = 0;
    
    public ContentFooter() {
        initComponents();
        setupLayout();
        setupStyling();
        updateDisplay();
    }
    
    private void initComponents() {
        // Info label
        infoLabel = new JLabel();
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(51, 51, 51));
        
        // Navigation buttons
        firstButton = createNavButton("<<");
        prevButton = createNavButton("<");
        nextButton = createNavButton(">");
        lastButton = createNavButton(">>");
        
        // Current page label
        currentPageLabel = new JLabel();
        currentPageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        currentPageLabel.setForeground(new Color(51, 51, 51));
        currentPageLabel.setPreferredSize(new Dimension(100, 30));
        currentPageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Page size combo box
        Integer[] pageSizes = {5, 10, 12, 15, 20, 25, 50};
        pageSizeComboBox = new JComboBox<>(pageSizes);
        pageSizeComboBox.setSelectedItem(12);
        pageSizeComboBox.setPreferredSize(new Dimension(60, 30));
        pageSizeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(40, 30));
        button.setBackground(new Color(236, 240, 241));
        button.setForeground(new Color(51, 51, 51));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(52, 152, 219));
                    button.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(236, 240, 241));
                    button.setForeground(new Color(51, 51, 51));
                }
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setBackground(Color.WHITE);
        
        // Left panel - info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(infoLabel);
        
        // Center panel - navigation
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(firstButton);
        centerPanel.add(prevButton);
        centerPanel.add(currentPageLabel);
        centerPanel.add(nextButton);
        centerPanel.add(lastButton);
        
        // Right panel - page size
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(new JLabel("Hiển thị: "));
        rightPanel.add(pageSizeComboBox);
        rightPanel.add(new JLabel(" dòng/trang"));
        
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void setupStyling() {
        // Set initial button states
        updateButtonStates();
    }
    
    private void updateDisplay() {
        // Calculate display values
        startItem = (currentPage - 1) * pageSize + 1;
        endItem = Math.min(currentPage * pageSize, totalItems);
        
        // Update info label
        if (totalItems == 0) {
            infoLabel.setText("Không có dữ liệu");
        } else {
            infoLabel.setText(String.format("Hiển thị %d - %d của %d kết quả", 
                startItem, endItem, totalItems));
        }
        
        // Update current page label
        currentPageLabel.setText(String.format("%d / %d", currentPage, totalPages));
        
        // Update button states
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        firstButton.setEnabled(currentPage > 1);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
        lastButton.setEnabled(currentPage < totalPages);
        
        // Update disabled button styling
        updateButtonStyle(firstButton);
        updateButtonStyle(prevButton);
        updateButtonStyle(nextButton);
        updateButtonStyle(lastButton);
    }
    
    private void updateButtonStyle(JButton button) {
        if (!button.isEnabled()) {
            button.setBackground(new Color(236, 240, 241));
            button.setForeground(new Color(189, 195, 199));
        } else {
            button.setBackground(new Color(236, 240, 241));
            button.setForeground(new Color(51, 51, 51));
        }
    }
    
    // Public methods để cập nhật pagination
    public void updatePagination(int totalItems, int currentPage, int pageSize) {
        this.totalItems = totalItems;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (this.totalPages == 0) {
            this.totalPages = 1;
        }
        
        // Đảm bảo currentPage không vượt quá totalPages
        if (this.currentPage > this.totalPages) {
            this.currentPage = this.totalPages;
        }
        
        updateDisplay();
    }
    
    // Getter methods
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getPageSize() {
        return (Integer) pageSizeComboBox.getSelectedItem();
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    // Event listener methods
    public void addFirstButtonListener(ActionListener listener) {
        firstButton.addActionListener(listener);
    }
    
    public void addPrevButtonListener(ActionListener listener) {
        prevButton.addActionListener(listener);
    }
    
    public void addNextButtonListener(ActionListener listener) {
        nextButton.addActionListener(listener);
    }
    
    public void addLastButtonListener(ActionListener listener) {
        lastButton.addActionListener(listener);
    }
    
    public void addPageSizeChangeListener(ActionListener listener) {
        pageSizeComboBox.addActionListener(listener);
    }
}