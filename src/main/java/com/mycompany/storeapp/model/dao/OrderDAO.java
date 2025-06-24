/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.model.entity.Order;
import com.mycompany.storeapp.model.entity.OrderDetail;
import com.mycompany.storeapp.config.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Order management
 * @author Hi
 */
public class OrderDAO {
    private final DatabaseConnection connection;
    Connection conn;
    ProductVariantDAO variantDAO;

    public OrderDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
        this.variantDAO = new ProductVariantDAO(this.connection);
    }
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                // Load order details
                order.setDetails(getOrderDetailsByOrderId(order.getOrderId()));
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE order_status = ? ORDER BY order_date DESC";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setDetails(getOrderDetailsByOrderId(order.getOrderId()));
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting orders by status: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Update order status
     */
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET order_status = ?, updated_at = ? WHERE order_id = ?";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get order by ID
     */
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setDetails(getOrderDetailsByOrderId(orderId));
                return order;
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get order details by order ID
     */
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
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
    
    /**
     * Map ResultSet to Order object
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setShippingAddressId(rs.getInt("shipping_address_id"));
        
        // Handle nullable promotion_id
        int promotionId = rs.getInt("promotion_id");
        if (rs.wasNull()) {
            order.setPromotionId(null);
        } else {
            order.setPromotionId(promotionId);
        }
        
        // Convert Timestamp to LocalDateTime
        Timestamp timestamp = rs.getTimestamp("order_date");
        if (timestamp != null) {
            order.setOrderDate(timestamp.toLocalDateTime());
        }
        
        order.setSubtotal(rs.getDouble("subtotal"));
        order.setShippingFee(rs.getDouble("shipping_fee"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setNote(rs.getString("note"));
        
        return order;
    }
    
    /**
     * Get order count by status
     */
    public int getOrderCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_status = ?";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting order count by status: " + e.getMessage());
        }
        
        return 0;
    }
}
