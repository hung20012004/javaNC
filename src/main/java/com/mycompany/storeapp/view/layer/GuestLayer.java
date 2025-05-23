/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.layer;

/**
 *
 * @author Manh Hung
 */
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.JPanel;

public class GuestLayer extends JFrame {
    private JPanel currentContent;

    public GuestLayer() {
        setTitle("Store App - Guest Layout");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Toàn màn hình
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLayout(null); // Layout tùy chỉnh
        setBackground(Color.LIGHT_GRAY); // Nền cho GuestLayer
    }

    public void setContent(JPanel content) {
        if (currentContent != null) {
            remove(currentContent);
        }
        currentContent = content;
        // Đặt kích thước cố định và căn giữa
        currentContent.setBounds((getWidth() - 400) / 2, (getHeight() - 300) / 2, 400, 300);
        currentContent.setBackground(Color.WHITE); // Nền trắng để giống dialog
        currentContent.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK)); // Viền để giống dialog
        add(currentContent);
        revalidate();
        repaint();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // Căn giữa lại nội dung khi JFrame thay đổi kích thước
        if (currentContent != null) {
            currentContent.setBounds((width - 400) / 2, (height - 300) / 2, 400, 300);
        }
    }
}
