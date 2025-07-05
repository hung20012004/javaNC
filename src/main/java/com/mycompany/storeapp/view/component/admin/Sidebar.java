/*
 * Enhanced Sidebar Component - Menu sidebar của admin với scroll bar được cải thiện
 * @author Hi
 */
package com.mycompany.storeapp.view.component.admin;

import com.mycompany.storeapp.view.component.admin.SidebarComponent.*;
import com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sidebar extends JPanel {
    private JPanel menuPanel;
    private JScrollPane scrollPane;
    private JButton toggleButton;
    private boolean isCollapsed = false;
    private Color sidebarBg = new Color(248, 250, 252);
    private Color scrollThumbColor = new Color(203, 213, 225);
    private Color scrollTrackColor = new Color(241, 245, 249);
    
    // Maps để quản lý menu items và groups
    private Map<String, com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem> actionMenuItemMap = new HashMap<>();
    private Map<String, MenuGroup> groupMap = new HashMap<>();
    private com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem currentActiveMenuItem = null;
    
    // Interface để xử lý sự kiện menu
    public interface MenuActionListener {
        void onMenuItemClicked(String action);
    }
    
    private MenuActionListener menuActionListener;
    
    public Sidebar() {
        initializeComponent();
        createMenuFromData(MenuDataProvider.getDefaultAdminMenuData());
        setDefaultActiveItem();
    }
    
    public Sidebar(List<MenuDataProvider.MenuGroupData> menuData) {
        initializeComponent();
        createMenuFromData(menuData);
        setDefaultActiveItem();
    }
    
    private void initializeComponent() {
        setLayout(new BorderLayout());
        setBackground(sidebarBg);
        setPreferredSize(new Dimension(280, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));
        
        // Tạo panel chứa menu với padding
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(sidebarBg);
        
        // Thêm nút toggle
        toggleButton = new JButton("☰");
        toggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        toggleButton.setBackground(sidebarBg);
        toggleButton.setBorder(new EmptyBorder(10, 10, 10, 10));
        toggleButton.setFocusPainted(false);
        toggleButton.addActionListener(e -> toggleSidebar());
        wrapperPanel.add(toggleButton, BorderLayout.NORTH);
        
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(sidebarBg);
        menuPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        wrapperPanel.add(menuPanel, BorderLayout.NORTH);
        
        // Tạo scroll pane với cấu hình tối ưu
        setupScrollPane(wrapperPanel);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void setupScrollPane(JPanel wrapperPanel) {
        scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getVerticalScrollBar().setBlockIncrement(80);
        scrollPane.setWheelScrollingEnabled(true);
        
        customizeScrollBar();
    }
    
    private void customizeScrollBar() {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(12, 0));
        verticalScrollBar.setBackground(scrollTrackColor);
        verticalScrollBar.setUI(new CustomScrollBarUI(scrollThumbColor, scrollTrackColor));
    }
    
    private void createMenuFromData(List<MenuDataProvider.MenuGroupData> menuGroupDataList) {
        boolean isFirstGroup = true;
        
        for (MenuDataProvider.MenuGroupData groupData : menuGroupDataList) {
            MenuGroup menuGroup = new MenuGroup(groupData.title, sidebarBg);
            
            if (!isFirstGroup) {
                menuGroup.addSpacing(15);
            }
            
            for (MenuDataProvider.MenuItemData itemData : groupData.items) {
                MenuItem menuItem = new MenuItem(itemData.icon, itemData.text, itemData.action, sidebarBg);
                menuItem.setClickListener(this::handleMenuItemClick);
                
                menuGroup.addMenuItem(menuItem);
                actionMenuItemMap.put(itemData.action, menuItem);
            }
            
            groupMap.put(groupData.title, menuGroup);
            menuPanel.add(menuGroup);
            isFirstGroup = false;
        }
        
        SwingUtilities.invokeLater(this::refreshScrollPane);
    }
    
    private void handleMenuItemClick(String action) {
        // Deactivate current active item
        if (currentActiveMenuItem != null) {
            currentActiveMenuItem.setActive(false);
        }
        
        com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem clickedItem = actionMenuItemMap.get(action);
        if (clickedItem != null) {
            clickedItem.setActive(true);
            currentActiveMenuItem = clickedItem;
        }
        
        if (menuActionListener != null) {
            menuActionListener.onMenuItemClicked(action);
        }
    }
    
    private void setDefaultActiveItem() {
        SwingUtilities.invokeLater(() -> {
            com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem dashboardItem = actionMenuItemMap.get("dashboard");
            if (dashboardItem != null) {
                dashboardItem.setActive(true);
                currentActiveMenuItem = dashboardItem;
            }
        });
    }
    
    public void setMenuActionListener(MenuActionListener listener) {
        this.menuActionListener = listener;
    }
    
    public void setActiveMenuItem(String action) {
        if (currentActiveMenuItem != null) {
            currentActiveMenuItem.setActive(false);
        }
        
        com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem menuItem = actionMenuItemMap.get(action);
        if (menuItem != null) {
            menuItem.setActive(true);
            currentActiveMenuItem = menuItem;
            
            SwingUtilities.invokeLater(() -> scrollToMenuItem(menuItem));
        }
    }
    
    public String getCurrentActiveAction() {
        return currentActiveMenuItem != null ? currentActiveMenuItem.getActionString(): null;
    }
    
    public void refreshScrollPane() {
        menuPanel.invalidate();
        scrollPane.invalidate();
        menuPanel.revalidate();
        scrollPane.revalidate();
        menuPanel.repaint();
        scrollPane.repaint();
        
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().revalidate();
            scrollPane.getVerticalScrollBar().repaint();
        });
    }
    
    public void scrollToTop() {
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(0);
        });
    }
    
    public void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        });
    }
    
    public void scrollToMenuGroup(String groupTitle) {
        SwingUtilities.invokeLater(() -> {
            MenuGroup group = groupMap.get(groupTitle);
            if (group != null) {
                Rectangle bounds = group.getBounds();
                Point location = SwingUtilities.convertPoint(group.getParent(), bounds.getLocation(), menuPanel);
                Rectangle viewRect = new Rectangle(location.x, location.y - 20, bounds.width, bounds.height);
                menuPanel.scrollRectToVisible(viewRect);
            }
        });
    }
    
    private void scrollToMenuItem(com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem menuItem) {
        Rectangle bounds = menuItem.getBounds();
        Point location = SwingUtilities.convertPoint(menuItem.getParent(), bounds.getLocation(), menuPanel);
        Rectangle viewRect = new Rectangle(location.x, location.y, bounds.width, bounds.height);
        menuPanel.scrollRectToVisible(viewRect);
    }
    
    public boolean isScrollBarVisible() {
        return scrollPane.getVerticalScrollBar().isVisible();
    }
    
    public int getTotalMenuHeight() {
        return menuPanel.getPreferredSize().height;
    }
    
    public MenuItem getMenuItem(String action) {
        return actionMenuItemMap.get(action);
    }
    
    public MenuGroup getMenuGroup(String groupTitle) {
        return groupMap.get(groupTitle);
    }
    
    public void setCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;
        updateSidebarState();
    }
    
    public boolean isCollapsed() {
        return isCollapsed;
    }
    
    private void toggleSidebar() {
        isCollapsed = !isCollapsed;
        updateSidebarState();
    }
    
    private void updateSidebarState() {
        if (isCollapsed) {
            setPreferredSize(new Dimension(60, 0));
            toggleButton.setText("→");
            menuPanel.setVisible(false);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        } else {
            setPreferredSize(new Dimension(280, 0));
            toggleButton.setText("☰");
            menuPanel.setVisible(true);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        revalidate();
        repaint();
    }
    // Theme customization methods
    
    public void updateTheme(Color backgroundColor, Color thumbColor, Color trackColor) {
        this.sidebarBg = backgroundColor;
        this.scrollThumbColor = thumbColor;
        this.scrollTrackColor = trackColor;
        
        // Update components
        setBackground(backgroundColor);
        menuPanel.setBackground(backgroundColor);
        
        // Update scroll bar
        customizeScrollBar();
        
        // Update all menu items and groups
        for (MenuGroup group : groupMap.values()) {
            group.setBackground(backgroundColor);
            for (com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem item : group.getAllMenuItems()) {
                item.updateColors(backgroundColor, new Color(243, 244, 246), new Color(219, 234, 254));
            }
        }
        
        refreshScrollPane();
    }
    
    // Dynamic menu management
    
    public void addMenuItem(String groupTitle, String icon, String text, String action) {
        MenuGroup group = groupMap.get(groupTitle);
        if (group != null) {
            com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem newItem = new com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem(icon, text, action, sidebarBg);
            newItem.setClickListener(this::handleMenuItemClick);
            group.addMenuItem(newItem);
            actionMenuItemMap.put(action, newItem);
            refreshScrollPane();
        }
    }
    
    public void removeMenuItem(String action) {
        com.mycompany.storeapp.view.component.admin.SidebarComponent.MenuItem item = actionMenuItemMap.get(action);
        if (item != null) {
            // Find and remove from group
            for (MenuGroup group : groupMap.values()) {
                group.getAllMenuItems().removeIf(menuItem -> action.equals(menuItem.getAction()));
            }
            actionMenuItemMap.remove(action);
            refreshScrollPane();
        }
    }
}