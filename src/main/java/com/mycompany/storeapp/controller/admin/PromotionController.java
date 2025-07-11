package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.PromotionDAO;
import com.mycompany.storeapp.model.entity.Promotion;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Hi
 */
public class PromotionController {
    private final PromotionDAO promotionDAO;
    
    public PromotionController() {
        var dbConnection = new DatabaseConnection();
        this.promotionDAO = new PromotionDAO(dbConnection);
    }
    
    public PromotionController(DatabaseConnection dbConnection) {
        this.promotionDAO = new PromotionDAO(dbConnection);
    }
    
    public boolean createPromotion(Promotion promotion) {
        try {
            // Validate required fields
            if (promotion.getCode() == null || promotion.getCode().trim().isEmpty()) {
                showErrorMessage("Mã khuyến mãi không được để trống!");
                return false;
            }
            if (promotion.getName() == null || promotion.getName().trim().isEmpty()) {
                showErrorMessage("Tên khuyến mãi không được để trống!");
                return false;
            }
            
            // Validate field lengths
            if (promotion.getCode().trim().length() > 50) {
                showErrorMessage("Mã khuyến mãi không được vượt quá 50 ký tự!");
                return false;
            }
            if (promotion.getName().trim().length() > 200) {
                showErrorMessage("Tên khuyến mãi không được vượt quá 200 ký tự!");
                return false;
            }
            if (promotion.getDescription() != null && promotion.getDescription().length() > 1000) {
                showErrorMessage("Mô tả không được vượt quá 1000 ký tự!");
                return false;
            }
            
            // Validate discount type
            if (promotion.getDiscountType() == null || promotion.getDiscountType().trim().isEmpty()) {
                showErrorMessage("Loại giảm giá không được để trống!");
                return false;
            }
            if (!promotion.getDiscountType().equals("percent") && !promotion.getDiscountType().equals("fixed")) {
                showErrorMessage("Loại giảm giá phải là 'percent' hoặc 'fixed'!");
                return false;
            }
            
            // Validate discount value
            if (promotion.getDiscountValue() <= 0) {
                showErrorMessage("Giá trị giảm giá phải lớn hơn 0!");
                return false;
            }
            if (promotion.getDiscountType().equals("percent") && promotion.getDiscountValue() > 100) {
                showErrorMessage("Giá trị giảm giá theo phần trăm không được vượt quá 100!");
                return false;
            }
            
            // Validate min order value
            if (promotion.getMinOrderValue() < 0) {
                showErrorMessage("Giá trị đơn hàng tối thiểu không được âm!");
                return false;
            }
            
            // Validate max discount
            if (promotion.getMaxDiscount() < 0) {
                showErrorMessage("Giá trị giảm giá tối đa không được âm!");
                return false;
            }
            
            // Validate usage limit
            if (promotion.getUsageLimit() < 0) {
                showErrorMessage("Giới hạn sử dụng không được âm!");
                return false;
            }
            
            // Validate date range
            if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
                if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
                    showErrorMessage("Ngày bắt đầu phải trước ngày kết thúc!");
                    return false;
                }
            }
            
            // Check if promotion code already exists
            if (promotionDAO.getByCode(promotion.getCode()) != null) {
                showErrorMessage("Mã khuyến mãi đã tồn tại!");
                return false;
            }
            
            // Set default values
            if (promotion.getUsedCount() == 0) {
                promotion.setUsedCount(0);
            }
            
            boolean result = promotionDAO.create(promotion);
            if (result) {
                showSuccessMessage("Tạo khuyến mãi thành công!");
            } else {
                showErrorMessage("Không thể tạo khuyến mãi. Vui lòng thử lại!");
            }
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    public List<Promotion> getAllPromotions() {
        try {
            return promotionDAO.getAll();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách khuyến mãi: " + e.getMessage());
            return null;
        }
    }

    public Promotion getPromotionById(int promotionId) {
        try {
            if (promotionId <= 0) {
                showErrorMessage("ID khuyến mãi không hợp lệ!");
                return null;
            }
            return promotionDAO.getById(promotionId);
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy thông tin khuyến mãi: " + e.getMessage());
            return null;
        }
    }

    public boolean updatePromotion(Promotion promotion) {
        try {
            if (promotion.getPromotionId() <= 0) {
                showErrorMessage("ID khuyến mãi không hợp lệ!");
                return false;
            }
            
            // Validate required fields
            if (promotion.getCode() == null || promotion.getCode().trim().isEmpty()) {
                showErrorMessage("Mã khuyến mãi không được để trống!");
                return false;
            }
            if (promotion.getName() == null || promotion.getName().trim().isEmpty()) {
                showErrorMessage("Tên khuyến mãi không được để trống!");
                return false;
            }
            
            // Validate field lengths
            if (promotion.getCode().trim().length() > 50) {
                showErrorMessage("Mã khuyến mãi không được vượt quá 50 ký tự!");
                return false;
            }
            if (promotion.getName().trim().length() > 200) {
                showErrorMessage("Tên khuyến mãi không được vượt quá 200 ký tự!");
                return false;
            }
            if (promotion.getDescription() != null && promotion.getDescription().length() > 1000) {
                showErrorMessage("Mô tả không được vượt quá 1000 ký tự!");
                return false;
            }
            
            // Validate discount type
            if (promotion.getDiscountType() == null || promotion.getDiscountType().trim().isEmpty()) {
                showErrorMessage("Loại giảm giá không được để trống!");
                return false;
            }
            if (!promotion.getDiscountType().equals("percent") && !promotion.getDiscountType().equals("fixed")) {
                showErrorMessage("Loại giảm giá phải là 'percent' hoặc 'fixed'!");
                return false;
            }
            
            // Validate discount value
            if (promotion.getDiscountValue() <= 0) {
                showErrorMessage("Giá trị giảm giá phải lớn hơn 0!");
                return false;
            }
            if (promotion.getDiscountType().equals("percent") && promotion.getDiscountValue() > 100) {
                showErrorMessage("Giá trị giảm giá theo phần trăm không được vượt quá 100!");
                return false;
            }
            
            // Validate min order value
            if (promotion.getMinOrderValue() < 0) {
                showErrorMessage("Giá trị đơn hàng tối thiểu không được âm!");
                return false;
            }
            
            // Validate max discount
            if (promotion.getMaxDiscount() < 0) {
                showErrorMessage("Giá trị giảm giá tối đa không được âm!");
                return false;
            }
            
            // Validate usage limit
            if (promotion.getUsageLimit() < 0) {
                showErrorMessage("Giới hạn sử dụng không được âm!");
                return false;
            }
            
            // Validate date range
            if (promotion.getStartDate() != null && promotion.getEndDate() != null) {
                if (promotion.getStartDate().isAfter(promotion.getEndDate())) {
                    showErrorMessage("Ngày bắt đầu phải trước ngày kết thúc!");
                    return false;
                }
            }
            
            // Check if promotion code already exists for other promotions
            Promotion existingPromotion = promotionDAO.getByCode(promotion.getCode());
            if (existingPromotion != null && existingPromotion.getPromotionId() != promotion.getPromotionId()) {
                showErrorMessage("Mã khuyến mãi đã tồn tại!");
                return false;
            }
            
            boolean result = promotionDAO.update(promotion);
            if (result) {
                showSuccessMessage("Cập nhật khuyến mãi thành công!");
            } else {
                showErrorMessage("Không thể cập nhật khuyến mãi. Vui lòng thử lại!");
            }
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePromotion(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID khuyến mãi không hợp lệ!");
                return false;
            }
            
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Bạn có chắc chắn muốn xóa khuyến mãi này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean result = promotionDAO.delete(id);
                if (result) {
                    showSuccessMessage("Xóa khuyến mãi thành công!");
                } else {
                    showErrorMessage("Không thể xóa khuyến mãi!");
                }
                return result;
            }
            return false;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa khuyến mãi: " + e.getMessage());
            return false;
        }
    }
    
    public Promotion getPromotionByCode(String code) {
        try {
            if (code == null || code.trim().isEmpty()) {
                showErrorMessage("Mã khuyến mãi không được để trống!");
                return null;
            }
            return promotionDAO.getByCode(code);
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tìm khuyến mãi: " + e.getMessage());
            return null;
        }
    }
    
    public List<Promotion> getActivePromotions() {
        try {
            return promotionDAO.getActivePromotions();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách khuyến mãi đang hoạt động: " + e.getMessage());
            return null;
        }
    }
    
    public boolean isPromotionValid(Promotion promotion) {
        if (promotion == null) {
            return false;
        }
        
        // Check if promotion is active
        if (!promotion.isActive()) {
            return false;
        }
        
        // Check date range
        LocalDateTime now = LocalDateTime.now();
        if (promotion.getStartDate() != null && now.isBefore(promotion.getStartDate())) {
            return false;
        }
        if (promotion.getEndDate() != null && now.isAfter(promotion.getEndDate())) {
            return false;
        }
        
        // Check usage limit
        if (promotion.getUsageLimit() > 0 && promotion.getUsedCount() >= promotion.getUsageLimit()) {
            return false;
        }
        
        return true;
    }
    
    public double calculateDiscount(Promotion promotion, double orderValue) {
        if (!isPromotionValid(promotion)) {
            return 0.0;
        }
        
        if (orderValue < promotion.getMinOrderValue()) {
            return 0.0;
        }
        
        double discount = 0.0;
        
        if (promotion.getDiscountType().equals("percent")) {
            discount = orderValue * (promotion.getDiscountValue() / 100);
        } else if (promotion.getDiscountType().equals("fixed")) {
            discount = promotion.getDiscountValue();
        }
        
        // Apply max discount limit
        if (promotion.getMaxDiscount() > 0 && discount > promotion.getMaxDiscount()) {
            discount = promotion.getMaxDiscount();
        }
        
        return discount;
    }
    
    public boolean activatePromotion(int promotionId) {
        try {
            Promotion promotion = promotionDAO.getById(promotionId);
            if (promotion == null) {
                showErrorMessage("Không tìm thấy khuyến mãi!");
                return false;
            }
            
            promotion.setActive(true);
            boolean result = promotionDAO.update(promotion);
            if (result) {
                showSuccessMessage("Kích hoạt khuyến mãi thành công!");
            } else {
                showErrorMessage("Không thể kích hoạt khuyến mãi!");
            }
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi kích hoạt khuyến mãi: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deactivatePromotion(int promotionId) {
        try {
            Promotion promotion = promotionDAO.getById(promotionId);
            if (promotion == null) {
                showErrorMessage("Không tìm thấy khuyến mãi!");
                return false;
            }
            
            promotion.setActive(false);
            boolean result = promotionDAO.update(promotion);
            if (result) {
                showSuccessMessage("Hủy kích hoạt khuyến mãi thành công!");
            } else {
                showErrorMessage("Không thể hủy kích hoạt khuyến mãi!");
            }
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi hủy kích hoạt khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}