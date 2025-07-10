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
 */
public class OrderDAO {
    private final DatabaseConnection connection;
    ProductVariantDAO variantDAO;
    ShippingAddressDAO shippingAddressDAO;

    public OrderDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.variantDAO = new ProductVariantDAO(this.connection);
        this.shippingAddressDAO = new ShippingAddressDAO(this.connection);
    }
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (Connection conn = connection.getConnection();
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
    
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE order_status = ? ORDER BY order_date DESC";
        
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
            for (Order order : orders) {
                loadShippingAddress(order); // Bây giờ an toàn hơn
                order.setDetails(getOrderDetailsByOrderId(order.getOrderId()));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting orders by status: " + e.getMessage());
        }
        
        return orders;
    }
    
    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE orders SET order_status = ?, updated_at = ? WHERE order_id = ?";
        
        try (Connection conn = connection.getConnection();
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
    

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        
        try (Connection conn = connection.getConnection();
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
    
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM order_details WHERE order_id = ?";
        
        try (Connection conn = connection.getConnection();
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
    
    public int getOrderCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_status = ?";
        
        try (Connection conn = connection.getConnection();
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
    
    public List<Order> getOrdersByMonth(int year, int month) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE YEAR(order_date) = ? AND MONTH(order_date) = ? ORDER BY order_date DESC";

        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                loadShippingAddress(order);
                order.setDetails(getOrderDetailsByOrderId(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders by month: " + e.getMessage());
        }

        return orders;
    }

    public int saveOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, shipping_address_id, promotion_id, order_date, subtotal, shipping_fee, discount_amount, total_amount, payment_method, payment_status, order_status, note, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
        try(Connection conn = connection.getConnection(); 
            PreparedStatement stmt = conn.prepareStatement(sql)) {
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
    public boolean saveOrderWithDetails(Order order, List<OrderDetail> details) {
    String orderSql = "INSERT INTO orders (user_id, shipping_address_id, promotion_id, order_date, subtotal, shipping_fee, discount_amount, total_amount, payment_method, payment_status, order_status, note, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String detailSql = "INSERT INTO order_details (order_id, variant_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
    
    try  (Connection conn = connection.getConnection(); ){
        conn.setAutoCommit(false); // Bắt đầu giao dịch
        try (PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
            // Lưu order
            orderStmt.setInt(1, order.getUserId());
            orderStmt.setInt(2, order.getShippingAddressId());
            orderStmt.setObject(3, order.getPromotionId());
            orderStmt.setTimestamp(4, Timestamp.valueOf(order.getOrderDate()));
            orderStmt.setDouble(5, order.getSubtotal());
            orderStmt.setDouble(6, order.getShippingFee());
            orderStmt.setDouble(7, order.getDiscountAmount());
            orderStmt.setDouble(8, order.getTotalAmount());
            orderStmt.setString(9, order.getPaymentMethod());
            orderStmt.setString(10, order.getPaymentStatus());
            orderStmt.setString(11, order.getOrderStatus());
            orderStmt.setString(12, order.getNote());
            orderStmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            orderStmt.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = orderStmt.executeUpdate();
            if (rowsAffected == 0) {
                conn.rollback();
                return false;
            }
            
            // Lấy order_id
            try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    conn.rollback();
                    return false;
                }
                int orderId = generatedKeys.getInt(1);
                order.setOrderId(orderId);
            }
            
            // Lưu order details
            try (PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {
                for (OrderDetail detail : details) {
                    detailStmt.setInt(1, order.getOrderId());
                    detailStmt.setInt(2, detail.getVariantId());
                    detailStmt.setInt(3, detail.getQuantity());
                    detailStmt.setDouble(4, detail.getUnitPrice());
                    detailStmt.setDouble(5, detail.getSubtotal());
                    detailStmt.addBatch();
                }
                int[] results = detailStmt.executeBatch();
                for (int result : results) {
                    if (result <= 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Error saving order with details: " + e.getMessage());
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Error connecting to database: " + e.getMessage());
        return false;
    }
}
}
