/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.model.entity.Order;
import com.mycompany.storeapp.model.entity.ShippingAddress;
import com.mycompany.storeapp.model.entity.OrderDetail;
import com.mycompany.storeapp.config.DatabaseConnection;
import java.sql.*;
import java.sql.Timestamp;
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
    ShippingAddressDAO shippingAddressDAO;

    public OrderDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
        this.variantDAO = new ProductVariantDAO(this.connection);
        this.shippingAddressDAO = new ShippingAddressDAO(this.connection);
    }
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                loadShippingAddress(order);
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
                loadShippingAddress(order);
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
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, orderId);
            
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
                loadShippingAddress(order);
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
    
    private void loadShippingAddress(Order order) {
        if (order.getShippingAddressId() > 0) {
            ShippingAddress shippingAddress = shippingAddressDAO.getShippingAddressById(order.getShippingAddressId());
            order.setShippingAddress(shippingAddress);
        }
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
/**
     * Save a new order
     */
    public int saveOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, shipping_address_id, promotion_id, order_date, subtotal, shipping_fee, discount_amount, total_amount, payment_method, payment_status, order_status, note, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUserId());
            stmt.setInt(2, order.getShippingAddressId());
            stmt.setObject(3, order.getPromotionId()); // Nullable
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(order.getOrderDate()));
            stmt.setDouble(5, order.getSubtotal());
            stmt.setDouble(6, order.getShippingFee());
            stmt.setDouble(7, order.getDiscountAmount());
            stmt.setDouble(8, order.getTotalAmount());
            stmt.setString(9, order.getPaymentMethod());
            stmt.setString(10, order.getPaymentStatus());
            stmt.setString(11, order.getOrderStatus());
            stmt.setString(12, order.getNote());
            stmt.setTimestamp(13, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(14, new java.sql.Timestamp(System.currentTimeMillis()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Save an order detail
     */
    public boolean saveOrderDetail(OrderDetail detail) {
        String sql = "INSERT INTO order_details (order_id, variant_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, detail.getOrderId());
            stmt.setInt(2, detail.getVariantId());
            stmt.setInt(3, detail.getQuantity());
            stmt.setDouble(4, detail.getUnitPrice());
            stmt.setDouble(5, detail.getSubtotal());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving order detail: " + e.getMessage());
            return false;
        }
    }
}
