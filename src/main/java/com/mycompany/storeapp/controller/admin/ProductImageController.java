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
                System.err.println("Chưa có hình ảnh sản phẩm nào trong hệ thống!");
            }
            return images;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách hình ảnh sản phẩm: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Tạo mới hình ảnh
     */
    public boolean createImage(ProductImage image) {
        try {
            // Validate input
            if (image == null) {
                showErrorMessage("Thông tin hình ảnh không hợp lệ!");
                return false;
            }
            
            if (image.getImageUrl() == null || image.getImageUrl().trim().isEmpty()) {
                showErrorMessage("URL hình ảnh không được để trống!");
                return false;
            }
            
            if (image.getProductId() <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return false;
            }
            
            // Nếu đây là hình ảnh chính, clear primary flag của các hình ảnh khác
            if (image.isPrimary()) {
                productImageDAO.clearPrimaryFlags(image.getProductId());
            }
            
            boolean success = productImageDAO.createImage(image);
            
            if (success) {
                showSuccessMessage("Thêm hình ảnh thành công!");
                return true;
            } else {
                showErrorMessage("Không thể thêm hình ảnh!");
                return false;
            }
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo hình ảnh: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật hình ảnh
     */
    public boolean updateImage(ProductImage image) {
        try {
            // Validate input
            if (image == null) {
                showErrorMessage("Thông tin hình ảnh không hợp lệ!");
                return false;
            }
            
            if (image.getImageId() <= 0) {
                showErrorMessage("ID hình ảnh không hợp lệ!");
                return false;
            }
            
            if (image.getImageUrl() == null || image.getImageUrl().trim().isEmpty()) {
                showErrorMessage("URL hình ảnh không được để trống!");
                return false;
            }
            
            // Nếu đây là hình ảnh chính, clear primary flag của các hình ảnh khác
            if (image.isPrimary()) {
                productImageDAO.clearPrimaryFlags(image.getProductId());
            }
            
            boolean success = productImageDAO.updateImage(image);
            
            if (success) {
                showSuccessMessage("Cập nhật hình ảnh thành công!");
                return true;
            } else {
                showErrorMessage("Không thể cập nhật hình ảnh!");
                return false;
            }
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật hình ảnh: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa hình ảnh
     */
    public boolean deleteImage(int imageId) {
        try {
            if (imageId <= 0) {
                showErrorMessage("ID hình ảnh không hợp lệ!");
                return false;
            }
            
            // Xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(null,
                "Bạn có chắc chắn muốn xóa hình ảnh này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            
            boolean success = productImageDAO.deleteImage(imageId);
            
            if (success) {
                showSuccessMessage("Xóa hình ảnh thành công!");
                return true;
            } else {
                showErrorMessage("Không thể xóa hình ảnh!");
                return false;
            }
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa hình ảnh: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa tất cả hình ảnh của một sản phẩm
     */
    public boolean deleteImagesByProductId(long productId) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return false;
            }
            
            boolean success = productImageDAO.deleteImagesByProductId(productId);
            
            if (success) {
                showSuccessMessage("Xóa tất cả hình ảnh sản phẩm thành công!");
                return true;
            } else {
                showErrorMessage("Không thể xóa hình ảnh sản phẩm!");
                return false;
            }
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa hình ảnh sản phẩm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy hình ảnh chính của sản phẩm
     */
    public ProductImage getPrimaryImageByProductId(long productId) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return null;
            }
            
            return productImageDAO.getPrimaryImageByProductId(productId);
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy hình ảnh chính: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy hình ảnh theo ID
     */
    public ProductImage getImageById(int imageId) {
        try {
            if (imageId <= 0) {
                showErrorMessage("ID hình ảnh không hợp lệ!");
                return null;
            }
            
            return productImageDAO.getImageById(imageId);
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy hình ảnh: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cập nhật thứ tự hiển thị cho danh sách hình ảnh
     */
    public boolean updateDisplayOrders(List<ProductImage> images) {
        try {
            if (images == null || images.isEmpty()) {
                showInfoMessage("Danh sách hình ảnh trống!");
                return false;
            }
            
            // Validate tất cả hình ảnh có ID hợp lệ
            for (ProductImage image : images) {
                if (image.getImageId() <= 0) {
                    showErrorMessage("Có hình ảnh với ID không hợp lệ!");
                    return false;
                }
            }
            
            boolean success = productImageDAO.updateDisplayOrders(images);
            
            if (success) {
                showSuccessMessage("Cập nhật thứ tự hiển thị thành công!");
                return true;
            } else {
                showErrorMessage("Không thể cập nhật thứ tự hiển thị!");
                return false;
            }
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật thứ tự hiển thị: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Đặt hình ảnh làm ảnh chính
     */
    public boolean setPrimaryImage(int imageId, long productId) {
        try {
            if (imageId <= 0) {
                showErrorMessage("ID hình ảnh không hợp lệ!");
                return false;
            }
            
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return false;
            }
            
            // Clear primary flag của tất cả hình ảnh
            productImageDAO.clearPrimaryFlags(productId);
            
            // Lấy hình ảnh hiện tại
            ProductImage image = productImageDAO.getImageById(imageId);
            if (image == null) {
                showErrorMessage("Không tìm thấy hình ảnh!");
                return false;
            }
            
            // Set primary flag
            image.setPrimary(true);
            
            boolean success = productImageDAO.updateImage(image);
            
            if (success) {
                showSuccessMessage("Đặt ảnh chính thành công!");
                return true;
            } else {
                showErrorMessage("Không thể đặt ảnh chính!");
                return false;
            }
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi đặt ảnh chính: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lưu danh sách hình ảnh cho sản phẩm
     */
    public boolean saveImagesForProduct(long productId, List<ProductImage> images) {
        try {
            if (productId <= 0) {
                showErrorMessage("ID sản phẩm không hợp lệ!");
                return false;
            }
            
            if (images == null) {
                images = new ArrayList<>();
            }
            
            // Xóa tất cả hình ảnh cũ của sản phẩm
            productImageDAO.deleteImagesByProductId(productId);
            
            // Thêm hình ảnh mới
            for (ProductImage image : images) {
                image.setProductId((int) productId);
                if (!productImageDAO.createImage(image)) {
                    showErrorMessage("Không thể lưu hình ảnh: " + image.getImageUrl());
                    return false;
                }
            }
            
            showSuccessMessage("Lưu danh sách hình ảnh thành công!");
            return true;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lưu danh sách hình ảnh: " + e.getMessage());
            return false;
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