/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;

/**
 *
 * @author Hi
 */
import com.mycompany.storeapp.model.entity.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
public class CategoryDAO {
    private final DatabaseConnection connection;
    Connection conn ;

    public CategoryDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
    }
    
    public boolean create(Category category) {
        String sql = "INSERT INTO categories (name, slug, description, image_url, is_active, display_order, created_at) " +
                     "VALUES ( ?, ?, ?, ?, ?, ?, ?)";
        try (
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getSlug());
            stmt.setString(3, category.getDescription());
            stmt.setString(4, category.getImageUrl());
            stmt.setBoolean(5, category.getIsActive());
            stmt.setObject(6, category.getDisplayOrder(), Types.INTEGER);
            stmt.setDate(7, new java.sql.Date(category.getCreated_at().getTime()));

            int affected = stmt.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    category.setCategoryId(rs.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ - Get by ID
    public Category getById(int id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCategory(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY display_order ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToCategory(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean update(Category category) {
        String sql = "UPDATE categories SET name = ?, slug = ?, description = ?, " +
                     "image_url = ?, is_active = ?, display_order = ?, updated_at = ? WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            stmt.setString(2, category.getSlug());
            stmt.setString(3, category.getDescription());
            stmt.setString(4, category.getImageUrl());
            stmt.setBoolean(5, category.getIsActive());
            stmt.setObject(6, category.getDisplayOrder(), Types.INTEGER);
            stmt.setInt(7, category.getCategoryId());
            stmt.setDate(8, new java.sql.Date(category.getUpdated_at().getTime()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(int id) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setName(rs.getString("name"));
        category.setSlug(rs.getString("slug"));
        category.setDescription(rs.getString("description"));
        category.setImageUrl(rs.getString("image_url"));
        category.setIsActive(rs.getBoolean("is_active"));
        category.setDisplayOrder((Integer) rs.getObject("display_order"));
        category.setCreated_at(rs.getDate("created_at"));
        category.setUpdated_at(rs.getDate("updated_at"));
        return category;
    }
}
