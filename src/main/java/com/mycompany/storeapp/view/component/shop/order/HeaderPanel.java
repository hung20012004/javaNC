package com.mycompany.storeapp.view.component.shop.order;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderPanel extends JPanel {
    private static final Color HEADER_COLOR = new Color(249, 250, 251);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);
    private static final Color SECONDARY_TEXT_COLOR = new Color(107, 114, 128);
    
    private String title;
    private String subtitle;
    private String icon;
    
    public HeaderPanel(String title, String subtitle, String icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        
        initializeComponents();
    }
    
    public HeaderPanel(String title, String subtitle) {
        this(title, subtitle, "");
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(HEADER_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(SECONDARY_TEXT_COLOR);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        add(titlePanel, BorderLayout.WEST);
    }
    
    public void setTitle(String title) {
        this.title = title;
        refreshTitle();
    }
    
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        refreshTitle();
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
        refreshTitle();
    }
    
    private void refreshTitle() {
        removeAll();
        initializeComponents();
        revalidate();
        repaint();
    }
}