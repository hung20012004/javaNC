package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.ProductImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductImageDAO {
    private final DatabaseConnection dbConnection;

    public ProductImageDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public List<ProductImage> getImagesByProductId(long productId) {
        List<ProductImage> images = new ArrayList<>();
        String sql = "SELECT * FROM product_images WHERE product_id = ? ORDER BY display_order ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProductImage image = new ProductImage();
                    image.setImageId(rs.getInt("image_id"));
                    image.setProductId(rs.getLong("product_id"));
                    image.setImageUrl(rs.getString("image_url"));
                    image.setDisplayOrder(rs.getInt("display_order"));
                    image.setPrimary(rs.getBoolean("is_primary"));
                    images.add(image);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally log the error or handle it gracefully
        }
        return images;
    }
    /**
     * Thêm mới hình ảnh
     */
    public boolean createImage(ProductImage image) {
        String sql = "INSERT INTO product_images (product_id, image_url, display_order, is_primary) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, image.getProductId());
            stmt.setString(2, image.getImageUrl());
            stmt.setInt(3, image.getDisplayOrder());
            stmt.setBoolean(4, image.isPrimary());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Lấy ID được generate
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        image.setImageId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo hình ảnh: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Cập nhật hình ảnh
     */
    public boolean updateImage(ProductImage image) {
        String sql = "UPDATE product_images SET image_url = ?, display_order = ?, is_primary = ? WHERE image_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, image.getImageUrl());
            stmt.setInt(2, image.getDisplayOrder());
            stmt.setBoolean(3, image.isPrimary());
            stmt.setInt(4, image.getImageId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật hình ảnh: " + e.getMessage());
        }
    }
    
    /**
     * Xóa hình ảnh
     */
    public boolean deleteImage(int imageId) {
        String sql = "DELETE FROM product_images WHERE image_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, imageId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa hình ảnh: " + e.getMessage());
        }
    }
    
    /**
     * Xóa tất cả hình ảnh của một sản phẩm
     */
    public boolean deleteImagesByProductId(long productId) {
        String sql = "DELETE FROM product_images WHERE product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, productId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected >= 0; // Trả về true ngay cả khi không có hình ảnh nào bị xóa
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa hình ảnh sản phẩm: " + e.getMessage());
        }
    }
    
    /**
     * Lấy hình ảnh chính của sản phẩm
     */
    public ProductImage getPrimaryImageByProductId(long productId) {
        String sql = "SELECT * FROM product_images WHERE product_id = ? AND is_primary = true LIMIT 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ProductImage image = new ProductImage();
                    image.setImageId(rs.getInt("image_id"));
                    image.setProductId(rs.getInt("product_id"));
                    image.setImageUrl(rs.getString("image_url"));
                    image.setDisplayOrder(rs.getInt("display_order"));
                    image.setPrimary(rs.getBoolean("is_primary"));
                    return image;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy hình ảnh chính: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Cập nhật tất cả hình ảnh của sản phẩm thành không phải primary
     */
    public boolean clearPrimaryFlags(long productId) {
        String sql = "UPDATE product_images SET is_primary = false WHERE product_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, productId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected >= 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật primary flags: " + e.getMessage());
        }
    }
    
    /**
     * Lấy hình ảnh theo ID
     */
    public ProductImage getImageById(int imageId) {
        String sql = "SELECT * FROM product_images WHERE image_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, imageId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ProductImage image = new ProductImage();
                    image.setImageId(rs.getInt("image_id"));
                    image.setProductId(rs.getInt("product_id"));
                    image.setImageUrl(rs.getString("image_url"));
                    image.setDisplayOrder(rs.getInt("display_order"));
                    image.setPrimary(rs.getBoolean("is_primary"));
                    return image;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy hình ảnh: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Cập nhật thứ tự hiển thị cho danh sách hình ảnh
     */
    public boolean updateDisplayOrders(List<ProductImage> images) {
        String sql = "UPDATE product_images SET display_order = ? WHERE image_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (ProductImage image : images) {
                stmt.setInt(1, image.getDisplayOrder());
                stmt.setInt(2, image.getImageId());
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            
            // Kiểm tra xem tất cả các update có thành công không
            for (int result : results) {
                if (result <= 0) {
                    return false;
                }
            }
            
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật thứ tự hiển thị: " + e.getMessage());
        }
    }
}