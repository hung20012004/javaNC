/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Promotion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hi
 */
public class PromotionDAO {
    private final DatabaseConnection connection;

    public PromotionDAO(DatabaseConnection connection) {
        this.connection = connection;
    }
    
    public boolean create(Promotion promotion) {
        String sql = "INSERT INTO promotions (code, name, description, discount_type, discount_value, " +
                    "min_order_value, max_discount, start_date, end_date, usage_limit, used_count, is_active) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, promotion.getCode());
            stmt.setString(2, promotion.getName());
            stmt.setString(3, promotion.getDescription());
            stmt.setString(4, promotion.getDiscountType());
            stmt.setDouble(5, promotion.getDiscountValue());
            stmt.setDouble(6, promotion.getMinOrderValue());
            stmt.setDouble(7, promotion.getMaxDiscount());
            stmt.setTimestamp(8, promotion.getStartDate() != null ? Timestamp.valueOf(promotion.getStartDate()) : null);
            stmt.setTimestamp(9, promotion.getEndDate() != null ? Timestamp.valueOf(promotion.getEndDate()) : null);
            stmt.setInt(10, promotion.getUsageLimit());
            stmt.setInt(11, promotion.getUsedCount());
            stmt.setBoolean(12, promotion.isActive());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    promotion.setPromotionId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Promotion getById(int id) {
        String sql = "SELECT * FROM promotions WHERE promotion_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Promotion promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("promotion_id"));
                promotion.setCode(rs.getString("code"));
                promotion.setName(rs.getString("name"));
                promotion.setDescription(rs.getString("description"));
                promotion.setDiscountType(rs.getString("discount_type"));
                promotion.setDiscountValue(rs.getDouble("discount_value"));
                promotion.setMinOrderValue(rs.getDouble("min_order_value"));
                promotion.setMaxDiscount(rs.getDouble("max_discount"));
                
                Timestamp startDate = rs.getTimestamp("start_date");
                if (startDate != null) {
                    promotion.setStartDate(startDate.toLocalDateTime());
                }
                
                Timestamp endDate = rs.getTimestamp("end_date");
                if (endDate != null) {
                    promotion.setEndDate(endDate.toLocalDateTime());
                }
                
                promotion.setUsageLimit(rs.getInt("usage_limit"));
                promotion.setUsedCount(rs.getInt("used_count"));
                promotion.setActive(rs.getBoolean("is_active"));
                
                return promotion;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Promotion> getAll() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM promotions";
        
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Promotion promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("promotion_id"));
                promotion.setCode(rs.getString("code"));
                promotion.setName(rs.getString("name"));
                promotion.setDescription(rs.getString("description"));
                promotion.setDiscountType(rs.getString("discount_type"));
                promotion.setDiscountValue(rs.getDouble("discount_value"));
                promotion.setMinOrderValue(rs.getDouble("min_order_value"));
                promotion.setMaxDiscount(rs.getDouble("max_discount"));
                
                Timestamp startDate = rs.getTimestamp("start_date");
                if (startDate != null) {
                    promotion.setStartDate(startDate.toLocalDateTime());
                }
                
                Timestamp endDate = rs.getTimestamp("end_date");
                if (endDate != null) {
                    promotion.setEndDate(endDate.toLocalDateTime());
                }
                
                promotion.setUsageLimit(rs.getInt("usage_limit"));
                promotion.setUsedCount(rs.getInt("used_count"));
                promotion.setActive(rs.getBoolean("is_active"));
                
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotions;
    }

    public boolean update(Promotion promotion) {
        String sql = "UPDATE promotions SET code = ?, name = ?, description = ?, discount_type = ?, " +
                    "discount_value = ?, min_order_value = ?, max_discount = ?, start_date = ?, " +
                    "end_date = ?, usage_limit = ?, used_count = ?, is_active = ? WHERE promotion_id = ?";
        
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, promotion.getCode());
            stmt.setString(2, promotion.getName());
            stmt.setString(3, promotion.getDescription());
            stmt.setString(4, promotion.getDiscountType());
            stmt.setDouble(5, promotion.getDiscountValue());
            stmt.setDouble(6, promotion.getMinOrderValue());
            stmt.setDouble(7, promotion.getMaxDiscount());
            stmt.setTimestamp(8, promotion.getStartDate() != null ? Timestamp.valueOf(promotion.getStartDate()) : null);
            stmt.setTimestamp(9, promotion.getEndDate() != null ? Timestamp.valueOf(promotion.getEndDate()) : null);
            stmt.setInt(10, promotion.getUsageLimit());
            stmt.setInt(11, promotion.getUsedCount());
            stmt.setBoolean(12, promotion.isActive());
            stmt.setInt(13, promotion.getPromotionId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM promotions WHERE promotion_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Promotion getByIdWithConn(int id, Connection conn) {
        String sql = "SELECT * FROM promotions WHERE promotion_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Promotion promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("promotion_id"));
                promotion.setCode(rs.getString("code"));
                promotion.setName(rs.getString("name"));
                promotion.setDescription(rs.getString("description"));
                promotion.setDiscountType(rs.getString("discount_type"));
                promotion.setDiscountValue(rs.getDouble("discount_value"));
                promotion.setMinOrderValue(rs.getDouble("min_order_value"));
                promotion.setMaxDiscount(rs.getDouble("max_discount"));
                
                Timestamp startDate = rs.getTimestamp("start_date");
                if (startDate != null) {
                    promotion.setStartDate(startDate.toLocalDateTime());
                }
                
                Timestamp endDate = rs.getTimestamp("end_date");
                if (endDate != null) {
                    promotion.setEndDate(endDate.toLocalDateTime());
                }
                
                promotion.setUsageLimit(rs.getInt("usage_limit"));
                promotion.setUsedCount(rs.getInt("used_count"));
                promotion.setActive(rs.getBoolean("is_active"));
                
                return promotion;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Promotion getByCode(String code) {
        String sql = "SELECT * FROM promotions WHERE code = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Promotion promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("promotion_id"));
                promotion.setCode(rs.getString("code"));
                promotion.setName(rs.getString("name"));
                promotion.setDescription(rs.getString("description"));
                promotion.setDiscountType(rs.getString("discount_type"));
                promotion.setDiscountValue(rs.getDouble("discount_value"));
                promotion.setMinOrderValue(rs.getDouble("min_order_value"));
                promotion.setMaxDiscount(rs.getDouble("max_discount"));
                
                Timestamp startDate = rs.getTimestamp("start_date");
                if (startDate != null) {
                    promotion.setStartDate(startDate.toLocalDateTime());
                }
                
                Timestamp endDate = rs.getTimestamp("end_date");
                if (endDate != null) {
                    promotion.setEndDate(endDate.toLocalDateTime());
                }
                
                promotion.setUsageLimit(rs.getInt("usage_limit"));
                promotion.setUsedCount(rs.getInt("used_count"));
                promotion.setActive(rs.getBoolean("is_active"));
                
                return promotion;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Promotion> getActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM promotions WHERE is_active = true AND " +
                    "(start_date IS NULL OR start_date <= NOW()) AND " +
                    "(end_date IS NULL OR end_date >= NOW())";
        
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Promotion promotion = new Promotion();
                promotion.setPromotionId(rs.getInt("promotion_id"));
                promotion.setCode(rs.getString("code"));
                promotion.setName(rs.getString("name"));
                promotion.setDescription(rs.getString("description"));
                promotion.setDiscountType(rs.getString("discount_type"));
                promotion.setDiscountValue(rs.getDouble("discount_value"));
                promotion.setMinOrderValue(rs.getDouble("min_order_value"));
                promotion.setMaxDiscount(rs.getDouble("max_discount"));
                
                Timestamp startDate = rs.getTimestamp("start_date");
                if (startDate != null) {
                    promotion.setStartDate(startDate.toLocalDateTime());
                }
                
                Timestamp endDate = rs.getTimestamp("end_date");
                if (endDate != null) {
                    promotion.setEndDate(endDate.toLocalDateTime());
                }
                
                promotion.setUsageLimit(rs.getInt("usage_limit"));
                promotion.setUsedCount(rs.getInt("used_count"));
                promotion.setActive(rs.getBoolean("is_active"));
                
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotions;
    }
}