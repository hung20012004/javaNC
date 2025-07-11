package com.mycompany.storeapp.view.component.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component component = super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            if (row % 2 == 0) {
                component.setBackground(Color.WHITE);
            } else {
                component.setBackground(new Color(248, 249, 250));
            }
        }
        
        if (column == 0 || column == table.getColumnCount() - 1) {
            ((DefaultTableCellRenderer) component).setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            ((DefaultTableCellRenderer) component).setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        return component;
    }
}