/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.dao;

/**
 *
 * @author Hi
 */
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductVariantDAO {
    private final DatabaseConnection connection;
    private final Connection conn;
    private ProductDAO productDAO;

    public ProductVariantDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
        this.productDAO = new ProductDAO(connection);
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

                // Nếu cần load Product (nếu có ProductDAO):
                 
                 Product product = productDAO.getProductById(variant.getProductId());
                 variant.setProduct(product);

                return variant;
            }

        } catch (SQLException e) {
            System.err.println("Error getting variant by ID: " + e.getMessage());
        }

        return null;
    }
    
    public List<ProductVariant> getVariantsByProductId(int productId) {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ProductVariant variant = new ProductVariant();
                variant.setVariantId(rs.getInt("variant_id"));
                variant.setProductId(rs.getInt("product_id"));
                variant.setColorId(rs.getInt("color_id"));
                variant.setSizeId(rs.getInt("size_id"));
                variant.setImageUrl(rs.getString("image_url"));
                variant.setStockQuantity(rs.getInt("stock_quantity"));
                variant.setPrice(rs.getBigDecimal("price"));

                Product product = productDAO.getProductById(variant.getProductId());
                variant.setProduct(product);

                variants.add(variant);
            }
        } catch (SQLException e) {
            System.err.println("Error getting variants by product ID: " + e.getMessage());
        }
        return variants;
    }

    public ProductVariant getVariantByProductSizeColor(int productId, int sizeId, int colorId) {
        String sql = "SELECT * FROM product_variants WHERE product_id = ? AND size_id = ? AND color_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, sizeId);
            stmt.setInt(3, colorId);
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

                Product product = productDAO.getProductById(variant.getProductId());
                variant.setProduct(product);

                return variant;
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant by product, size, and color: " + e.getMessage());
        }
        return null;
    }

    public void updateProductVariant(ProductVariant variant) {
        String sql = "UPDATE product_variants SET stock_quantity = ?, updated_at = NOW() WHERE variant_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, variant.getStockQuantity());
            stmt.setInt(2, variant.getVariantId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating variant: " + e.getMessage());
        }
    }
    
    public boolean updateQuantity(int variantId, int quantity) {
        String sql = "UPDATE product_variants SET quantity = quantity + ? WHERE variant_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, variantId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product variant quantity: " + e.getMessage());
            return false;
        }
    }
}
