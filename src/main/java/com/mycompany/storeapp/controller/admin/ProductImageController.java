package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.ProductImageDAO;
import com.mycompany.storeapp.model.entity.ProductImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ProductImageController {
    private final ProductImageDAO productImageDAO;

    public ProductImageController() {
        this.productImageDAO = new ProductImageDAO(new DatabaseConnection());
    }

    public List<String> getImageUrlsByProductId(long productId) {
        List<ProductImage> images = productImageDAO.getImagesByProductId(productId);
        List<String> imageUrls = new ArrayList<>();
        for (ProductImage image : images) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }
    public List<ProductImage> getImagesByProductId(long productId) {
        try {
            List<ProductImage> images = productImageDAO.getImagesByProductId(productId);
            if (images.isEmpty()) {
                showInfoMessage("Chưa có hình ảnh sản phẩm nào trong hệ thống!");
            }
            return images;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách hình ảnh sản phẩm: " + e.getMessage());
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