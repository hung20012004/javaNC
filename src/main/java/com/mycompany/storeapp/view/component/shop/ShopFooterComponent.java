/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.shop;

/**
 *
 * @author Manh Hung
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ShopFooterComponent extends JPanel {
    
    // Colors
    private static final Color FOOTER_BACKGROUND = new Color(249, 250, 251);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(107, 114, 128);
    private static final Color ACCENT_COLOR = new Color(59, 130, 246);
    private static final Color STATUS_ONLINE_COLOR = new Color(16, 185, 129);
    private static final Color STATUS_OFFLINE_COLOR = new Color(239, 68, 68);
    
    // Components
    private JLabel statusLabel;
    private JLabel versionLabel;
    private JLabel copyrightLabel;
    private JLabel connectionLabel;
    private JLabel shortcutsLabel;
    
    // Status
    private boolean isOnline = true;
    
    public ShopFooterComponent() {
        initializeFooter();
        setupComponents();
        setupLayout();
    }
    
    private void initializeFooter() {
        setLayout(new BorderLayout());
        setBackground(FOOTER_BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(8, 20, 8, 20)
        ));
        setPreferredSize(new Dimension(0, 35));
    }
    
    private void setupComponents() {
        // Left panel - Status and connection
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        // Connection status
        connectionLabel = new JLabel();
        updateConnectionStatus();
        connectionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        connectionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        connectionLabel.setToolTipText("Nhấp để kiểm tra kết nối");
        connectionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkConnection();
            }
        });
        
        // System status
        statusLabel = new JLabel("⚡ Hệ thống hoạt động bình thường");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        leftPanel.add(connectionLabel);
        leftPanel.add(statusLabel);
        
        // Center panel - Keyboard shortcuts
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        
        shortcutsLabel = new JLabel("F1: POS | F2: Giỏ hàng | F3: Đơn mới | F4: Thanh toán | Alt+Q: Thoát");
        shortcutsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        shortcutsLabel.setForeground(TEXT_COLOR);
        shortcutsLabel.setToolTipText("Phím tắt hệ thống");
        
        centerPanel.add(shortcutsLabel);
        
        // Right panel - Version and copyright
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        
        versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(ACCENT_COLOR);
        versionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        versionLabel.setToolTipText("Thông tin phiên bản");
        versionLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showVersionInfo();
            }
        });
        
        copyrightLabel = new JLabel("© 2024 POS System - Manh Hung");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(TEXT_COLOR);
        copyrightLabel.setBorder(new EmptyBorder(0, 15, 0, 0));
        
        rightPanel.add(versionLabel);
        rightPanel.add(copyrightLabel);
        
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void setupLayout() {
        // Additional layout setup if needed
    }
    
    private void updateConnectionStatus() {
        if (isOnline) {
            connectionLabel.setText("🟢 Kết nối");
            connectionLabel.setForeground(STATUS_ONLINE_COLOR);
        } else {
            connectionLabel.setText("🔴 Mất kết nối");
            connectionLabel.setForeground(STATUS_OFFLINE_COLOR);
        }
    }
    
    private void checkConnection() {
        // Simulate connection check
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                connectionLabel.setText("🟡 Đang kiểm tra...");
                connectionLabel.setForeground(TEXT_COLOR);
                
                // Simulate network check delay
                Thread.sleep(1000);
                
                // Random connection result for demonstration
                return Math.random() > 0.2; // 80% success rate
            }
            
            @Override
            protected void done() {
                try {
                    isOnline = get();
                    updateConnectionStatus();
                    
                    if (isOnline) {
                        setStatus("✅ Kết nối thành công");
                    } else {
                        setStatus("❌ Kiểm tra kết nối mạng");
                    }
                } catch (Exception e) {
                    isOnline = false;
                    updateConnectionStatus();
                    setStatus("❌ Lỗi kiểm tra kết nối");
                }
            }
        };
        worker.execute();
    }
    
    private void showVersionInfo() {
        String info = "<html><body style='width: 250px; padding: 10px;'>" +
                "<h3>Thông tin phiên bản</h3>" +
                "<p><b>Phiên bản:</b> 1.0.0</p>" +
                "<p><b>Ngày phát hành:</b> 19/06/2025</p>" +
                "<p><b>Tác giả:</b> Manh Hung</p>" +
                "<p><b>Mô tả:</b> Hệ thống quản lý bán hàng POS</p>" +
                "<hr>" +
                "<p style='font-size: 11px; color: #666;'>" +
                "Phiên bản Java: " + System.getProperty("java.version") + "<br>" +
                "Hệ điều hành: " + System.getProperty("os.name") +
                "</p>" +
                "</body></html>";
        
        JOptionPane.showMessageDialog(
            this,
            info,
            "Thông tin phiên bản",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Public methods
    public void setStatus(String status) {
        statusLabel.setText(status);
        
        // Auto-reset status after 3 seconds
        Timer timer = new Timer(3000, e -> {
            statusLabel.setText("⚡ Hệ thống hoạt động bình thường");
            ((Timer) e.getSource()).stop();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public void setConnectionStatus(boolean online) {
        this.isOnline = online;
        updateConnectionStatus();
    }
    
    public void setVersion(String version) {
        versionLabel.setText("v" + version);
    }
    
    public void setCopyright(String copyright) {
        copyrightLabel.setText(copyright);
    }
    
    public void showMessage(String message) {
        setStatus(message);
    }
    
    public void showError(String error) {
        setStatus("❌ " + error);
    }
    
    public void showSuccess(String success) {
        setStatus("✅ " + success);
    }
    
    public void showWarning(String warning) {
        setStatus("⚠️ " + warning);
    }
    
    public void showInfo(String info) {
        setStatus("ℹ️ " + info);
    }
    
    // Responsive design
    @Override
    public void doLayout() {
        super.doLayout();
        
        // Hide shortcuts on small screens
        if (getWidth() < 1000) {
            shortcutsLabel.setVisible(false);
        } else {
            shortcutsLabel.setVisible(true);
        }
        
        // Adjust copyright text on very small screens
        if (getWidth() < 600) {
            copyrightLabel.setText("© 2024 POS");
        } else {
            copyrightLabel.setText("© 2024 POS System - Manh Hung");
        }
    }
}