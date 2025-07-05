/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;


import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.SupplierDAO;
import com.mycompany.storeapp.model.entity.Supplier;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
/**
 *
 * @author ADMIN
 */
public class SupplierController {
    private final SupplierDAO supplierDAO;

    public SupplierController() {
        var dbConnection = new DatabaseConnection();
        this.supplierDAO = new SupplierDAO(dbConnection);
    }

    public SupplierController(DatabaseConnection dbConnection) {
        this.supplierDAO = new SupplierDAO(dbConnection);
    }

    public boolean createSupplier(Supplier supplier) {
        try {
            if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
                showErrorMessage("Tên nhà cung cấp không được để trống!");
                return false;
            }
            if (supplier.getName().trim().length() > 100) {
                showErrorMessage("Tên nhà cung cấp không được vượt quá 100 ký tự!");
                return false;
            }
            if (supplier.getPhone() != null && !supplier.getPhone().matches("\\d{10}")) {
                showErrorMessage("Số điện thoại phải là 10 chữ số!");
                return false;
            }
            if (supplier.getEmail() != null && !supplier.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showErrorMessage("Email không hợp lệ!");
                return false;
            }
            supplier.setCreatedAt(new Date());
            supplier.setUpdatedAt(new Date());
            boolean result = supplierDAO.create(supplier);
            if (result) showSuccessMessage("Tạo nhà cung cấp thành công!");
            else showErrorMessage("Không thể tạo nhà cung cấp. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi tạo nhà cung cấp: " + e.getMessage());
            return false;
        }
    }

    public List<Supplier> getAllSuppliers() {
        try {
            return supplierDAO.getAll();
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách nhà cung cấp: " + e.getMessage());
            return null;
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        try {
            if (supplier.getSupplierId() <= 0) {
                showErrorMessage("ID nhà cung cấp không hợp lệ!");
                return false;
            }
            if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
                showErrorMessage("Tên nhà cung cấp không được để trống!");
                return false;
            }
            if (supplier.getName().trim().length() > 100) {
                showErrorMessage("Tên nhà cung cấp không được vượt quá 100 ký tự!");
                return false;
            }
            if (supplier.getPhone() != null && !supplier.getPhone().matches("\\d{10}")) {
                showErrorMessage("Số điện thoại phải là 10 chữ số!");
                return false;
            }
            if (supplier.getEmail() != null && !supplier.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showErrorMessage("Email không hợp lệ!");
                return false;
            }
            supplier.setUpdatedAt(new Date());
            boolean result = supplierDAO.update(supplier);
            if (result) showSuccessMessage("Cập nhật nhà cung cấp thành công!");
            else showErrorMessage("Không thể cập nhật nhà cung cấp. Vui lòng thử lại!");
            return result;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi cập nhật nhà cung cấp: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSupplier(int id) {
        try {
            if (id <= 0) {
                showErrorMessage("ID nhà cung cấp không hợp lệ!");
                return false;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa nhà cung cấp này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean result = supplierDAO.delete(id);
                if (result) showSuccessMessage("Xóa nhà cung cấp thành công!");
                else showErrorMessage("Không thể xóa nhà cung cấp!");
                return result;
            }
            return false;
        } catch (Exception e) {
            showErrorMessage("Lỗi khi xóa nhà cung cấp: " + e.getMessage());
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
