/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.admin.SidebarComponent;

/**
 *
 * @author Hi
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MenuGroup extends JPanel {
    private String groupTitle;
    private List<MenuItem> menuItems;
    private Color backgroundColor;
    private JPanel itemsContainer;
    private JLabel groupLabel;
    
    public MenuGroup(String title, Color backgroundColor) {
        this.groupTitle = title;
        this.backgroundColor = backgroundColor;
        this.menuItems = new ArrayList<>();
        
        initializeComponent();
    }
    
    private void initializeComponent() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(backgroundColor);
        setOpaque(true);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Tạo group label
        createGroupLabel();
        
        // Tạo container cho các menu items
        createItemsContainer();
        
        // Đảm bảo component có thể resize
        setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
    }
    
    private void createGroupLabel() {
        groupLabel = new JLabel(groupTitle);
        groupLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        groupLabel.setForeground(new Color(107, 114, 128));
        groupLabel.setBorder(new EmptyBorder(8, 20, 8, 20));
        groupLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        groupLabel.setOpaque(false);
        
        add(groupLabel);
    }
    
    private void createItemsContainer() {
        itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(backgroundColor);
        itemsContainer.setOpaque(true);
        itemsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        add(itemsContainer);
    }
    
    public void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
        menuItem.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemsContainer.add(menuItem);
        
        // Cập nhật layout
        revalidate();
        repaint();
        updateMaximumSize();
    }
    
    public void addMenuItem(String icon, String text, String action, MenuItem.MenuItemClickListener listener) {
        MenuItem menuItem = new MenuItem(icon, text, action, backgroundColor);
        menuItem.setClickListener(listener);
        addMenuItem(menuItem);
    }
    
    public void removeMenuItem(String action) {
        MenuItem toRemove = null;
        for (MenuItem item : menuItems) {
            if (action.equals(item.getAction())) {
                toRemove = item;
                break;
            }
        }
        
        if (toRemove != null) {
            menuItems.remove(toRemove);
            itemsContainer.remove(toRemove);
            revalidate();
            repaint();
            updateMaximumSize();
        }
    }
    
    public MenuItem getMenuItem(String action) {
        for (MenuItem item : menuItems) {
            if (action.equals(item.getAction())) {
                return item;
            }
        }
        return null;
    }
    
    public List<MenuItem> getAllMenuItems() {
        return new ArrayList<>(menuItems);
    }
    
    public String getGroupTitle() {
        return groupTitle;
    }
    
    public void setGroupTitle(String title) {
        this.groupTitle = title;
        groupLabel.setText(title);
        repaint();
    }
    
    public void addSpacing(int spacing) {
        Component spacer = Box.createVerticalStrut(spacing);
        add(spacer, 0); // Thêm vào đầu
        revalidate();
        repaint();
    }
    
    public void updateBackgroundColor(Color newBackgroundColor) {
        this.backgroundColor = newBackgroundColor;
        setBackground(newBackgroundColor);
        itemsContainer.setBackground(newBackgroundColor);
        
        // Cập nhật cho tất cả menu items
        for (MenuItem item : menuItems) {
            item.updateColors(newBackgroundColor, new Color(243, 244, 246), new Color(219, 234, 254));
        }
        
        repaint();
    }
    
    private void updateMaximumSize() {
        // Tính toán lại kích thước tối đa
        Dimension preferredSize = getPreferredSize();
        setMaximumSize(new Dimension(Integer.MAX_VALUE, preferredSize.height));
    }
    
    @Override
    public Dimension getPreferredSize() {
        // Tính toán kích thước preferred dựa trên các component con
        Dimension labelSize = groupLabel.getPreferredSize();
        Dimension containerSize = itemsContainer.getPreferredSize();
        
        int totalHeight = labelSize.height + containerSize.height;
        int maxWidth = Math.max(labelSize.width, containerSize.width);
        
        return new Dimension(maxWidth, totalHeight);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Có thể thêm custom painting ở đây nếu cần
    }
    
    // Utility methods
    
    public int getMenuItemCount() {
        return menuItems.size();
    }
    
    public boolean containsAction(String action) {
        return getMenuItem(action) != null;
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        groupLabel.setEnabled(enabled);
        for (MenuItem item : menuItems) {
            item.setEnabled(enabled);
        }
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateMaximumSize();
        }
    }
}