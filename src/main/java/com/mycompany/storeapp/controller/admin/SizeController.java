package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.SizeDAO;
import com.mycompany.storeapp.model.entity.Size;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class SizeController {
    private final SizeDAO sizeDAO;
    
    public SizeController() {
        var dbConnection = new DatabaseConnection();
        this.sizeDAO = new SizeDAO(dbConnection);
    }
    
    public SizeController(DatabaseConnection dbConnection) {
        this.sizeDAO = new SizeDAO(dbConnection);
    }
    
    public boolean createSize(Size size) {
        try {
            if (size.getName() == null || size.getName().trim().isEmpty()) {
                showErrorMessage("Tên kích cỡ không được để trống!");
                return false;
            }
            if (size.getName().trim().length() > 100) {
                showErrorMessage("Tên kích cỡ không được vượt quá 100 ký tự!");
                return false;
            }
            if (size.getDescription() != null && size.getDescription().length() > 500) {
                showErrorMessage("Mô tả không được vượt quá 500 ký tự!");
                return false;
            }
            size.setCreated_at(new Date());
            size.setUpdated_at(new Date());
            boolean result = sizeDAO.create(size);
            if (result) showSuccessMessage("Tạo kích cỡ thành công!");
            else showErrorMessage("Không thể tạo kích cỡ. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo kích cỡ: " + e.getMessage());
            return false;
        }
    }

    public List<Size> getAllSizes() {
        try {
            return sizeDAO.getAll();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách kích cỡ: " + e.getMessage());
            return null;
        }
    }

    public Size getSizeById(int sizeId) {
        try {
            if (sizeId <= 0) {
                showErrorMessage("ID kích cỡ không hợp lệ!");
                return null;
            }
            return sizeDAO.getById(sizeId);
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy thông tin kích cỡ: " + e.getMessage());
            return null;
        }
    }

    public boolean updateSize(Size size) {
        try {
            if (size.getSizeId() <= 0) {
                showErrorMessage("ID kích cỡ không hợp lệ!");
                return false;
            }
            if (size.getName() == null || size.getName().trim().isEmpty()) {
                showErrorMessage("Tên kích cỡ không được để trống!");
                return false;
            }
            if (size.getName().trim().length() > 100) {
                showErrorMessage("Tên kích cỡ không được vượt quá 100 ký tự!");
                return false;
            }
            if (size.getDescription() != null && size.getDescription().length() > 500) {
                showErrorMessage("Mô tả không được vượt quá 500 ký tự!");
                return false;
            }
            size.setUpdated_at(new Date());
            boolean result = sizeDAO.update(size);
            if (result) showSuccessMessage("Cập nhật kích cỡ thành công!");
            else showErrorMessage("Không thể cập nhật kích cỡ. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật kích cỡ: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSize(long id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID kích cỡ không hợp lệ!");
                return false;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa kích cỡ này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean result = sizeDAO.delete(id);
                if (result) showSuccessMessage("Xóa kích cỡ thành công!");
                else showErrorMessage("Không thể xóa kích cỡ!");
                return result;
            }
            return false;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa kích cỡ: " + e.getMessage());
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