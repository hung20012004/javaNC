package com.mycompany.storeapp.view.component.shop.order;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class FooterPanel extends JPanel {
    private static final Color HEADER_COLOR = new Color(249, 250, 251);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    
    private List<JButton> buttons;
    
    public FooterPanel() {
        this.buttons = new ArrayList<>();
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBackground(HEADER_COLOR);
        setBorder(new EmptyBorder(15, 20, 15, 20));
    }
    
    public JButton addButton(String text, String icon, Color backgroundColor, ActionListener actionListener) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(actionListener);
        
        buttons.add(button);
        
        if (buttons.size() > 1) {
            add(Box.createHorizontalStrut(10));
        }
        add(button);
        
        return button;
    }
    
    public JButton addCancelButton(String text, ActionListener actionListener) {
        return addButton(text, "❌", DANGER_COLOR, actionListener);
    }
    
    public JButton addSubmitButton(String text, ActionListener actionListener) {
        JButton button = addButton(text, "✅", SUCCESS_COLOR, actionListener);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }
    
    public JButton addPrimaryButton(String text, ActionListener actionListener) {
        return addButton(text, "", PRIMARY_COLOR, actionListener);
    }
    
    public void removeAllButtons() {
        buttons.forEach(this::remove);
        buttons.clear();
        removeAll();
        revalidate();
        repaint();
    }
    
    public List<JButton> getButtons() {
        return new ArrayList<>(buttons);
    }
}