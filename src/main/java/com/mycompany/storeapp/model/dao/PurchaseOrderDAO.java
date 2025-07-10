/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.model.entity.PurchaseOrder;
import com.mycompany.storeapp.model.entity.PurchaseOrderDetail;
import com.mycompany.storeapp.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Purchase Order management
 */
public class PurchaseOrderDAO {
    private final DatabaseConnection connection;
    private Connection conn;
    private PurchaseOrderDetailDAO detailDAO;

    public PurchaseOrderDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
        this.detailDAO = new PurchaseOrderDetailDAO(connection); // Khởi tạo detailDAO
    }
    
    public boolean createPurchaseOrder(PurchaseOrder po, List<PurchaseOrderDetail> details) {
    String sql = "INSERT INTO purchase_orders (supplier_id, create_by_user_id, order_date, expected_date, " +
                 "total_amount, status, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    try {
        conn.setAutoCommit(false); // Bắt đầu transaction
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, po.getSupplierId());
            stmt.setInt(2, po.getCreatedByUserId());
            stmt.setTimestamp(3, po.getOrderDate() != null ? new Timestamp(po.getOrderDate().getTime()) : new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(4, po.getExpectedDate() != null ? new Timestamp(po.getExpectedDate().getTime()) : new Timestamp(System.currentTimeMillis()));
            stmt.setDouble(5, po.getTotalAmount());
            stmt.setString(6, po.getStatus());
            stmt.setString(7, po.getNote());
           
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Lấy po_id vừa được sinh ra
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int poId = generatedKeys.getInt(1);
                        po.setPoId(poId); // Cập nhật po_id vào đối tượng po
                        // Thêm chi tiết đơn hàng
                        if (details != null && !details.isEmpty()) {
                            for (PurchaseOrderDetail detail : details) {
                                detail.setPoId(poId); // Gán po_id cho mỗi detail
                                if (!detailDAO.createPurchaseOrderDetail(poId, detail)) {
                                    throw new SQLException("Failed to insert purchase order detail for po_id: " + poId);
                                }
                            }
                        }
                    } else {
                        throw new SQLException("Failed to get generated po_id.");
                    }
                }
            } else {
                throw new SQLException("Failed to insert purchase order.");
            }

            conn.commit();
            return true;
        }
    } catch (SQLException e) {
        try {
            conn.rollback();
        } catch (SQLException rollbackEx) {
            System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
        }
        System.err.println("Error creating purchase order: " + e.getMessage());
        return false;
    } finally {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("Error resetting auto-commit: " + e.getMessage());
        }
    }
}

        public boolean updatePurchaseOrder(PurchaseOrder po) {
        String sql = "UPDATE purchase_orders SET supplier_id = ?, create_by_user_id = ?, order_date = ?, " +
                     "expected_date = ?, total_amount = ?, status = ?, note = ? WHERE po_id = ?";

        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Cập nhật thông tin đơn nhập hàng
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, po.getSupplierId());
                stmt.setInt(2, po.getCreatedByUserId());
                stmt.setTimestamp(3, new Timestamp(po.getOrderDate().getTime()));
                stmt.setTimestamp(4, new Timestamp(po.getExpectedDate().getTime()));
                stmt.setDouble(5, po.getTotalAmount());
                stmt.setString(6, po.getStatus());
                stmt.setString(7, po.getNote());
                stmt.setInt(8, po.getPoId());
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Failed to update purchase order with po_id: " + po.getPoId());
                }
            }

            
            detailDAO.deleteDetailsByPoId(po.getPoId());

            List<PurchaseOrderDetail> details = po.getDetails();
            if (details != null && !details.isEmpty()) {
                for (PurchaseOrderDetail detail : details) {
                    detail.setPoId(po.getPoId());
                    if (!detailDAO.createPurchaseOrderDetail(po.getPoId(), detail)) {
                        throw new SQLException("Failed to insert purchase order detail for po_id: " + po.getPoId());
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error updating purchase order: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public boolean deletePurchaseOrder(int poId) {
        String sql = "DELETE FROM purchase_orders WHERE po_id = ?";
        
        try {
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            // Xóa chi tiết trước
            detailDAO.deleteDetailsByPoId(poId);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, poId);
                int rowsAffected = stmt.executeUpdate();
                conn.commit();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error deleting purchase order: " + e.getMessage());
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public PurchaseOrder getPurchaseOrderById(int poId) {
        String sql = "SELECT * FROM purchase_orders WHERE po_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PurchaseOrder po = new PurchaseOrder(
                        rs.getInt("po_id"),
                        rs.getInt("supplier_id"),
                        rs.getInt("create_by_user_id"),
                        rs.getTimestamp("order_date"),
                        rs.getTimestamp("expected_date"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    po.setDetails(detailDAO.getDetailsByPoId(poId));
                    return po;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting purchase order: " + e.getMessage());
        }
        return null;
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        List<PurchaseOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM purchase_orders ORDER BY order_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PurchaseOrder po = new PurchaseOrder(
                    rs.getInt("po_id"),
                    rs.getInt("supplier_id"),
                    rs.getInt("create_by_user_id"),
                    rs.getTimestamp("order_date"),
                    rs.getTimestamp("expected_date"),
                    rs.getDouble("total_amount"),
                    rs.getString("status"),
                    rs.getString("note"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                );
                po.setDetails(detailDAO.getDetailsByPoId(rs.getInt("po_id")));
                orders.add(po);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all purchase orders: " + e.getMessage());
        }
        return orders;
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) {
        List<PurchaseOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM purchase_orders WHERE status = ? ORDER BY order_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder po = new PurchaseOrder(
                        rs.getInt("po_id"),
                        rs.getInt("supplier_id"),
                        rs.getInt("create_by_user_id"),
                        rs.getTimestamp("order_date"),
                        rs.getTimestamp("expected_date"),
                        rs.getDouble("total_amount"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    po.setDetails(detailDAO.getDetailsByPoId(rs.getInt("po_id")));
                    orders.add(po);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting purchase orders by status: " + e.getMessage());
        }
        return orders;
    }

    public boolean updatePurchaseOrderStatus(int poId, String newStatus) {
        String sql = "UPDATE purchase_orders SET status = ?, updated_at = ? WHERE po_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
            stmt.setInt(3, poId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating purchase order status: " + e.getMessage());
            return false;
        }
    }

    public int getPurchaseOrderCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM purchase_orders WHERE status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting purchase orders by status: " + e.getMessage());
        }
        return 0;
    }
}