/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Banner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho bảng banners
 * @author ADMIN
 */
public class BannerDAO {
    private final DatabaseConnection connection;

    public BannerDAO(DatabaseConnection connection) {
        this.connection = connection;
    }
    
    public boolean create(Banner banner) {
        String sql = "INSERT INTO banners (title, subtitle, button_text, button_link, image_url, is_active, order_sequence, start_date, end_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, banner.getTitle());
            stmt.setString(2, banner.getSubtitle());
            stmt.setString(3, banner.getButtonText());
            stmt.setString(4, banner.getButtonLink());
            stmt.setString(5, banner.getImageUrl());
            stmt.setInt(6, banner.getIsActive());
            stmt.setInt(7, banner.getOrderSequence());
            stmt.setTimestamp(8, new java.sql.Timestamp(banner.getStartDate().getTime()));
            stmt.setTimestamp(9, new java.sql.Timestamp(banner.getEndDate().getTime()));
            stmt.setTimestamp(10, new java.sql.Timestamp(banner.getCreated_at().getTime()));
            stmt.setTimestamp(11, new java.sql.Timestamp(banner.getUpdated_at().getTime()));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    banner.setBannerId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Banner getById(int id) {
        String sql = "SELECT * FROM banners WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Banner banner = new Banner();
                banner.setBannerId(rs.getInt("id"));
                banner.setTitle(rs.getString("title"));
                banner.setSubtitle(rs.getString("subtitle"));
                banner.setButtonText(rs.getString("button_text"));
                banner.setButtonLink(rs.getString("button_link"));
                banner.setImageUrl(rs.getString("image_url"));
                banner.setIsActive(rs.getInt("is_active"));
                banner.setOrderSequence(rs.getInt("order_sequence"));
                banner.setStartDate(rs.getTimestamp("start_date"));
                banner.setEndDate(rs.getTimestamp("end_date"));
                banner.setCreated_at(rs.getTimestamp("created_at"));
                banner.setUpdated_at(rs.getTimestamp("updated_at"));
                return banner;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Banner> getAll() {
        List<Banner> banners = new ArrayList<>();
        String sql = "SELECT * FROM banners";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Banner banner = new Banner();
                banner.setBannerId(rs.getInt("id"));
                banner.setTitle(rs.getString("title"));
                banner.setSubtitle(rs.getString("subtitle"));
                banner.setButtonText(rs.getString("button_text"));
                banner.setButtonLink(rs.getString("button_link"));
                banner.setImageUrl(rs.getString("image_url"));
                banner.setIsActive(rs.getInt("is_active"));
                banner.setOrderSequence(rs.getInt("order_sequence"));
                banner.setStartDate(rs.getTimestamp("start_date"));
                banner.setEndDate(rs.getTimestamp("end_date"));
                banner.setCreated_at(rs.getTimestamp("created_at"));
                banner.setUpdated_at(rs.getTimestamp("updated_at"));
                banners.add(banner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banners;
    }

    public boolean update(Banner banner) {
        String sql = "UPDATE banners SET title = ?, subtitle = ?, button_text = ?, button_link = ?, image_url = ?, is_active = ?, order_sequence = ?, start_date = ?, end_date = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, banner.getTitle());
            stmt.setString(2, banner.getSubtitle());
            stmt.setString(3, banner.getButtonText());
            stmt.setString(4, banner.getButtonLink());
            stmt.setString(5, banner.getImageUrl());
            stmt.setInt(6, banner.getIsActive());
            stmt.setInt(7, banner.getOrderSequence());
            stmt.setTimestamp(8, new java.sql.Timestamp(banner.getStartDate().getTime()));
            stmt.setTimestamp(9, new java.sql.Timestamp(banner.getEndDate().getTime()));
            stmt.setTimestamp(10, new java.sql.Timestamp(banner.getUpdated_at().getTime()));
            stmt.setLong(11, banner.getBannerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM banners WHERE id = ?";
        try (Connection conn = connection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}