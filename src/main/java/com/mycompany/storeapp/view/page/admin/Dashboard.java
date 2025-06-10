/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin;

/**
 *
 * @author Hi
 */
import com.mycompany.storeapp.view.component.CustomTable;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.border.TitledBorder;

public class Dashboard extends JFrame {
    private JPanel contentPanel;

    public Dashboard() {
        setTitle("IT Shop - Quản lý cửa hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(10, 1, 0, 5));
        sidebar.setBackground(new Color(65, 105, 225));
        sidebar.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {
            "Trang chủ", "Hàng hóa", "Giao dịch", "Thống kê", "Nhân viên",
            "Khách hàng", "Cá nhân", "Khuyến mãi", "Đăng xuất"
        };
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(65, 105, 225));
            btn.setForeground(Color.WHITE);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            sidebar.add(btn);
        }

        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 50));
        JLabel logo = new JLabel("🛒 IT Shop APP");
        logo.setFont(new Font("Arial", Font.BOLD, 16));
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel user = new JLabel("Nguyễn Văn Đức (Quản lý)", SwingConstants.RIGHT);
        user.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        header.add(logo, BorderLayout.WEST);
        header.add(user, BorderLayout.EAST);

        // Content chính
        contentPanel = new JPanel(new BorderLayout());

        String[] columns = {"Tên", "Giới tính", "SĐT", "Số hàng đã mua"};
        List<String[]> data = List.of(
            new String[]{"Nguyễn Văn A", "Nam", "0123456789", "10"},
            new String[]{"Trần Thị B", "Nữ", "0987654321", "3"}
        );

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.add(createStatCard("Tổng khách hàng", "18", Color.MAGENTA));
        statsPanel.add(createStatCard("Doanh thu / Ngày", "2,900,000", Color.BLUE));
        statsPanel.add(createStatCard("Sản phẩm / Ngày", "8", Color.ORANGE));
        statsPanel.add(createStatCard("Tồn kho", "706", Color.GREEN));

        // Section: Danh sách khách hàng (Table)
//        String[] cols = {"Tên", "Giới tính", "SĐT", "Số hàng đã mua"};
//        String[][] data = {
//            {"Bùi Thị Thu Trang", "Nữ", "0976805783", "5"},
//            {"Chu Anh Quân", "Nam", "0384864472", "2"},
//            {"Đào Thị Tú Uyên", "Nữ", "0372870765", "0"},
//            {"Lai Duy Phước", "Nam", "0348999483", "1040"},
//            {"Nguyễn Văn Xuân", "Nam", "0374887408", "24"}
//        };
//        JTable table = new JTable(data, cols);
//        JScrollPane scrollPane = new JScrollPane(table);

//        CustomTable panel = new CustomTable(columns, data);

        JPanel tablePanel = new JPanel(new BorderLayout());
        TitledBorder listBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.ORANGE, 3),
                "Danh sách khách hàng",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.DARK_GRAY
        );
        tablePanel.setBorder(listBorder);
//        tablePanel.add(panel, BorderLayout.CENTER);

        // Lắp ráp content panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Add all main sections
        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        lblValue.setForeground(Color.WHITE);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        new Dashboard().setVisible(true);
    }
}