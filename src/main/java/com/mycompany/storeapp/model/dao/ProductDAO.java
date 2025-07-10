package com.mycompany.storeapp.model.dao;

/**
 *
 * @author Hi
 */
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final DatabaseConnection connection;
    private final ProductVariantDAO productVariantDAO;

    public ProductDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.productVariantDAO = new ProductVariantDAO(connection);
    }

    /**
     * Lấy sản phẩm theo ID
     */
    public Product getProductById(long productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try ( Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                loadProductVariants(product);
                return product;
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
        String sql = "SELECT * FROM products ORDER BY product_id";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            int count = 0;
            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                products.add(product);
            }
            for (Product product : products) {
                    loadProductVariants(product); 
             }

        } catch (SQLException e) {
            System.err.println("Error getting all products: " + e.getMessage());
        }

        return products;
    }

    /**
     * Lấy sản phẩm đang active với ProductVariants
     */
    public List<Product> getActiveProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active = true ORDER BY created_at DESC";

        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = mapResultSetToProduct(rs);
                products.add(product);
            }
            for (Product product : products) {
                    loadProductVariants(product); 
             }

        } catch (SQLException e) {
            System.err.println("Error getting active products: " + e.getMessage());
        }

        return products;
    }
    
    /**
     * Thêm mới sản phẩm
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, description, category_id, material_id, brand, gender, " +
                            "care_instruction, slug, price, sale_price, stock_quantity, sku, min_purchase_quantity, " +
                            "max_purchase_quantity, is_active, created_at, updated_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setLong(3, product.getCategoryId());
            stmt.setLong(4, product.getMaterialId());
            stmt.setString(5, product.getBrand());
            stmt.setString(6, product.getGender());
            stmt.setString(7, product.getCareInstruction());
            stmt.setString(8, product.getSlug());
            stmt.setDouble(9, product.getPrice());
            stmt.setDouble(10, product.getSalePrice());
            stmt.setInt(11, product.getStockQuantity());
            stmt.setString(12, product.getSku());
            stmt.setInt(13, product.getMinPurchaseQuantity());
            stmt.setInt(14, product.getMaxPurchaseQuantity());
            stmt.setBoolean(15, product.isActive());
            stmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));

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
        String sql = "UPDATE products SET name = ?, description = ?, category_id = ?, material_id = ?, " +
                            "brand = ?, gender = ?, care_instruction = ?, slug = ?, price = ?, sale_price = ?, " +
                            "stock_quantity = ?, sku = ?, min_purchase_quantity = ?, max_purchase_quantity = ?, " +
                            "is_active = ?, updated_at = ? WHERE product_id = ?";

        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setLong(3, product.getCategoryId());
            stmt.setLong(4, product.getMaterialId());
            stmt.setString(5, product.getBrand());
            stmt.setString(6, product.getGender());
            stmt.setString(7, product.getCareInstruction());
            stmt.setString(8, product.getSlug());
            stmt.setDouble(9, product.getPrice());
            stmt.setDouble(10, product.getSalePrice());
            stmt.setInt(11, product.getStockQuantity());
            stmt.setString(12, product.getSku());
            stmt.setInt(13, product.getMinPurchaseQuantity());
            stmt.setInt(14, product.getMaxPurchaseQuantity());
            stmt.setBoolean(15, product.isActive());
            stmt.setTimestamp(16, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(17, product.getProductId());

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

        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
        }

        return false;
    }
    
    /**
     * Cập nhật trạng thái active của sản phẩm
     */
    public boolean updateProductActiveStatus(long productId, boolean isActive) {
        String sql = "UPDATE products SET is_active = ?, updated_at = ? WHERE product_id = ?";

        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isActive);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, productId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating product active status: " + e.getMessage());
        }

        return false;
    }

        /**
     * Load ProductVariants for a product
     */
    private void loadProductVariants(Product product) {
        if (product.getProductId() > 0) {
            List<ProductVariant> variants = productVariantDAO.getVariantsByProductId(product.getProductId());
            product.setVariants(variants);
        }
    }
    
    /**
     * Ánh xạ từ ResultSet sang đối tượng Product
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getLong("product_id"));
        p.setCategoryId(rs.getInt("category_id"));
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

