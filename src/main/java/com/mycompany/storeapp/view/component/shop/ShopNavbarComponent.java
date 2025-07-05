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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ShopNavbarComponent extends JPanel {
    
    // Colors
    private static final Color NAVBAR_BACKGROUND = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color ACTIVE_COLOR = new Color(59, 130, 246);
    private static final Color HOVER_COLOR = new Color(249, 250, 251);
    private static final Color TEXT_COLOR = new Color(75, 85, 99);
    private static final Color ACTIVE_TEXT_COLOR = Color.WHITE;
    
    // Components
    private List<NavbarItem> navbarItems;
    private NavbarItem activeItem;
    private ActionListener menuActionListener;
    
    public ShopNavbarComponent() {
        initializeNavbar();
        createNavbarItems();
    }
    
    private void initializeNavbar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBackground(NAVBAR_BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(8, 20, 8, 20)
        ));
        setPreferredSize(new Dimension(0, 55));
        
        navbarItems = new ArrayList<>();
    }
    
    private void createNavbarItems() {
        // Tạo các menu item cho shop
        addNavbarItem("pos", "🛒", "POS", "Điểm bán hàng", true);
        addNavbarItem("products", "📦", "Sản phẩm", "Danh sách sản phẩm", false);
        addNavbarItem("orders", "📋", "Đơn hàng", "Quản lý đơn hàng", false);
        addNavbarItem("customers", "👥", "Khách hàng", "Thông tin khách hàng", false);
        addNavbarItem("reports", "📊", "Báo cáo", "Thống kê bán hàng", false);
        addNavbarItem("settings", "⚙️", "Cài đặt", "Cấu hình hệ thống", false);
    }
    
    private void addNavbarItem(String action, String icon, String text, String tooltip, boolean isActive) {
        NavbarItem item = new NavbarItem(action, icon, text, tooltip);
        
        if (isActive) {
            activeItem = item; // FIX: Đặt activeItem trực tiếp thay vì gọi setActiveItem
            item.setActive(true);
        }
        
        // Add click handler - FIX: Sửa logic xử lý click
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleItemClick(item); // FIX: Gọi method riêng để xử lý click
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!item.isActive()) {
                    item.setBackground(HOVER_COLOR);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!item.isActive()) {
                    item.setBackground(NAVBAR_BACKGROUND);
                }
            }
        });
        
        navbarItems.add(item);
        add(item);
    }
    
    // FIX: Method mới để xử lý click item
    private void handleItemClick(NavbarItem clickedItem) {
        // Chỉ xử lý nếu không phải item đang active
        if (clickedItem != activeItem) {
            // Deactivate current active item
            if (activeItem != null) {
                activeItem.setActive(false);
            }
            
            // Set new active item
            activeItem = clickedItem;
            clickedItem.setActive(true);
            
            // Trigger action listener
            if (menuActionListener != null) {
                ActionEvent event = new ActionEvent(clickedItem, ActionEvent.ACTION_PERFORMED, clickedItem.getAction());
                menuActionListener.actionPerformed(event);
            }
        }
    }
    
    // FIX: Sửa method setActiveItem để tránh gọi đệ quy
    public void setActiveItem(String action) {
        // Deactivate current active item
        if (activeItem != null) {
            activeItem.setActive(false);
        }
        
        // Find and activate new item
        for (NavbarItem item : navbarItems) {
            if (item.getAction().equals(action)) {
                activeItem = item;
                item.setActive(true);
                break;
            }
        }
    }
    
    // FIX: Loại bỏ method setActiveItem(NavbarItem) gây đệ quy
    
    public void setMenuActionListener(ActionListener listener) {
        this.menuActionListener = listener;
    }
    
    // Inner class for navbar items
    private class NavbarItem extends JPanel {
        private String action;
        private String icon;
        private String text;
        private String tooltip;
        private boolean isActive;
        
        private JLabel iconLabel;
        private JLabel textLabel;
        
        public NavbarItem(String action, String icon, String text, String tooltip) {
            this.action = action;
            this.icon = icon;
            this.text = text;
            this.tooltip = tooltip;
            this.isActive = false;
            
            initializeItem();
        }
        
        private void initializeItem() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
            setBackground(NAVBAR_BACKGROUND);
            setBorder(new EmptyBorder(8, 16, 8, 16));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setToolTipText(tooltip);
            
            // Icon label
            iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            
            // Text label
            textLabel = new JLabel(text);
            textLabel.setFont(new Font("Segoe UI", Font.TRUETYPE_FONT, 14));
            textLabel.setForeground(TEXT_COLOR);
            
            add(iconLabel);
            add(textLabel);
            
            updateAppearance();
        }
        
        public void setActive(boolean active) {
            this.isActive = active;
            updateAppearance();
        }
        
        private void updateAppearance() {
            if (isActive) {
                setBackground(ACTIVE_COLOR);
                textLabel.setForeground(ACTIVE_TEXT_COLOR);
                iconLabel.setForeground(ACTIVE_TEXT_COLOR);
            } else {
                setBackground(NAVBAR_BACKGROUND);
                textLabel.setForeground(TEXT_COLOR);
                iconLabel.setForeground(TEXT_COLOR);
            }
            
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (isActive) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
            }
        }
        
        // Getters
        public String getAction() { return action; }
        public boolean isActive() { return isActive; }
    }
}