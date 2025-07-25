package com.mycompany.storeapp.model.dao;
/**
 *
 * @author Hi
 */
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.model.entity.Color;
import com.mycompany.storeapp.model.entity.Size;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductVariantDAO {
    private final DatabaseConnection connection;
    private ColorDAO colorDAO;
    private SizeDAO sizeDAO;
    
    public ProductVariantDAO(DatabaseConnection connection) {
        this.connection = connection;
        this.colorDAO = new ColorDAO(connection);
        this.sizeDAO = new SizeDAO(connection);
    }
    
    public ProductVariant getVariantById(int variantId) {
        String sql = "SELECT pv.*, c.name as color_name, s.name as size_name " +
                            "FROM product_variants pv " +
                            "LEFT JOIN colors c ON pv.color_id = c.color_id " +
                            "LEFT JOIN sizes s ON pv.size_id = s.size_id " +
                            "WHERE pv.variant_id = ? ORDER BY pv.variant_id";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, variantId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ProductVariant variant = mapResultSetToProductVariant(rs);  
                    loadColorAndSize(variant, conn);
                    return variant;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant by ID: " + e.getMessage());
        }
        return null;
    }
    
    public List<ProductVariant> getVariantsByProductId(long productId) {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT pv.*, c.name as color_name, s.name as size_name " +
                            "FROM product_variants pv " +
                            "LEFT JOIN colors c ON pv.color_id = c.color_id " +
                            "LEFT JOIN sizes s ON pv.size_id = s.size_id " +
                            "WHERE product_id = ?";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProductVariant variant = mapResultSetToProductVariant(rs);
                    variants.add(variant);
                }
                
                for (ProductVariant variant : variants) {
                    loadColorAndSize(variant, conn); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variants by product ID: " + e.getMessage());
            e.printStackTrace();
        }

        return variants;
    }
    
    public List<ProductVariant> getAllVariants() {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT * FROM product_variants ORDER BY product_id, variant_id";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ProductVariant variant = mapResultSetToProductVariant(rs);
                variants.add(variant);
            }
            for (ProductVariant variant : variants) {
                    loadColorAndSize(variant, conn); 
             }
            
        } catch (SQLException e) {
            System.err.println("Error getting all variants: " + e.getMessage());
        }
        return variants;
    }
    
    public ProductVariant getVariantByProductColorSize(long productId, long colorId, int sizeId) {
        String sql = "SELECT * FROM product_variants WHERE product_id = ? AND color_id = ? AND size_id = ?";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            stmt.setLong(2, colorId);
            stmt.setInt(3, sizeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ProductVariant variant = mapResultSetToProductVariant(rs);
                    loadColorAndSize(variant, conn);
                    return variant;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant by product color size: " + e.getMessage());
        }
        return null;
    }
    
    public List<ProductVariant> getVariantsInStock(long productId) {
        List<ProductVariant> variants = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ? AND stock_quantity > 0 ORDER BY variant_id";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProductVariant variant = mapResultSetToProductVariant(rs);
                    variants.add(variant);
                }
                for (ProductVariant variant : variants) {
                    loadColorAndSize(variant, conn); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variants in stock: " + e.getMessage());
        }
        return variants;
    }
    
    public void updateProductVariant(ProductVariant variant) {
        String sql = "UPDATE product_variants SET stock_quantity = ?, updated_at = NOW() WHERE variant_id = ?";
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, variant.getStockQuantity());
            stmt.setInt(2, variant.getVariantId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating variant: " + e.getMessage());
        }
    }
    
    public boolean updateQuantity(int variantId, int quantity) {
        String sql = "UPDATE product_variants SET quantity = quantity + ? WHERE variant_id = ?";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, variantId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product variant quantity: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStockQuantity(int variantId, int newQuantity) {
        String sql = "UPDATE product_variants SET stock_quantity = ? WHERE variant_id = ?";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, variantId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock quantity: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Thêm ProductVariant mới
     */
    public boolean addVariant(ProductVariant variant) {
        String sql = "INSERT INTO product_variants (product_id, color_id, size_id, image_url, stock_quantity, price) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, variant.getProductId());
            stmt.setLong(2, variant.getColorId());
            stmt.setInt(3, variant.getSizeId());
            stmt.setString(4, variant.getImageUrl());
            stmt.setInt(5, variant.getStockQuantity());
            stmt.setBigDecimal(6, variant.getPrice());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding variant: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Cập nhật ProductVariant
     */
    public boolean updateVariant(ProductVariant variant) {
        String sql = "UPDATE product_variants SET product_id = ?, color_id = ?, size_id = ?, image_url = ?, stock_quantity = ?, price = ? WHERE variant_id = ?";
        
        try (Connection conn = connection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, variant.getProductId());
            stmt.setLong(2, variant.getColorId());
            stmt.setInt(3, variant.getSizeId());
            stmt.setString(4, variant.getImageUrl());
            stmt.setInt(5, variant.getStockQuantity());
            stmt.setBigDecimal(6, variant.getPrice());
            stmt.setInt(7, variant.getVariantId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating variant: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Xóa ProductVariant
     */
    public boolean deleteVariant(int variantId) {
        String sql = "DELETE FROM product_variants WHERE variant_id = ?";
        
        try (Connection conn = connection.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, variantId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting variant: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Đếm số lượng variant theo product
     */
    public int getVariantCountByProduct(long productId) {
        String sql = "SELECT COUNT(*) FROM product_variants WHERE product_id = ?";
        
        try (Connection conn = connection.getConnection();
               PreparedStatement stmt = conn.prepareStatement(sql)) {
               stmt.setLong(1, productId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant count: " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Load Color và Size cho ProductVariant
     */
    private void loadColorAndSize(ProductVariant variant, Connection conn) {
        if (variant.getColorId() > 0) {
            Color color = colorDAO.getByIdwithConn(variant.getColorId(), conn);
            variant.setColor(color);
        }
        
        if (variant.getSizeId() > 0) {
            Size size = sizeDAO.getByIdwithConn(variant.getSizeId(), conn);
            variant.setSize(size);
        }
    }
    
    /**
     * Map ResultSet to ProductVariant
     */
    private ProductVariant mapResultSetToProductVariant(ResultSet rs) throws SQLException {
        ProductVariant variant = new ProductVariant();
        variant.setVariantId(rs.getInt("variant_id"));
        variant.setProductId(rs.getLong("product_id"));
        variant.setColorId(rs.getLong("color_id"));
        variant.setSizeId(rs.getInt("size_id"));
        variant.setImageUrl(rs.getString("image_url"));
        variant.setStockQuantity(rs.getInt("stock_quantity"));
        variant.setPrice(rs.getBigDecimal("price"));
        return variant;
    }
    
    public ProductVariant getVariantByIdwithConn(int variantId, Connection conn) {
        String sql = "SELECT pv.*, c.name as color_name, s.name as size_name " +
                            "FROM product_variants pv " +
                            "LEFT JOIN colors c ON pv.color_id = c.color_id " +
                            "LEFT JOIN sizes s ON pv.size_id = s.size_id " +
                            "WHERE pv.variant_id = ? ORDER BY pv.variant_id";
        
        try (
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, variantId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ProductVariant variant = mapResultSetToProductVariant(rs);  
                    loadColorAndSize(variant, conn);
                    return variant;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting variant by ID: " + e.getMessage());
        }
        return null;
    }
    public boolean updateStock(int variantId, int quantity, Connection conn) throws SQLException {
        String sql = "UPDATE product_variants SET stock_quantity = stock_quantity + ?, updated_at = NOW() WHERE variant_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, variantId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock for variant ID " + variantId + ": " + e.getMessage());
            throw e;
        }
    }
}

