
package com.mycompany.storeapp.view.component.shop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ShopNavbarComponent extends JPanel {
    private static final Color NAV_BACKGROUND = new Color(255, 255, 255);
    private static final Color NAV_ITEM_COLOR = new Color(107, 114, 128);
    private static final Color NAV_ITEM_ACTIVE_COLOR = new Color(59, 130, 246);
    private String activeItem = "pos";
    
    private ActionListener menuActionListener;
    
    public ShopNavbarComponent() {
        setBackground(NAV_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setupMenuItems();
    }
    
    private void setupMenuItems() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        
        String[] menuItems = {"pos", "orders"};
        String[] menuLabels = {"POS", "Đơn hàng"};
        String[] menuIcons = {"🛒", "📋"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createMenuButton(menuLabels[i], menuIcons[i], menuItems[i]);
            add(menuButton);
        }
    }
    
    private JButton createMenuButton(String label, String icon, String actionCommand) {
        JButton button = new JButton("<html>" + icon + " " + label + "</html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(activeItem.equals(actionCommand) ? NAV_ITEM_ACTIVE_COLOR : NAV_ITEM_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setActionCommand(actionCommand);
        button.addActionListener(e -> {
            if (menuActionListener != null) {
                menuActionListener.actionPerformed(e);
            }
            setActiveItem(actionCommand);
        });
        return button;
    }
    
    public void setMenuActionListener(ActionListener listener) {
        this.menuActionListener = listener;
    }
    
    public void setActiveItem(String actionCommand) {
        activeItem = actionCommand;
        for (Component component : getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setForeground(button.getActionCommand().equals(actionCommand) 
                    ? NAV_ITEM_ACTIVE_COLOR : NAV_ITEM_COLOR);
            }
        }
    }
}
