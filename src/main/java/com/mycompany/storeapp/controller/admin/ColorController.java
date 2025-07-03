/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.ColorDAO;
import com.mycompany.storeapp.model.entity.Color;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author ADMIN
 */
public class ColorController {
    private final ColorDAO colorDAO;
    
    public ColorController() {
        var dbConnection = new DatabaseConnection();
        this.colorDAO = new ColorDAO(dbConnection);
    }
    
    public ColorController(DatabaseConnection dbConnection) {
        this.colorDAO = new ColorDAO(dbConnection);
    }
    
    public boolean createColor(Color color) {
        try {
            if (color.getName() == null || color.getName().trim().isEmpty()) {
                showErrorMessage("Tên màu không được để trống!");
                return false;
            }
            if (color.getName().trim().length() > 100) {
                showErrorMessage("Tên màu không được vượt quá 100 ký tự!");
                return false;
            }
            if (color.getDescription() != null && color.getDescription().length() > 500) {
                showErrorMessage("Mô tả không được vượt quá 500 ký tự!");
                return false;
            }
            color.setCreated_at(new Date());
            color.setUpdated_at(new Date());
            boolean result = colorDAO.create(color);
            if (result) showSuccessMessage("Tạo màu thành công!");
            else showErrorMessage("Không thể tạo màu. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo màu: " + e.getMessage());
            return false;
        }
    }

    public List<Color> getAllColors() {
        try {
            return colorDAO.getAll();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách màu: " + e.getMessage());
            return null;
        }
    }

    public boolean updateColor(Color color) {
        try {
            if (color.getColorId() <= 0) {
                showErrorMessage("ID màu không hợp lệ!");
                return false;
            }
            if (color.getName() == null || color.getName().trim().isEmpty()) {
                showErrorMessage("Tên màu không được để trống!");
                return false;
            }
            if (color.getName().trim().length() > 100) {
                showErrorMessage("Tên màu không được vượt quá 100 ký tự!");
                return false;
            }
            if (color.getDescription() != null && color.getDescription().length() > 500) {
                showErrorMessage("Mô tả không được vượt quá 500 ký tự!");
                return false;
            }
            color.setUpdated_at(new Date());
            boolean result = colorDAO.update(color);
            if (result) showSuccessMessage("Cập nhật màu thành công!");
            else showErrorMessage("Không thể cập nhật màu. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật màu: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteColor(long id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID màu không hợp lệ!");
                return false;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa màu này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean result = colorDAO.delete(id);
                if (result) showSuccessMessage("Xóa màu thành công!");
                else showErrorMessage("Không thể xóa màu!");
                return result;
            }
            return false;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa màu: " + e.getMessage());
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