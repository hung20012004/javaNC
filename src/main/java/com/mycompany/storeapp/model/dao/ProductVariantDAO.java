package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.ProductVariant;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductVariantDAO {
    private final DatabaseConnection connection;
    private final Connection conn;

    public ProductVariantDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
    }

    public ProductVariant getVariantById(int variantId) {
        String sql = "SELECT * FROM product_variants WHERE variant_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, variantId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ProductVariant variant = new ProductVariant();
                variant.setVariantId(rs.getInt("variant_id"));
                variant.setProductId(rs.getInt("product_id"));
                variant.setColorId(rs.getInt("color_id"));
                variant.setSizeId(rs.getInt("size_id"));
                variant.setImageUrl(rs.getString("image_url"));
                variant.setStockQuantity(rs.getInt("stock_quantity"));
                variant.setPrice(rs.getBigDecimal("price"));
                return variant;
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant by ID: " + e.getMessage());
        }
        return null;
    }

    public ProductVariant getByProductColorSize(long productId, long colorId, int sizeId) {
        String sql = "SELECT * FROM product_variants WHERE product_id = ? AND color_id = ? AND size_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            stmt.setLong(2, colorId);
            stmt.setInt(3, sizeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ProductVariant variant = new ProductVariant();
                variant.setVariantId(rs.getInt("variant_id"));
                variant.setProductId(rs.getInt("product_id"));
                variant.setColorId(rs.getInt("color_id"));
                variant.setSizeId(rs.getInt("size_id"));
                variant.setImageUrl(rs.getString("image_url"));
                variant.setStockQuantity(rs.getInt("stock_quantity"));
                variant.setPrice(rs.getBigDecimal("price"));
                return variant;
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant by product, color, and size: " + e.getMessage());
        }
        return null;
    }

    public void updateStockQuantity(int variantId, int stockQuantity) {
        String sql = "UPDATE product_variants SET stock_quantity = ? WHERE variant_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stockQuantity);
            stmt.setInt(2, variantId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating stock quantity: " + e.getMessage());
        }
    }
   
}