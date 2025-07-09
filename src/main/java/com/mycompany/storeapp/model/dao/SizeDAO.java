package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Size;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hi
 */
public class SizeDAO {
    private final DatabaseConnection connection;
    private final Connection conn; // Thêm field này để consistent với ProductVariantDAO
    
    public SizeDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
    }
    
    public List<Size> getAll() {
        List<Size> sizes = new ArrayList<>();
        String sql = "SELECT * FROM sizes";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Size size = mapResultSetToSize(rs);
                sizes.add(size);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all sizes: " + e.getMessage());
        }
        return sizes;
    }
    
    public Size getById(int id) {
        String sql = "SELECT * FROM sizes WHERE size_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSize(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting size by ID: " + e.getMessage());
        }
        return null;
    }
    
    public boolean create(Size size) {
        String sql = "INSERT INTO sizes (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, size.getName());
            stmt.setString(2, size.getDescription());
            stmt.setTimestamp(3, new java.sql.Timestamp(size.getCreated_at().getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(size.getUpdated_at().getTime()));
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        size.setSizeId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating size: " + e.getMessage());
            return false;
        }
    }
    
    public boolean update(Size size) {
        String sql = "UPDATE sizes SET name = ?, description = ?, updated_at = ? WHERE size_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, size.getName());
            stmt.setString(2, size.getDescription());
            stmt.setTimestamp(3, new java.sql.Timestamp(size.getUpdated_at().getTime())); // Fix: dùng updated_at
            stmt.setInt(4, size.getSizeId()); // Fix: thêm size_id parameter
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating size: " + e.getMessage());
            return false;
        }
    }
    
    public boolean delete(long id) {
        String sql = "DELETE FROM sizes WHERE size_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting size: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper method to map ResultSet to Size object
     */
    private Size mapResultSetToSize(ResultSet rs) throws SQLException {
        Size size = new Size();
        size.setSizeId(rs.getInt("size_id"));
        size.setName(rs.getString("name"));
        size.setDescription(rs.getString("description"));
        size.setCreated_at(rs.getTimestamp("created_at"));
        size.setUpdated_at(rs.getTimestamp("updated_at"));
        return size;
    }
    
      public Size getByIdwithConn(int id, Connection conn) {
        String sql = "SELECT * FROM sizes WHERE size_id = ?";
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSize(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting size by ID: " + e.getMessage());
        }
        return null;
    }
}