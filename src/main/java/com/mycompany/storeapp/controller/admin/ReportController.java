/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Product;
import java.util.HashMap;
import java.util.Map;

public class ReportController {
    private final OrderController orderController;
    private final DatabaseConnection dbConnection;

    public ReportController() {
        this.dbConnection = new DatabaseConnection();
        this.orderController = new OrderController(dbConnection);
    }

    public ReportController(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.orderController = new OrderController(dbConnection);
    }

    public SalesReportData generateSalesReport(int year, int month) {
        SalesReportData report = new SalesReportData();
        report.setTotalQuantity(orderController.getTotalQuantitySold(year, month));
        report.setTotalRevenue(orderController.getTotalRevenue(year, month));
        report.setMonthlySales(orderController.getMonthlySales(year, month));
        report.setTopProducts(orderController.getTopSellingProducts(5, year, month));
        return report;
    }

    public static class SalesReportData {
        private int totalQuantity;
        private double totalRevenue;
        private Map<String, Integer> monthlySales;
        private Map<Product, Integer> topProducts;

        public int getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public Map<String, Integer> getMonthlySales() { return monthlySales; }
        public void setMonthlySales(Map<String, Integer> monthlySales) { this.monthlySales = monthlySales; }
        public Map<Product, Integer> getTopProducts() { return topProducts; }
        public void setTopProducts(Map<Product, Integer> topProducts) { this.topProducts = topProducts; }
    }
}
