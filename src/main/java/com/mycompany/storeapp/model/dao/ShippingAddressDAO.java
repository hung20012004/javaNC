/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.model.entity.ShippingAddress;
import com.mycompany.storeapp.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for ShippingAddress management
 * @author Hi
 */
public class ShippingAddressDAO {
    private final DatabaseConnection connection;
    Connection conn;

    public ShippingAddressDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
    }

    /**
     * Get all shipping addresses
     */
    public List<ShippingAddress> getAllShippingAddresses() {
        List<ShippingAddress> addresses = new ArrayList<>();
        String sql = "SELECT * FROM shipping_addresses ORDER BY address_id DESC";

        try (
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                addresses.add(mapResultSetToShippingAddress(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all shipping addresses: " + e.getMessage());
        }

        return addresses;
    }

    /**
     * Get shipping address by ID
     */
    public ShippingAddress getShippingAddressById(int addressId) {
        String sql = "SELECT * FROM shipping_addresses WHERE address_id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, addressId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToShippingAddress(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting shipping address by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get shipping addresses by user ID
     */
    public List<ShippingAddress> getShippingAddressesByUserId(int userId) {
        List<ShippingAddress> addresses = new ArrayList<>();
        String sql = "SELECT * FROM shipping_addresses WHERE user_id = ? ORDER BY is_default DESC, address_id DESC";

        try (
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                addresses.add(mapResultSetToShippingAddress(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting shipping addresses by user ID: " + e.getMessage());
        }

        return addresses;
    }

    /**
     * Get default shipping address for user
     */
    public ShippingAddress getDefaultAddress(int userId) {
        String sql = "SELECT * FROM shipping_addresses WHERE user_id = ? AND is_default = true LIMIT 1";

        try (
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToShippingAddress(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error getting default shipping address: " + e.getMessage());
        }

        return null;
    }

    /**
     * Set default shipping address for user
     */
    public boolean setDefaultAddress(int userId, int addressId) {
        Connection conn = null;
        try {
            conn = connection.getConnection();
            conn.setAutoCommit(false);

            // First, unset all default addresses for this user
            String unsetSql = "UPDATE shipping_addresses SET is_default = false WHERE user_id = ?";
            try (PreparedStatement unsetStmt = conn.prepareStatement(unsetSql)) {
                unsetStmt.setInt(1, userId);
                unsetStmt.executeUpdate();
            }

            // Then set the specified address as default
            String setSql = "UPDATE shipping_addresses SET is_default = true WHERE address_id = ? AND user_id = ?";
            try (PreparedStatement setStmt = conn.prepareStatement(setSql)) {
                setStmt.setInt(1, addressId);
                setStmt.setInt(2, userId);
                int rowsAffected = setStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error setting default shipping address: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    /**
     * Update shipping address
     */
    public boolean updateShippingAddress(ShippingAddress address) {
        String sql = "UPDATE shipping_addresses SET recipient_name = ?, phone = ?, " +
                     "province = ?, district = ?, ward = ?, street_address = ?, is_default = ?, " +
                     "latitude = ?, longitude = ?, updated_at = ? WHERE address_id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, address.getRecipientName());
            stmt.setString(2, address.getPhone());
            stmt.setString(3, address.getProvince());
            stmt.setString(4, address.getDistrict());
            stmt.setString(5, address.getWard());
            stmt.setString(6, address.getStreetAddress());
            stmt.setBoolean(7, address.isDefault());
            stmt.setBigDecimal(8, address.getLatitude());
            stmt.setBigDecimal(9, address.getLongitude());
            stmt.setTimestamp(10, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setInt(11, address.getAddressId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating shipping address: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add new shipping address
     */
    public boolean addShippingAddress(ShippingAddress address) {
        String sql = "INSERT INTO shipping_addresses (user_id, recipient_name, phone, province, " +
                     "district, ward, street_address, is_default, latitude, longitude, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, address.getUserId());
            stmt.setString(2, address.getRecipientName());
            stmt.setString(3, address.getPhone());
            stmt.setString(4, address.getProvince());
            stmt.setString(5, address.getDistrict());
            stmt.setString(6, address.getWard());
            stmt.setString(7, address.getStreetAddress());
            stmt.setBoolean(8, address.isDefault());
            stmt.setBigDecimal(9, address.getLatitude());
            stmt.setBigDecimal(10, address.getLongitude());
            stmt.setTimestamp(11, new java.sql.Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(12, new java.sql.Timestamp(System.currentTimeMillis()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        address.setAddressId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error adding shipping address: " + e.getMessage());
        }

        return false;
    }

    /**
     * Delete shipping address
     */
    public boolean deleteShippingAddress(int addressId) {
        String sql = "DELETE FROM shipping_addresses WHERE address_id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, addressId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting shipping address: " + e.getMessage());
            return false;
        }
    }

    /**
     * Map ResultSet to ShippingAddress object
     */
    private ShippingAddress mapResultSetToShippingAddress(ResultSet rs) throws SQLException {
        ShippingAddress address = new ShippingAddress();
        address.setAddressId(rs.getInt("address_id"));
        address.setUserId(rs.getInt("user_id"));
        address.setRecipientName(rs.getString("recipient_name"));
        address.setPhone(rs.getString("phone"));
        address.setProvince(rs.getString("province"));
        address.setDistrict(rs.getString("district"));
        address.setWard(rs.getString("ward"));
        address.setStreetAddress(rs.getString("street_address"));
        address.setDefault(rs.getBoolean("is_default"));
        address.setLatitude(rs.getBigDecimal("latitude"));
        address.setLongitude(rs.getBigDecimal("longitude"));

        return address;
    }
}