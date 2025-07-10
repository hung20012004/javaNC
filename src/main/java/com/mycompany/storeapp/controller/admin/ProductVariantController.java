/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.ProductVariantDAO;
import com.mycompany.storeapp.model.entity.ProductVariant;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Hi
 */
public class ProductVariantController {
    private final ProductVariantDAO productVariantDAO;
    
    public ProductVariantController() {
        this.productVariantDAO = new ProductVariantDAO(new DatabaseConnection());
    }
    
    /**
     * Lấy danh sách tất cả ProductVariant
     */
    public List<ProductVariant> getAllVariants() {
        try {
            List<ProductVariant> variants = productVariantDAO.getAllVariants();
            if (variants.isEmpty()) {
                showInfoMessage("Chưa có chi tiết sản phẩm nào trong hệ thống!");
            }
            return variants;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách chi tiết sản phẩm: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy danh sách ProductVariant theo Product ID
     */
    public List<ProductVariant> getVariantsByProductId(long productId) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return new ArrayList<>();
            }
            
            List<ProductVariant> variants = productVariantDAO.getVariantByProductId(productId);
            if (variants.isEmpty()) {
                System.out.println("Chưa có chi tiết sản phẩm nào cho sản phẩm: " + productId);
            }
            return variants;
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy danh sách chi tiết sản phẩm: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy ProductVariant theo ID
     */
    public ProductVariant getVariantById(int variantId) {
        try {
            if (variantId <= 0) {
                showErrorMessage("ID chi tiết sản phẩm không hợp lệ!");
                return null;
            }
            
            ProductVariant variant = productVariantDAO.getVariantById(variantId);
            if (variant == null) {
                System.out.println("Không tìm thấy chi tiết sản phẩm với ID: " + variantId);
            }
            return variant;
        } catch (Exception e) {
            System.out.println("Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy ProductVariant theo Product ID, Color ID và Size ID
     */
    public ProductVariant getVariantByProductColorSize(long productId, int colorId, int sizeId) {
        try {
            if (productId <= 0 || colorId <= 0 || sizeId <= 0) {
                showErrorMessage("Thông tin sản phẩm, màu sắc hoặc kích cỡ không hợp lệ!");
                return null;
            }
            
            ProductVariant variant = productVariantDAO.getVariantByProductColorSize(productId, colorId, sizeId);
            if (variant == null) {
                showInfoMessage("Không tìm thấy chi tiết sản phẩm với thông tin đã cho!");
            }
            return variant;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tìm kiếm chi tiết sản phẩm: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy danh sách ProductVariant có tồn kho
     */
    public List<ProductVariant> getVariantsInStock(long productId) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return new ArrayList<>();
            }
            
            List<ProductVariant> variants = productVariantDAO.getVariantsInStock(productId);
            if (variants.isEmpty()) {
                showInfoMessage("Không có chi tiết sản phẩm nào còn hàng!");
            }
            return variants;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách chi tiết sản phẩm còn hàng: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Tạo ProductVariant mới
     */
    public boolean createVariant(ProductVariant variant) {
        try {
            if (variant == null) {
                showErrorMessage("Thông tin chi tiết sản phẩm không được để trống!");
                return false;
            }
            
            if (!validateVariant(variant)) {
                return false;
            }
            
            // Kiểm tra xem variant đã tồn tại chưa
            ProductVariant existingVariant = productVariantDAO.getVariantByProductColorSize(
                variant.getProductId(), variant.getColorId(), variant.getSizeId());
            
            if (existingVariant != null) {
                showErrorMessage("Chi tiết sản phẩm với màu sắc và kích cỡ này đã tồn tại!");
                return false;
            }
            
            boolean success = productVariantDAO.addVariant(variant);
            if (success) {
                showSuccessMessage("Thêm chi tiết sản phẩm thành công!");
            } else {
                showErrorMessage("Thêm chi tiết sản phẩm thất bại!");
            }
            return success;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi thêm chi tiết sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật ProductVariant
     */
    public boolean updateVariant(ProductVariant variant) {
        try {
            if (variant == null) {
                showErrorMessage("Thông tin chi tiết sản phẩm không được để trống!");
                return false;
            }
            
            if (variant.getVariantId() <= 0) {
                showErrorMessage("ID chi tiết sản phẩm không hợp lệ!");
                return false;
            }
            
            if (!validateVariant(variant)) {
                return false;
            }
            
            // Kiểm tra xem variant có tồn tại không
            ProductVariant existingVariant = productVariantDAO.getVariantById(variant.getVariantId());
            if (existingVariant == null) {
                showErrorMessage("Chi tiết sản phẩm không tồn tại!");
                return false;
            }
            
            // Kiểm tra xem có variant khác với cùng product_id, color_id, size_id không
            ProductVariant duplicateVariant = productVariantDAO.getVariantByProductColorSize(
                variant.getProductId(), variant.getColorId(), variant.getSizeId());
            
            if (duplicateVariant != null && duplicateVariant.getVariantId() != variant.getVariantId()) {
                showErrorMessage("Đã tồn tại chi tiết sản phẩm khác với màu sắc và kích cỡ này!");
                return false;
            }
            
            boolean success = productVariantDAO.updateVariant(variant);
            if (success) {
                showSuccessMessage("Cập nhật chi tiết sản phẩm thành công!");
            } else {
                showErrorMessage("Cập nhật chi tiết sản phẩm thất bại!");
            }
            return success;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật chi tiết sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa ProductVariant
     */
    public boolean deleteVariant(int variantId) {
        try {
            if (variantId <= 0) {
                showErrorMessage("ID chi tiết sản phẩm không hợp lệ!");
                return false;
            }
            
            // Kiểm tra xem variant có tồn tại không
            ProductVariant variant = productVariantDAO.getVariantById(variantId);
            if (variant == null) {
                showErrorMessage("Chi tiết sản phẩm không tồn tại!");
                return false;
            }
            
            // Xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Bạn có chắc chắn muốn xóa chi tiết sản phẩm này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            
            boolean success = productVariantDAO.deleteVariant(variantId);
            if (success) {
                showSuccessMessage("Xóa chi tiết sản phẩm thành công!");
            } else {
                showErrorMessage("Xóa chi tiết sản phẩm thất bại!");
            }
            return success;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa chi tiết sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật số lượng tồn kho
     */
    public boolean updateStockQuantity(int variantId, int newQuantity) {
        try {
            if (variantId <= 0) {
                showErrorMessage("ID chi tiết sản phẩm không hợp lệ!");
                return false;
            }
            
            if (newQuantity < 0) {
                showErrorMessage("Số lượng tồn kho không được âm!");
                return false;
            }
            
            // Kiểm tra xem variant có tồn tại không
            ProductVariant variant = productVariantDAO.getVariantById(variantId);
            if (variant == null) {
                showErrorMessage("Chi tiết sản phẩm không tồn tại!");
                return false;
            }
            
            boolean success = productVariantDAO.updateStockQuantity(variantId, newQuantity);
            if (success) {
                showSuccessMessage("Cập nhật số lượng tồn kho thành công!");
            } else {
                showErrorMessage("Cập nhật số lượng tồn kho thất bại!");
            }
            return success;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật số lượng tồn kho: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Đếm số lượng variant theo product
     */
    public int getVariantCountByProduct(long productId) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return 0;
            }
            
            return productVariantDAO.getVariantCountByProduct(productId);
        } catch (Exception e) {
            showErrorMessage("Lỗi khi đếm số lượng chi tiết sản phẩm: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Xóa tất cả variants của một product
     */
    public boolean deleteVariantsByProductId(long productId) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return false;
            }
            
            List<ProductVariant> variants = productVariantDAO.getVariantByProductId(productId);
            if (variants.isEmpty()) {
                showInfoMessage("Không có chi tiết sản phẩm nào để xóa!");
                return true;
            }
            
            // Xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Bạn có chắc chắn muốn xóa tất cả " + variants.size() + " chi tiết sản phẩm?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            
            boolean allSuccess = true;
            int deletedCount = 0;
            
            for (ProductVariant variant : variants) {
                if (productVariantDAO.deleteVariant(variant.getVariantId())) {
                    deletedCount++;
                } else {
                    allSuccess = false;
                }
            }
            
            if (allSuccess) {
                showSuccessMessage("Xóa tất cả " + deletedCount + " chi tiết sản phẩm thành công!");
            } else {
                showErrorMessage("Chỉ xóa được " + deletedCount + "/" + variants.size() + " chi tiết sản phẩm!");
            }
            
            return allSuccess;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa chi tiết sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lưu danh sách variants (thêm mới hoặc cập nhật)
     */
    public boolean saveVariants(List<ProductVariant> variants) {
        if (variants == null || variants.isEmpty()) {
            showInfoMessage("Không có chi tiết sản phẩm nào để lưu!");
            return true;
        }
        
        boolean allSuccess = true;
        int successCount = 0;
        
        for (ProductVariant variant : variants) {
            boolean success;
            if (variant.getVariantId() == 0) {
                // Thêm mới
                success = createVariant(variant);
            } else {
                // Cập nhật
                success = updateVariant(variant);
            }
            
            if (success) {
                successCount++;
            } else {
                allSuccess = false;
            }
        }
        
        if (allSuccess) {
            showSuccessMessage("Lưu tất cả " + successCount + " chi tiết sản phẩm thành công!");
        } else {
            showErrorMessage("Chỉ lưu được " + successCount + "/" + variants.size() + " chi tiết sản phẩm!");
        }
        
        return allSuccess;
    }
    
    /**
     * Kiểm tra tính hợp lệ của ProductVariant
     */
    private boolean validateVariant(ProductVariant variant) {
        if (variant.getProductId() <= 0) {
            showErrorMessage("ID sản phẩm không hợp lệ!");
            return false;
        }
        
        if (variant.getColorId() <= 0) {
            showErrorMessage("ID màu sắc không hợp lệ!");
            return false;
        }
        
        if (variant.getSizeId() <= 0) {
            showErrorMessage("ID kích cỡ không hợp lệ!");
            return false;
        }
        
        if (variant.getStockQuantity() < 0) {
            showErrorMessage("Số lượng tồn kho không được âm!");
            return false;
        }
        
        if (variant.getPrice() == null || variant.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            showErrorMessage("Giá sản phẩm không hợp lệ!");
            return false;
        }
        
        return true;
    }
    
    /**
     * Kiểm tra xem có variant nào đang có tồn kho không
     */
    public boolean hasVariantsInStock(long productId) {
        try {
            List<ProductVariant> variants = getVariantsInStock(productId);
            return !variants.isEmpty();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi kiểm tra tồn kho: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tính tổng tồn kho của tất cả variants
     */
    public int getTotalStockByProduct(long productId) {
        try {
            List<ProductVariant> variants = getVariantsByProductId(productId);
            return variants.stream()
                    .mapToInt(ProductVariant::getStockQuantity)
                    .sum();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tính tổng tồn kho: " + e.getMessage());
            return 0;
        }
    }
    
    // Các phương thức hiển thị thông báo
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Thông tin", JOptionPane.INFORMATION_MESSAGE);
    }
}