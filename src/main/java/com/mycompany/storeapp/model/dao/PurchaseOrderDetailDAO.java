/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.model.entity.PurchaseOrderDetail;
import com.mycompany.storeapp.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Purchase Order Detail management
 */
public class PurchaseOrderDetailDAO {
    private final DatabaseConnection connection;
    private Connection conn;

    public PurchaseOrderDetailDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
    }

    public boolean createPurchaseOrderDetail(int poId, PurchaseOrderDetail detail) {
        String sql = "INSERT INTO purchase_order_details (po_id, product_id, quantity, unit_price, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            stmt.setInt(2, detail.getProductId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setDouble(4, detail.getUnitPrice());
            stmt.setDouble(5, detail.getSubTotal());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating purchase order detail: " + e.getMessage());
            return false;
        }
    }

    public List<PurchaseOrderDetail> getDetailsByPoId(int poId) {
        List<PurchaseOrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM purchase_order_details WHERE po_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrderDetail detail = new PurchaseOrderDetail(
                        poId,
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("subtotal"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting purchase order details: " + e.getMessage());
        }
        return details;
    }

    public boolean deleteDetailsByPoId(int poId) {
        String sql = "DELETE FROM purchase_order_details WHERE po_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting purchase order details: " + e.getMessage());
            return false;
        }
    }
}