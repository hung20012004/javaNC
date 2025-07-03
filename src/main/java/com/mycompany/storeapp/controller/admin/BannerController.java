/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.BannerDAO;
import com.mycompany.storeapp.model.entity.Banner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Controller cho quản lý banner
 * @author ADMIN
 */
public class BannerController {
    private final BannerDAO bannerDAO;
    
    public BannerController() {
        var dbConnection = new DatabaseConnection();
        this.bannerDAO = new BannerDAO(dbConnection);
    }
    
    public BannerController(DatabaseConnection dbConnection) {
        this.bannerDAO = new BannerDAO(dbConnection);
    }
    
    public boolean createBanner(Banner banner) {
        try {
            if (banner.getTitle() == null || banner.getTitle().trim().isEmpty()) {
                showErrorMessage("Tiêu đề banner không được để trống!");
                return false;
            }
            if (banner.getTitle().trim().length() > 100) {
                showErrorMessage("Tiêu đề banner không được vượt quá 100 ký tự!");
                return false;
            }
            if (banner.getSubtitle() != null && banner.getSubtitle().length() > 200) {
                showErrorMessage("Phụ đề không được vượt quá 200 ký tự!");
                return false;
            }
            if (banner.getButtonText() != null && banner.getButtonText().length() > 50) {
                showErrorMessage("Văn bản nút không được vượt quá 50 ký tự!");
                return false;
            }
            if (banner.getButtonLink() != null && banner.getButtonLink().length() > 255) {
                showErrorMessage("Liên kết nút không được vượt quá 255 ký tự!");
                return false;
            }
            try {
                Integer.parseInt(String.valueOf(banner.getOrderSequence()));
            } catch (NumberFormatException e) {
                showErrorMessage("Thứ tự phải là số nguyên!");
                return false;
            }
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            if (banner.getStartDate() != null && !dateFormat.format(banner.getStartDate()).equals(dateFormat.format(new Date()))) {
//                showErrorMessage("Ngày bắt đầu phải là ngày hiện tại hoặc trong tương lai!");
//                return false;
//            }
//            if (banner.getEndDate() != null && banner.getStartDate() != null && banner.getEndDate().before(banner.getStartDate())) {
//                showErrorMessage("Ngày kết thúc phải sau ngày bắt đầu!");
//                return false;
//            }
            banner.setCreated_at(new Date());
            banner.setUpdated_at(new Date());
            boolean result = bannerDAO.create(banner);
            if (result) showSuccessMessage("Tạo banner thành công!");
            else showErrorMessage("Không thể tạo banner. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo banner: " + e.getMessage());
            return false;
        }
    }

    public Banner getBannerById(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID banner không hợp lệ!");
                return null;
            }
            Banner banner = bannerDAO.getById(id);
            if (banner == null) {
                showInfoMessage("Không tìm thấy banner với ID: " + id);
            }
            return banner;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy thông tin banner: " + e.getMessage());
            return null;
        }
    }

    public List<Banner> getAllBanners() {
        try {
            List<Banner> banners = bannerDAO.getAll();
            if (banners.isEmpty()) {
                showInfoMessage("Chưa có banner nào trong hệ thống!");
            }
            return banners;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách banner: " + e.getMessage());
            return null;
        }
    }

    public boolean updateBanner(Banner banner) {
        try {
            if (banner.getBannerId() <= 0) {
                showErrorMessage("ID banner không hợp lệ!");
                return false;
            }
            if (banner.getTitle() == null || banner.getTitle().trim().isEmpty()) {
                showErrorMessage("Tiêu đề banner không được để trống!");
                return false;
            }
            if (banner.getTitle().trim().length() > 100) {
                showErrorMessage("Tiêu đề banner không được vượt quá 100 ký tự!");
                return false;
            }
            if (banner.getSubtitle() != null && banner.getSubtitle().length() > 200) {
                showErrorMessage("Phụ đề không được vượt quá 200 ký tự!");
                return false;
            }
            if (banner.getButtonText() != null && banner.getButtonText().length() > 50) {
                showErrorMessage("Văn bản nút không được vượt quá 50 ký tự!");
                return false;
            }
            if (banner.getButtonLink() != null && banner.getButtonLink().length() > 255) {
                showErrorMessage("Liên kết nút không được vượt quá 255 ký tự!");
                return false;
            }
            try {
                Integer.parseInt(String.valueOf(banner.getOrderSequence()));
            } catch (NumberFormatException e) {
                showErrorMessage("Thứ tự phải là số nguyên!");
                return false;
            }
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            if (banner.getStartDate() != null && !dateFormat.format(banner.getStartDate()).equals(dateFormat.format(new Date()))) {
//                showErrorMessage("Ngày bắt đầu phải là ngày hiện tại hoặc trong tương lai!");
//                return false;
//            }
//            if (banner.getEndDate() != null && banner.getStartDate() != null && banner.getEndDate().before(banner.getStartDate())) {
//                showErrorMessage("Ngày kết thúc phải sau ngày bắt đầu!");
//                return false;
//            }
            banner.setUpdated_at(new Date());
            boolean result = bannerDAO.update(banner);
            if (result) showSuccessMessage("Cập nhật banner thành công!");
            else showErrorMessage("Không thể cập nhật banner. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật banner: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBanner(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID banner không hợp lệ!");
                return false;
            }
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc chắn muốn xóa banner này?\nHành động này không thể hoàn tác!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return false;
            }
            boolean result = bannerDAO.delete(id);
            if (result) showSuccessMessage("Xóa banner thành công!");
            else showErrorMessage("Không thể xóa banner. Có thể banner đang được sử dụng!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa banner: " + e.getMessage());
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