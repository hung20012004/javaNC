/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.model.entity.Material;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hi
 */
public class MaterialDAO {
    private final DatabaseConnection connection;
    Connection conn ;

    public MaterialDAO(DatabaseConnection connection) {
        this.connection = connection;
        //this.conn = connection.getConnection();
    }
    
    public boolean create(Material material) {
       String sql = "INSERT INTO materials (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = connection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, material.getName());
            stmt.setString(2, material.getDescription());
            stmt.setTimestamp(3, new java.sql.Timestamp(material.getCreated_at().getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(material.getUpdated_at().getTime()));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    material.setMaterialId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
    public Material getById(int id) {
        String sql = "SELECT * FROM materials WHERE material_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setName(rs.getString("name"));
                material.setDescription(rs.getString("description"));
                material.setCreated_at(rs.getTimestamp("created_at"));
                material.setUpdated_at(rs.getTimestamp("updated_at"));
                return material;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Material> getAll() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Material material = new Material();
                material.setMaterialId(rs.getInt("material_id"));
                material.setName(rs.getString("name"));
                material.setDescription(rs.getString("description"));
                material.setCreated_at(rs.getTimestamp("created_at"));
                material.setUpdated_at(rs.getTimestamp("updated_at"));
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }

   public boolean update(Material material) {
        String sql = "UPDATE materials SET name = ?, description = ?, updated_at = ? WHERE material_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, material.getName());
            stmt.setString(2, material.getDescription());
            stmt.setTimestamp(3, new java.sql.Timestamp(material.getUpdated_at().getTime()));
            stmt.setLong(4, material.getMaterialId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM materials WHERE material_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

//    private Category mapRowToCategory(ResultSet rs) throws SQLException {
//        Category category = new Category();
//        category.setCategoryId(rs.getInt("category_id"));
//        category.setName(rs.getString("name"));
//        category.setSlug(rs.getString("slug"));
//        category.setDescription(rs.getString("description"));
//        category.setImageUrl(rs.getString("image_url"));
//        category.setIsActive(rs.getBoolean("is_active"));
//        category.setDisplayOrder((Integer) rs.getObject("display_order"));
//        category.setCreated_at(rs.getDate("created_at"));
//        category.setUpdated_at(rs.getDate("updated_at"));
//        return category;
//    }
}
