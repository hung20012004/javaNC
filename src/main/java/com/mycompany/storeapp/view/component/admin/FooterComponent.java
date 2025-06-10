/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.admin;

/**
 * Footer Component - Thanh footer của admin
 * @author Hi
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FooterComponent extends JPanel {
    
    public FooterComponent() {
        initializeComponent();
    }
    
    private void initializeComponent() {
        setLayout(new BorderLayout());
        setBackground(new Color(249, 250, 251));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));
        setPreferredSize(new Dimension(0, 40));
        
        // Footer content bên trái
        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);
        
        // Footer content bên phải (nếu cần)
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(249, 250, 251));
        leftPanel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JLabel copyrightLabel = new JLabel("© 2025 - Phần mềm quản lý bán hàng");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(new Color(107, 114, 128));
        
        leftPanel.add(copyrightLabel);
        return leftPanel;
    }
    
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(249, 250, 251));
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 20));
        
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(107, 114, 128));
        
        rightPanel.add(versionLabel);
        return rightPanel;
    }
    
    // Phương thức để cập nhật thông tin copyright
    public void setCopyrightText(String text) {
        Component[] leftComponents = ((JPanel) getComponent(0)).getComponents();
        for (Component comp : leftComponents) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText(text);
                break;
            }
        }
    }
    
    // Phương thức để cập nhật version
    public void setVersionText(String version) {
        Component[] rightComponents = ((JPanel) getComponent(1)).getComponents();
        for (Component comp : rightComponents) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText(version);
                break;
            }
        }
    }
}
