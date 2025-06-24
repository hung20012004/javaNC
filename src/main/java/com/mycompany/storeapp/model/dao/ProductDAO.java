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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final DatabaseConnection connection;
    private final Connection conn;

    public ProductDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.conn = connection.getConnection();
    }

    /**
     * Lấy sản phẩm theo ID
     */
    public Product getProductById(long productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting product by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Lấy danh sách tất cả sản phẩm
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error getting all products: " + e.getMessage());
        }

        return products;
    }

    /**
     * Thêm mới sản phẩm
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, description, category_id, supplier_id, material_id, is_active, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setLong(3, product.getCategoryId());
            stmt.setLong(4, product.getMaterialId());
            stmt.setBoolean(5, product.isActive());
            stmt.setTimestamp(6, product.getCreatedAt());
            stmt.setTimestamp(7, product.getUpdatedAt());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }

        return false;
    }

    /**
     * Cập nhật thông tin sản phẩm
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, description = ?, category_id = ?, supplier_id = ?, " +
                     "material_id = ?, is_active = ?, updated_at = ? WHERE product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setLong(3, product.getCategoryId());
            stmt.setLong(4, product.getMaterialId());
            stmt.setBoolean(5, product.isActive());
            stmt.setTimestamp(6, product.getUpdatedAt());
            stmt.setLong(7, product.getProductId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }

        return false;
    }

    /**
     * Xoá sản phẩm theo ID
     */
    public boolean deleteProduct(long productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
        }

        return false;
    }

    /**
     * Ánh xạ từ ResultSet sang đối tượng Product
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getLong("product_id"));
        p.setCategoryId(rs.getLong("category_id"));
        p.setMaterialId(rs.getLong("material_id"));
        p.setBrand(rs.getString("brand"));
        p.setName(rs.getString("name"));
        p.setGender(rs.getString("gender"));
        p.setCareInstruction(rs.getString("care_instruction"));
        p.setSlug(rs.getString("slug"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getFloat("price"));
        p.setSalePrice(rs.getFloat("sale_price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setSku(rs.getString("sku"));
        p.setMinPurchaseQuantity(rs.getInt("min_purchase_quantity"));
        p.setMaxPurchaseQuantity(rs.getInt("max_purchase_quantity"));
        p.setActive(rs.getBoolean("is_active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }
}

