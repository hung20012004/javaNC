package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final DatabaseConnection dbConnection;

    public ProductDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public boolean create(Product product) {
        String sql = "INSERT INTO products (product_id, category_id, material_id, brand, name, gender, care_instruction, slug, description, price, sale_price, stock_quantity, sku, min_purchase_quantity, max_purchase_quantity, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, product.getProductId());
            stmt.setLong(2, product.getCategoryId());
            stmt.setLong(3, product.getMaterialId());
            stmt.setString(4, product.getBrand());
            stmt.setString(5, product.getName());
            stmt.setString(6, product.getGender());
            stmt.setString(7, product.getCareInstruction());
            stmt.setString(8, product.getSlug());
            stmt.setString(9, product.getDescription());
            stmt.setDouble(10, product.getPrice());
            stmt.setDouble(11, product.getSalePrice());
            stmt.setInt(12, product.getStockQuantity());
            stmt.setString(13, product.getSku());
            stmt.setInt(14, product.getMinPurchaseQuantity());
            stmt.setInt(15, product.getMaxPurchaseQuantity());
            stmt.setBoolean(16, product.isActive());
            stmt.setTimestamp(17, new Timestamp(System.currentTimeMillis()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Product getById(long id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getLong("product_id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setMaterialId(rs.getLong("material_id"));
                product.setBrand(rs.getString("brand"));
                product.setName(rs.getString("name"));
                product.setGender(rs.getString("gender"));
                product.setCareInstruction(rs.getString("care_instruction"));
                product.setSlug(rs.getString("slug"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setSalePrice(rs.getDouble("sale_price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setSku(rs.getString("sku"));
                product.setMinPurchaseQuantity(rs.getInt("min_purchase_quantity"));
                product.setMaxPurchaseQuantity(rs.getInt("max_purchase_quantity"));
                product.setActive(rs.getBoolean("is_active"));
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getLong("product_id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setMaterialId(rs.getLong("material_id"));
                product.setBrand(rs.getString("brand"));
                product.setName(rs.getString("name"));
                product.setGender(rs.getString("gender"));
                product.setCareInstruction(rs.getString("care_instruction"));
                product.setSlug(rs.getString("slug"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getDouble("price"));
                product.setSalePrice(rs.getDouble("sale_price"));
                product.setStockQuantity(rs.getInt("stock_quantity"));
                product.setSku(rs.getString("sku"));
                product.setMinPurchaseQuantity(rs.getInt("min_purchase_quantity"));
                product.setMaxPurchaseQuantity(rs.getInt("max_purchase_quantity"));
                product.setActive(rs.getBoolean("is_active"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean update(Product product) {
        String sql = "UPDATE products SET category_id = ?, material_id = ?, brand = ?, name = ?, gender = ?, care_instruction = ?, slug = ?, description = ?, price = ?, sale_price = ?, stock_quantity = ?, sku = ?, min_purchase_quantity = ?, max_purchase_quantity = ?, is_active = ?, updated_at = ? WHERE product_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, product.getCategoryId());
            stmt.setLong(2, product.getMaterialId());
            stmt.setString(3, product.getBrand());
            stmt.setString(4, product.getName());
            stmt.setString(5, product.getGender());
            stmt.setString(6, product.getCareInstruction());
            stmt.setString(7, product.getSlug());
            stmt.setString(8, product.getDescription());
            stmt.setDouble(9, product.getPrice());
            stmt.setDouble(10, product.getSalePrice());
            stmt.setInt(11, product.getStockQuantity());
            stmt.setString(12, product.getSku());
            stmt.setInt(13, product.getMinPurchaseQuantity());
            stmt.setInt(14, product.getMaxPurchaseQuantity());
            stmt.setBoolean(15, product.isActive());
            stmt.setTimestamp(16, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(17, product.getProductId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(long id) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private long getLongOrZero(ResultSet rs, String columnName) {
        try {
            return rs.getLong(columnName);
        } catch (SQLException e) {
            return 0;
        }
    }
}