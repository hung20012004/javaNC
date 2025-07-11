package com.mycompany.storeapp.view.component.admin;

import javax.swing.*;
import com.mycompany.storeapp.view.component.CustomTable;
import java.awt.*;
import java.awt.event.ActionListener;

public class ActionCellEditor extends DefaultCellEditor {
    private JPanel panel;
    private JButton editBtn;
    private JButton deleteBtn;
    private int currentRow;
    private final CustomTable customTable;

    public ActionCellEditor(CustomTable customTable) {
        super(new JTextField());
        this.customTable = customTable;
        
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        editBtn = new JButton("Sửa");
        editBtn.setPreferredSize(new Dimension(50, 25));
        editBtn.setBackground(new Color(241, 196, 15));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        editBtn.setBorderPainted(false);
        editBtn.setFocusPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        deleteBtn = new JButton("Xóa");
        deleteBtn.setPreferredSize(new Dimension(50, 25));
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        editBtn.addActionListener(e -> {
            ActionListener listener = customTable.getEditActionListener();
            if (listener != null) {
                listener.actionPerformed(
                    new java.awt.event.ActionEvent(customTable, currentRow, "edit"));
            }
            fireEditingStopped();
        });
        
        deleteBtn.addActionListener(e -> {
            ActionListener listener = customTable.getDeleteActionListener();
            if (listener != null) {
                listener.actionPerformed(
                    new java.awt.event.ActionEvent(customTable, currentRow, "delete"));
            }
            fireEditingStopped();
        });
        
        panel.add(editBtn);
        panel.add(deleteBtn);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentRow = row;
        return panel;
    }
    
    @Override
    public Object getCellEditorValue() {
        return "actions";
    }
}