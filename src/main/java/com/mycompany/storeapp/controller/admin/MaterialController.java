/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.MaterialDAO;
import com.mycompany.storeapp.model.entity.Material;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author ADMIN
 */
public class MaterialController {
    private final MaterialDAO materialDAO;
    
    public MaterialController() {
        var dbConnection = new DatabaseConnection();
        this.materialDAO = new MaterialDAO(dbConnection);
    }
    
    public MaterialController(DatabaseConnection dbConnection) {
        this.materialDAO = new MaterialDAO(dbConnection);
    }
    
    public boolean createMaterial(Material material) {
        try {
            if (material.getName() == null || material.getName().trim().isEmpty()) {
                showErrorMessage("Tên chất liệu không được để trống!");
                return false;
            }
            if (material.getName().trim().length() > 100) {
                showErrorMessage("Tên chất liệu không được vượt quá 100 ký tự!");
                return false;
            }
            if (material.getDescription() != null && material.getDescription().length() > 500) {
                showErrorMessage("Mô tả không được vượt quá 500 ký tự!");
                return false;
            }
            material.setCreated_at(new Date());
            material.setUpdated_at(new Date());
            boolean result = materialDAO.create(material);
            if (result) showSuccessMessage("Tạo chất liệu thành công!");
            else showErrorMessage("Không thể tạo chất liệu. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo chất liệu: " + e.getMessage());
            return false;
        }
    }

    public List<Material> getAllMaterials() {
        try {
            return materialDAO.getAll();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách chất liệu: " + e.getMessage());
            return null;
        }
    }

    public boolean updateMaterial(Material material) {
        try {
            if (material.getMaterialId() <= 0) {
                showErrorMessage("ID chất liệu không hợp lệ!");
                return false;
            }
            if (material.getName() == null || material.getName().trim().isEmpty()) {
                showErrorMessage("Tên chất liệu không được để trống!");
                return false;
            }
            if (material.getName().trim().length() > 100) {
                showErrorMessage("Tên chất liệu không được vượt quá 100 ký tự!");
                return false;
            }
            if (material.getDescription() != null && material.getDescription().length() > 500) {
                showErrorMessage("Mô tả không được vượt quá 500 ký tự!");
                return false;
            }
            material.setUpdated_at(new Date());
            boolean result = materialDAO.update(material);
            if (result) showSuccessMessage("Cập nhật chất liệu thành công!");
            else showErrorMessage("Không thể cập nhật chất liệu. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật chất liệu: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMaterial(long id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID chất liệu không hợp lệ!");
                return false;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa chất liệu này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean result = materialDAO.delete(id);
                if (result) showSuccessMessage("Xóa chất liệu thành công!");
                else showErrorMessage("Không thể xóa chất liệu!");
                return result;
            }
            return false;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa chất liệu: " + e.getMessage());
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
