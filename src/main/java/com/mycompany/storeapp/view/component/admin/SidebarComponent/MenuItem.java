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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuItem extends JButton {
    private String icon;
    private String text;
    private String action;
    private Color backgroundColor;
    private Color hoverColor;
    private Color selectedColor;
    private JPanel contentPanel;
    private JLabel iconLabel;
    private JLabel textLabel;
    private boolean isActive = false;
    private MenuItemClickListener clickListener;
    
    // Interface cho việc xử lý click
    public interface MenuItemClickListener {
        void onMenuItemClicked(String action);
    }
    
    public MenuItem(String icon, String text, String action, Color backgroundColor) {
        this.icon = icon;
        this.text = text;
        this.action = action;
        this.backgroundColor = backgroundColor;
        this.hoverColor = new Color(243, 244, 246);
        this.selectedColor = new Color(219, 234, 254);
        
        initializeComponent();
        setupEventHandlers();
    }
    
    private void initializeComponent() {
        // Cấu hình button
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setBackground(backgroundColor);
        setForeground(new Color(55, 65, 81));
        setFont(new Font("SansSerif", Font.PLAIN, 14));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Đảm bảo button có kích thước đồng nhất
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        setPreferredSize(new Dimension(260, 44));
        setMinimumSize(new Dimension(260, 44));
        
        // Tạo content panel
        createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contentPanel.setBackground(backgroundColor);
        contentPanel.setOpaque(true);
        
        // Icon label
        iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 12));
        iconLabel.setPreferredSize(new Dimension(28, 20));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Text label
        textLabel = new JLabel(text);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textLabel.setForeground(new Color(55, 65, 81));
        
        contentPanel.add(iconLabel);
        contentPanel.add(textLabel);
    }
    
    private void setupEventHandlers() {
        // Hover effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    animateColor(hoverColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    animateColor(backgroundColor);
                }
            }
        });
        
        // Click handler
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clickListener != null) {
                    clickListener.onMenuItemClicked(action);
                }
            }
        });
    }
    
    private void animateColor(Color targetColor) {
        setBackground(targetColor);
        contentPanel.setBackground(targetColor);
        repaint();
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
        if (active) {
            animateColor(selectedColor);
        } else {
            animateColor(backgroundColor);
        }
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setClickListener(MenuItemClickListener listener) {
        this.clickListener = listener;
    }
    
    // Getters
    public String getMenuIcon() {
        return icon;
    }
    
    public String getMenuText() {
        return text;
    }
    
    public String getActionString() {
        return action;
    }
    
    // Setters với cập nhật UI
    public void updateIcon(String newIcon) {
        this.icon = newIcon;
        iconLabel.setText(newIcon);
        repaint();
    }
    
    public void updateText(String newText) {
        this.text = newText;
        textLabel.setText(newText);
        repaint();
    }
    
    public void updateColors(Color bg, Color hover, Color selected) {
        this.backgroundColor = bg;
        this.hoverColor = hover;
        this.selectedColor = selected;
        
        contentPanel.setBackground(bg);
        if (!isActive) {
            animateColor(backgroundColor);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Có thể thêm custom painting ở đây nếu cần
    }
}
