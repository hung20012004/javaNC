package com.mycompany.storeapp.view.component.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ActionCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setOpaque(true);
        
        if (!isSelected) {
            if (row % 2 == 0) {
                panel.setBackground(Color.WHITE);
            } else {
                panel.setBackground(new Color(248, 249, 250));
            }
        } else {
            panel.setBackground(new Color(217, 237, 247));
        }
        
        JButton editBtn = new JButton("Sửa️");
        editBtn.setPreferredSize(new Dimension(50, 25));
        editBtn.setBackground(new Color(241, 196, 15));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        editBtn.setBorderPainted(false);
        editBtn.setFocusPainted(false);
        
        JButton deleteBtn = new JButton("Xóa");
        deleteBtn.setPreferredSize(new Dimension(50, 25));
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        
        panel.add(editBtn);
        panel.add(deleteBtn);
        
        return panel;
    }
}