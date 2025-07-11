package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.UserProfile;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserProfileDAO {
    private DatabaseConnection connection;
    
    public UserProfileDAO(DatabaseConnection connection) {
        this.connection = connection;
    }
    
    // Tạo profile mới
    public boolean createProfile(UserProfile profile) {
        String sql = "INSERT INTO user_profiles (user_id, full_name, date_of_birth, gender, phone, avatar_url) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getFullName());
            stmt.setDate(3, Date.valueOf(profile.getDateOfBirth()));
            stmt.setString(4, profile.getGender());
            stmt.setString(5, profile.getPhone());
            stmt.setString(6, profile.getAvatarUrl());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profile.setProfileId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Lấy profile theo ID
    public UserProfile getProfileById(int profileId) {
        String sql = "SELECT * FROM user_profiles WHERE profile_id = ?";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProfileFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy profile theo user ID
    public UserProfile getProfileByUserId(int userId) {
        String sql = "SELECT * FROM user_profiles WHERE user_id = ?";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProfileFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy tất cả profiles
    public List<UserProfile> getAllProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM user_profiles ORDER BY profile_id";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                profiles.add(extractProfileFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
    
    // Cập nhật profile
    public boolean updateProfile(UserProfile profile) {
        String sql = "UPDATE user_profiles SET full_name = ?, date_of_birth = ?, gender = ?, phone = ?, avatar_url = ? WHERE profile_id = ?";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getFullName());
            stmt.setDate(2, Date.valueOf(profile.getDateOfBirth()));
            stmt.setString(3, profile.getGender());
            stmt.setString(4, profile.getPhone());
            stmt.setString(5, profile.getAvatarUrl());
            stmt.setInt(6, profile.getProfileId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Xóa profile
    public boolean deleteProfile(int profileId) {
        String sql = "DELETE FROM user_profiles WHERE profile_id = ?";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, profileId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Tìm kiếm profiles theo tên
    public List<UserProfile> searchProfilesByName(String name) {
        List<UserProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM user_profiles WHERE full_name LIKE ? ORDER BY full_name";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(extractProfileFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
    
    private UserProfile extractProfileFromResultSet(ResultSet rs) throws SQLException {
        UserProfile profile = new UserProfile();
        profile.setProfileId(rs.getInt("profile_id"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setFullName(rs.getString("full_name"));
        
        Date dateOfBirth = rs.getDate("date_of_birth");
        if (dateOfBirth != null) {
            profile.setDateOfBirth(dateOfBirth.toLocalDate());
        }
        
        profile.setGender(rs.getString("gender"));
        profile.setPhone(rs.getString("phone"));
        profile.setAvatarUrl(rs.getString("avatar_url"));
        
        return profile;
    }
    
    public boolean profileExistsForUser(int userId) {
        String sql = "SELECT COUNT(*) FROM user_profiles WHERE user_id = ?";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}