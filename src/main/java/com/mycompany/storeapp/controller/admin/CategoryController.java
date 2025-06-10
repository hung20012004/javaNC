/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.CategoryDAO;
import com.mycompany.storeapp.model.entity.Category;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Hi
 */
public class CategoryController {
    private final CategoryDAO categoryDAO;
    
    public CategoryController() {
        var dbConnection = new DatabaseConnection();
        this.categoryDAO = new CategoryDAO(dbConnection);
    }
    
    public CategoryController(DatabaseConnection dbConnection) {
        this.categoryDAO = new CategoryDAO(dbConnection);
    }
    
    /**
     * Tạo mới một category
     * @param category Category cần tạo
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean createCategory(Category category) {
        try {
            // Validate dữ liệu trước khi tạo
            if (!validateCategory(category)) {
                return false;
            }
            
            // Tạo slug từ name nếu chưa có
            if (category.getSlug() == null || category.getSlug().trim().isEmpty()) {
                category.setSlug(generateSlug(category.getName()));
            }
            
            boolean result = categoryDAO.create(category);
            if (result) {
                showSuccessMessage("Tạo danh mục thành công!");
            } else {
                showErrorMessage("Không thể tạo danh mục. Vui lòng thử lại!");
            }
            return result;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo danh mục: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy category theo ID
     * @param id ID của category
     * @return Category object hoặc null nếu không tìm thấy
     */
    public Category getCategoryById(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID danh mục không hợp lệ!");
                return null;
            }
            
            Category category = categoryDAO.getById(id);
            if (category == null) {
                showInfoMessage("Không tìm thấy danh mục với ID: " + id);
            }
            return category;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy thông tin danh mục: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy tất cả categories
     * @return List các Category
     */
    public List<Category> getAllCategories() {
        try {
            List<Category> categories = categoryDAO.getAll();
            if (categories.isEmpty()) {
                showInfoMessage("Chưa có danh mục nào trong hệ thống!");
            }
            return categories;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách danh mục: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy các categories đang active
     * @return List các Category active
     */
    public List<Category> getActiveCategories() {
        try {
            List<Category> allCategories = categoryDAO.getAll();
            return allCategories.stream()
                    .filter(Category::getIsActive)
                    .collect(java.util.stream.Collectors.toList());
                    
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách danh mục hoạt động: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Cập nhật category
     * @param category Category cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateCategory(Category category) {
        try {
            // Validate dữ liệu trước khi cập nhật
            if (!validateCategory(category)) {
                return false;
            }
            
            if (category.getCategoryId() <= 0) {
                showErrorMessage("ID danh mục không hợp lệ!");
                return false;
            }
            
            // Cập nhật slug từ name nếu cần
            if (category.getSlug() == null || category.getSlug().trim().isEmpty()) {
                category.setSlug(generateSlug(category.getName()));
            }
            
            boolean result = categoryDAO.update(category);
            if (result) {
                showSuccessMessage("Cập nhật danh mục thành công!");
            } else {
                showErrorMessage("Không thể cập nhật danh mục. Vui lòng thử lại!");
            }
            return result;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật danh mục: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa category
     * @param id ID của category cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean deleteCategory(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID danh mục không hợp lệ!");
                return false;
            }
            
            // Xác nhận trước khi xóa
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn xóa danh mục này?\nHành động này không thể hoàn tác!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            
            boolean result = categoryDAO.delete(id);
            if (result) {
                showSuccessMessage("Xóa danh mục thành công!");
            } else {
                showErrorMessage("Không thể xóa danh mục. Có thể danh mục đang được sử dụng!");
            }
            return result;
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa danh mục: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Thay đổi trạng thái active của category
     * @param id ID của category
     * @param isActive Trạng thái mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean toggleCategoryStatus(int id, boolean isActive) {
        try {
            Category category = getCategoryById(id);
            if (category == null) {
                return false;
            }
            
            category.setIsActive(isActive);
            return updateCategory(category);
            
        } catch (Exception e) {
            showErrorMessage("Lỗi khi thay đổi trạng thái danh mục: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate dữ liệu category
     * @param category Category cần validate
     * @return true nếu hợp lệ, false nếu không hợp lệ
     */
    private boolean validateCategory(Category category) {
        if (category == null) {
            showErrorMessage("Thông tin danh mục không được để trống!");
            return false;
        }
        
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            showErrorMessage("Tên danh mục không được để trống!");
            return false;
        }
        
        if (category.getName().trim().length() > 100) {
            showErrorMessage("Tên danh mục không được vượt quá 100 ký tự!");
            return false;
        }
        
        if (category.getDescription() != null && category.getDescription().length() > 500) {
            showErrorMessage("Mô tả danh mục không được vượt quá 500 ký tự!");
            return false;
        }
        
        return true;
    }
    
    /**
     * Tạo slug từ tên category
     * @param name Tên category
     * @return Slug string
     */
    private String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        
        return name.trim()
                .toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
    
    /**
     * Hiển thị thông báo thành công
     * @param message Nội dung thông báo
     */
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Thành công",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Hiển thị thông báo lỗi
     * @param message Nội dung lỗi
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Lỗi",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Hiển thị thông báo thông tin
     * @param message Nội dung thông tin
     */
    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Thông tin",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
