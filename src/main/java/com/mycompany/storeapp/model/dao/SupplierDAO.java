/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;


import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Supplier;
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
public class SupplierDAO {
    private final DatabaseConnection dbConnection;

    public SupplierDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public boolean create(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact_name, phone, email, address, description, logo_url, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactName());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getDescription());
            stmt.setNull(7, java.sql.Types.VARCHAR);
            stmt.setBoolean(8, supplier.getIsActive());
            stmt.setTimestamp(9, new java.sql.Timestamp(supplier.getCreatedAt().getTime()));
            stmt.setTimestamp(10, new java.sql.Timestamp(supplier.getUpdatedAt().getTime()));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    supplier.setSupplierId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getInt("supplier_id"));
                supplier.setName(rs.getString("name"));
                supplier.setContactName(rs.getString("contact_name"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setEmail(rs.getString("email"));
                supplier.setAddress(rs.getString("address"));
                supplier.setDescription(rs.getString("description"));
                supplier.setLogoUrl(rs.getString("logo_url"));
                supplier.setIsActive(rs.getBoolean("is_active"));
                supplier.setCreatedAt(rs.getTimestamp("created_at"));
                supplier.setUpdatedAt(rs.getTimestamp("updated_at"));
                suppliers.add(supplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public boolean update(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ?, contact_name = ?, phone = ?, email = ?, address = ?, description = ?, logo_url = ?, is_active = ?, updated_at = ? WHERE supplier_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getContactName());
            stmt.setString(3, supplier.getPhone());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getDescription());
            stmt.setNull(7, java.sql.Types.VARCHAR);
            stmt.setBoolean(8, supplier.getIsActive());
            stmt.setTimestamp(9, new java.sql.Timestamp(supplier.getUpdatedAt().getTime()));
            stmt.setInt(10, supplier.getSupplierId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Supplier getById(int id) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getInt("supplier_id"));
                supplier.setName(rs.getString("name"));
                supplier.setContactName(rs.getString("contact_name"));
                supplier.setPhone(rs.getString("phone"));
                supplier.setEmail(rs.getString("email"));
                supplier.setAddress(rs.getString("address"));
                supplier.setDescription(rs.getString("description"));
                supplier.setLogoUrl(rs.getString("logo_url"));
                supplier.setIsActive(rs.getBoolean("is_active"));
                supplier.setCreatedAt(rs.getTimestamp("created_at"));
                supplier.setUpdatedAt(rs.getTimestamp("updated_at"));
                return supplier;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
