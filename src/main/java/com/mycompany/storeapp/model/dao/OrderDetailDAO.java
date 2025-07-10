/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.OrderDetail;
import com.mycompany.storeapp.model.entity.ProductVariant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for OrderDetail management
 * @author Hi
 */
public class OrderDetailDAO {
    private final DatabaseConnection connection;
    private final Connection conn;
    private final ProductVariantDAO variantDAO;

    public OrderDetailDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
        this.variantDAO = new ProductVariantDAO(connection);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderId(rs.getInt("order_id"));
                detail.setVariantId(rs.getInt("variant_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setUnitPrice(rs.getDouble("unit_price"));
                detail.setSubtotal(rs.getDouble("subtotal"));
                detail.setVariant(variantDAO.getVariantById(rs.getInt("variant_id")));
                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order details: " + e.getMessage());
        }

        return details;
    }
}