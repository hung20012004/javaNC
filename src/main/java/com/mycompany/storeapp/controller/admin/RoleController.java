package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.RoleDAO;
import com.mycompany.storeapp.model.entity.Role;
import java.util.List;
import javax.swing.JOptionPane;

public class RoleController {
    private final RoleDAO roleDAO;

    public RoleController() {
        var dbConnection = new DatabaseConnection();
        this.roleDAO = new RoleDAO(dbConnection);
    }

    public RoleController(DatabaseConnection dbConnection) {
        this.roleDAO = new RoleDAO(dbConnection);
    }

    public boolean createRole(Role role) {
        try {
            if (role.getName() == null || role.getName().trim().isEmpty()) {
                showErrorMessage("Tên vai trò không được để trống!");
                return false;
            }
            if (role.getName().trim().length() > 50) {
                showErrorMessage("Tên vai trò không được vượt quá 50 ký tự!");
                return false;
            }
            boolean result = roleDAO.create(role);
            if (result) showSuccessMessage("Tạo vai trò thành công!");
            else showErrorMessage("Không thể tạo vai trò. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo vai trò: " + e.getMessage());
            return false;
        }
    }

    public List<Role> getAllRoles() {
        try {
            return roleDAO.getAll();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách vai trò: " + e.getMessage());
            return null;
        }
    }

    public boolean updateRole(Role role) {
        try {
            if (role.getRoleId() <= 0) {
                showErrorMessage("ID vai trò không hợp lệ!");
                return false;
            }
            if (role.getName() == null || role.getName().trim().isEmpty()) {
                showErrorMessage("Tên vai trò không được để trống!");
                return false;
            }
            if (role.getName().trim().length() > 50) {
                showErrorMessage("Tên vai trò không được vượt quá 50 ký tự!");
                return false;
            }
            boolean result = roleDAO.update(role);
            if (result) showSuccessMessage("Cập nhật vai trò thành công!");
            else showErrorMessage("Không thể cập nhật vai trò. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật vai trò: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRole(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID vai trò không hợp lệ!");
                return false;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa vai trò này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean result = roleDAO.delete(id);
                if (result) showSuccessMessage("Xóa vai trò thành công!");
                else showErrorMessage("Không thể xóa vai trò!");
                return result;
            }
            return false;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa vai trò: " + e.getMessage());
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