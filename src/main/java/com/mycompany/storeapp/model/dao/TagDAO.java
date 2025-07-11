/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Tag;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hi
 */
public class TagDAO {
    private final DatabaseConnection connection;
    Connection conn;

    public TagDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
    }
    
    /**
     * Tạo mới một tag
     * @param tag Tag cần tạo
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean create(Tag tag) {
        String sql = "INSERT INTO tags (name, slug) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getSlug());

            int affected = stmt.executeUpdate();
            if (affected == 0) return false;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tag.setTagId(rs.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tag theo ID
     * @param id ID của tag
     * @return Tag object hoặc null nếu không tìm thấy
     */
    public Tag getById(int id) {
        String sql = "SELECT * FROM tags WHERE tag_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTag(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy tất cả tags
     * @return List các Tag
     */
    public List<Tag> getAll() {
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT * FROM tags ORDER BY name ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRowToTag(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tìm kiếm tag theo tên
     * @param name Tên tag cần tìm
     * @return List các Tag khớp với tên
     */
    public List<Tag> findByName(String name) {
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT * FROM tags WHERE name LIKE ? ORDER BY name ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToTag(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Kiểm tra tag có tồn tại theo tên
     * @param name Tên tag
     * @return true nếu tồn tại, false nếu không
     */
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM tags WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            
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

    /**
     * Kiểm tra tag có tồn tại theo slug
     * @param slug Slug của tag
     * @return true nếu tồn tại, false nếu không
     */
    public boolean existsBySlug(String slug) {
        String sql = "SELECT COUNT(*) FROM tags WHERE slug = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, slug);
            
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

    /**
     * Cập nhật tag
     * @param tag Tag cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean update(Tag tag) {
        String sql = "UPDATE tags SET name = ?, slug = ? WHERE tag_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getSlug());
            stmt.setInt(3, tag.getTagId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa tag
     * @param id ID của tag cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM tags WHERE tag_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tags theo product ID
     * @param productId ID của product
     * @return List các Tag thuộc về product
     */
    public List<Tag> getTagsByProductId(int productId) {
        List<Tag> list = new ArrayList<>();
        String sql = "SELECT t.* FROM tags t " +
                    "INNER JOIN product_tags pt ON t.tag_id = pt.tag_id " +
                    "WHERE pt.product_id = ? ORDER BY t.name ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToTag(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thêm tag vào product
     * @param productId ID của product
     * @param tagId ID của tag
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addTagToProduct(int productId, int tagId) {
        String sql = "INSERT INTO product_tags (product_id, tag_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, tagId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tag khỏi product
     * @param productId ID của product
     * @param tagId ID của tag
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean removeTagFromProduct(int productId, int tagId) {
        String sql = "DELETE FROM product_tags WHERE product_id = ? AND tag_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, tagId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả tags khỏi product
     * @param productId ID của product
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean removeAllTagsFromProduct(int productId) {
        String sql = "DELETE FROM product_tags WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Kiểm tra tag có được sử dụng bởi product nào không
     * @param tagId ID của tag
     * @return true nếu được sử dụng, false nếu không
     */
    public boolean isTagUsedByProducts(int tagId) {
        String sql = "SELECT COUNT(*) FROM product_tags WHERE tag_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tagId);
            
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

    /**
     * Map ResultSet row to Tag object
     * @param rs ResultSet
     * @return Tag object
     * @throws SQLException
     */
    private Tag mapRowToTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setTagId(rs.getInt("tag_id"));
        tag.setName(rs.getString("name"));
        tag.setSlug(rs.getString("slug"));
        return tag;
    }
}