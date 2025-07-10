/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class ColorDAO {
    private final DatabaseConnection connection;

    public ColorDAO(DatabaseConnection connection) {
        this.connection = connection;
    }
    
    public boolean create(Color color) {
        String sql = "INSERT INTO colors (name, description, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, color.getName());
            stmt.setString(2, color.getDescription());
            stmt.setTimestamp(3, new java.sql.Timestamp(color.getCreated_at().getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(color.getUpdated_at().getTime()));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    color.setColorId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Color getById(Long id) {
        String sql = "SELECT * FROM colors WHERE color_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Color color = new Color();
                color.setColorId(rs.getInt("color_id"));
                color.setName(rs.getString("name"));
                color.setDescription(rs.getString("description"));
                color.setCreated_at(rs.getTimestamp("created_at"));
                color.setUpdated_at(rs.getTimestamp("updated_at"));
                return color;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Color> getAll() {
        List<Color> colors = new ArrayList<>();
        String sql = "SELECT * FROM colors";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Color color = new Color();
                color.setColorId(rs.getLong("color_id"));
                color.setName(rs.getString("name"));
                color.setDescription(rs.getString("description"));
                color.setCreated_at(rs.getTimestamp("created_at"));
                color.setUpdated_at(rs.getTimestamp("updated_at"));
                colors.add(color);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colors;
    }

    public boolean update(Color color) {
        String sql = "UPDATE colors SET name = ?, description = ?, updated_at = ? WHERE color_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, color.getName());
            stmt.setString(2, color.getDescription());
            stmt.setTimestamp(3, new java.sql.Timestamp(color.getUpdated_at().getTime()));
            stmt.setLong(4, color.getColorId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM colors WHERE color_id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Color getByIdwithConn(long id, Connection conn) {
        String sql = "SELECT * FROM colors WHERE color_id = ?";
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Color color = new Color();
                color.setColorId(rs.getInt("color_id"));
                color.setName(rs.getString("name"));
                color.setDescription(rs.getString("description"));
                color.setCreated_at(rs.getTimestamp("created_at"));
                color.setUpdated_at(rs.getTimestamp("updated_at"));
                return color;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}