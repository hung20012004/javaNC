package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.ProductImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                    image.setProductId(rs.getInt("product_id"));
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
}