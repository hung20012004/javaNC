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
    public List<ProductVariant> getVariantsByProductId(long productId) {
         try {
            List<ProductVariant> variants = productVariantDAO.getVariantByProductId(productId);
            if (variants.isEmpty()) {
                showInfoMessage("Chưa có chi tiết sản phẩm nào trong hệ thống!");
            }
            return variants;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách chi tiết sản phẩm: " + e.getMessage());
            return null;
        }
    }
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
