/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Banner;
import com.mycompany.storeapp.model.entity.Banner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Dialog hiển thị chi tiết banner
 * @author ADMIN
 */
public class BannerDetailsDialog extends JDialog {
    private Banner banner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public BannerDetailsDialog(JFrame parent, Banner banner) {
        super(parent, "Chi tiết banner", true);
        this.banner = banner;
        
        initComponents();
        setupLayout();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        getContentPane().setBackground(Color.WHITE);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        JPanel contentPanel = createContentPanel();
        JPanel buttonPanel = createButtonPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Chi tiết banner");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel statusLabel = new JLabel();
        statusLabel.setText(banner.getIsActive() == 1 ? "HOẠT ĐỘNG" : "KHÔNG HOẠT ĐỘNG");
        statusLabel.setBackground(banner.getIsActive() == 1 ? new Color(46, 204, 113) : new Color(231, 76, 60));
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
        
        panel.add(createDetailRow("ID:", String.valueOf(banner.getBannerId())));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Tiêu đề:", banner.getTitle()));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Phụ đề:", banner.getSubtitle() != null ? banner.getSubtitle() : ""));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Văn bản nút:", banner.getButtonText() != null ? banner.getButtonText() : ""));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Liên kết nút:", banner.getButtonLink() != null ? banner.getButtonLink() : ""));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Thứ tự:", String.valueOf(banner.getOrderSequence())));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Ngày bắt đầu:", banner.getStartDate() != null ? dateFormat.format(banner.getStartDate()) : ""));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Ngày kết thúc:", banner.getEndDate() != null ? dateFormat.format(banner.getEndDate()) : ""));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Ngày tạo:", banner.getCreated_at() != null ? dateFormat.format(banner.getCreated_at()) : ""));
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createDetailRow("Ngày cập nhật:", banner.getUpdated_at() != null ? dateFormat.format(banner.getUpdated_at()) : ""));
        
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