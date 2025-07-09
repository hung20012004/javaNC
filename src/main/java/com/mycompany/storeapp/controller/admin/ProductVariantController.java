package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.ProductVariantDAO;
import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.JOptionPane;

public class ProductVariantController {
    private final ProductVariantDAO variantDAO;

    public ProductVariantController() {
        var dbConnection = new DatabaseConnection();
        this.variantDAO = new ProductVariantDAO(dbConnection);
    }

    public ProductVariantController(DatabaseConnection dbConnection) {
        this.variantDAO = new ProductVariantDAO(dbConnection);
    }

    public ProductVariant getVariantByProductColorSize(long productId, long colorId, int sizeId) {
        try {
            if (productId <= 0 || colorId <= 0 || sizeId <= 0) {
                JOptionPane.showMessageDialog(null, 
                    "ID sản phẩm, màu sắc hoặc kích thước không hợp lệ!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return variantDAO.getByProductColorSize(productId, colorId, sizeId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Lỗi khi lấy biến thể: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void updateStockQuantity(int variantId, int stockQuantity) {
        try {
            if (variantId <= 0) {
                JOptionPane.showMessageDialog(null, 
                    "ID biến thể không hợp lệ!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (stockQuantity < 0) {
                JOptionPane.showMessageDialog(null, 
                    "Số lượng tồn kho không hợp lệ!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            variantDAO.updateStockQuantity(variantId, stockQuantity);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Lỗi khi cập nhật tồn kho: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}