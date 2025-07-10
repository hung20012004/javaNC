/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.admin.KanbanComponent;

import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentHeader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Header component cho POKanban board quản lý đơn đặt hàng
 * @author Hi
 */
public class POKanBanHeader extends JPanel {
    
   private ContentHeader header;
    private JButton refreshButton;
    private JButton addButton;
    //private JButton updateButton;
    private JButton deleteButton;
    private JButton exportButton;
    
    public POKanBanHeader() {
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        header = new ContentHeader("POKanban - Quản lý đơn đặt hàng");
        
        refreshButton = header.getAddButton();
        refreshButton.setText("Làm mới");
        refreshButton.setIcon(createRefreshIcon());
        
        addButton = new JButton("Thêm đơn nhập hàng mới");
       // updateButton = new JButton("Cập nhật");
        deleteButton = new JButton("Xóa");
        exportButton = new JButton("Xuất Excel");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addButton);
        //buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(refreshButton);
        
        header.add(buttonPanel, BorderLayout.EAST);
        add(header, BorderLayout.CENTER);
        
        // Thêm separator
        JPanel separator = new JPanel();
        separator.setBackground(new Color(189, 195, 199));
        separator.setPreferredSize(new Dimension(0, 1));
        add(separator, BorderLayout.SOUTH);
    }
    
    private Icon createRefreshIcon() {
        return new ImageIcon(new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB) {
            {
                Graphics2D g2 = createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 204, 113));
                g2.setStroke(new BasicStroke(2));
                g2.drawArc(2, 2, 12, 12, 0, 270);
                g2.drawLine(10, 2, 14, 2);
                g2.drawLine(10, 2, 10, 6);
                g2.dispose();
            }
        });
    }
    
    public void addRefreshListener(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }
    
    public void addAddListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }
    
//    public void addUpdateListener(ActionListener listener) {
//        updateButton.addActionListener(listener);
//    }
    
    public void addDeleteListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }
    
    public void addExportListener(ActionListener listener) {
        exportButton.addActionListener(listener);
    }
    
    public JButton getRefreshButton() {
        return refreshButton;
    }
    
    public JButton getAddButton() {
        return addButton;
    }
    
//    public JButton getUpdateButton() {
//        return updateButton;
//    }
    
    public JButton getDeleteButton() {
        return deleteButton;
    }
    
    public JButton getExportButton() {
        return exportButton;
    }
}