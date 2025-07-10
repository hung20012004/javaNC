/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Report;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.controller.admin.OrderController;
import com.mycompany.storeapp.controller.admin.ReportController;
import com.mycompany.storeapp.model.entity.Product;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

/**
 * Panel hiển thị báo cáo thống kê bán hàng từ cơ sở dữ liệu
 */
public class ReportPanel extends JPanel {
    private final OrderController orderController;
    private final ReportController reportController;

    public ReportPanel() {
        DatabaseConnection dbConnection = new DatabaseConnection(); // Khởi tạo DatabaseConnection
        this.orderController = new OrderController(dbConnection);   // Truyền DatabaseConnection
        this.reportController = new ReportController(dbConnection); // Truyền DatabaseConnection
        initComponents();
        setupLayout();
        loadStatistics();
    }

    private void initComponents() {
        // Không cần khởi tạo riêng vì sẽ dùng trong setupLayout
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 248, 255));

        // Tổng quan
        JPanel overviewPanel = createOverviewPanel();
        add(overviewPanel, BorderLayout.NORTH);

        // Tabbed Pane cho biểu đồ và bảng
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Biểu Đồ Thống Kê", createChartsPanel());
        tabbedPane.addTab("Danh Sách Sản Phẩm", createTopProductsTable());
        add(tabbedPane, BorderLayout.CENTER);

        // Nút đóng
        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(231, 76, 60));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose(); // Đóng toàn bộ cửa sổ
            } else {
                Container parent = getParent();
                if (parent instanceof JPanel) {
                    parent.remove(this);
                    parent.revalidate();
                    parent.repaint();
                }
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBackground(new Color(240, 248, 255));

        ReportController.SalesReportData report = reportController.generateSalesReport(2025, 7);
        int totalQuantity = report.getTotalQuantity();
        double totalRevenue = report.getTotalRevenue();
        String topProductName = report.getTopProducts().isEmpty() ? "N/A" : report.getTopProducts().keySet().iterator().next().getName();

        JLabel totalSalesLabel = new JLabel("Tổng Số Lượng Đã Bán: " + totalQuantity + " sản phẩm");
        totalSalesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalSalesLabel.setForeground(new Color(46, 204, 113));
        panel.add(wrapInPanel(totalSalesLabel, new Color(144, 238, 144)));

        JLabel revenueLabel = new JLabel("Doanh Thu: " + String.format("%,.0f VNĐ", totalRevenue));
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        revenueLabel.setForeground(new Color(52, 152, 219));
        panel.add(wrapInPanel(revenueLabel, new Color(173, 216, 230)));

        JLabel topProductLabel = new JLabel("Sản Phẩm Bán Chạy Nhất: " + topProductName);
        topProductLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topProductLabel.setForeground(new Color(231, 76, 60));
        panel.add(wrapInPanel(topProductLabel, new Color(250, 128, 114)));

        return panel;
    }

    private JPanel wrapInPanel(JLabel label, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBackground(new Color(240, 248, 255));

        JFreeChart barChart = createBarChart();
        ChartPanel barChartPanel = new ChartPanel(barChart);
        barChartPanel.setPreferredSize(new Dimension(900, 300));
        panel.add(barChartPanel);

        JFreeChart pieChart = createPieChart();
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setPreferredSize(new Dimension(900, 300));
        panel.add(pieChartPanel);

        return panel;
    }

    private JFreeChart createBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        ReportController.SalesReportData report = reportController.generateSalesReport(2025, 7);
        for (Map.Entry<String, Integer> entry : report.getMonthlySales().entrySet()) {
            dataset.addValue(entry.getValue(), "Số Lượng", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Số Lượng Bán Theo Tháng",
                "Tháng",
                "Số Lượng",
                dataset
        );
        chart.setBackgroundPaint(new Color(240, 248, 255));
        return chart;
    }

    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        ReportController.SalesReportData report = reportController.generateSalesReport(2025, 7);
        for (Map.Entry<Product, Integer> entry : report.getTopProducts().entrySet()) {
            dataset.setValue(entry.getKey().getName(), entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Phân Bổ Sản Phẩm Bán Chạy",
                dataset,
                true, true, false
        );
        chart.setBackgroundPaint(new Color(240, 248, 255));
        return chart;
    }

    private JPanel createTopProductsTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        String[] columnNames = {"Tên Sản Phẩm", "Số Lượng", "Doanh Thu"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setBackground(new Color(255, 255, 255));
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        ReportController.SalesReportData report = reportController.generateSalesReport(2025, 7);
        for (Map.Entry<Product, Integer> entry : report.getTopProducts().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double revenue = quantity * (product.getSalePrice() > 0 ? product.getSalePrice() : product.getPrice());
            tableModel.addRow(new Object[]{product.getName(), quantity, String.format("%,.0f VNĐ", revenue)});
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadStatistics() {
        // Logic có thể được gọi từ đây nếu cần cập nhật động
    }
}