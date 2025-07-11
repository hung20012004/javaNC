package com.mycompany.storeapp.view.page.admin.Staff;

import com.mycompany.storeapp.controller.admin.UserController;
import com.mycompany.storeapp.model.entity.User;
import com.mycompany.storeapp.service.ExcelExporter;
import com.mycompany.storeapp.view.component.CustomTable;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentFooter;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StaffGUI extends JPanel {
    private UserController userController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<User> currentData;
    private List<User> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    // Cập nhật column names để phù hợp với quản lý nhân viên
    private final String[] columnNames = {"Họ tên", "Email", "Số điện thoại", "Giới tính", "Ngày sinh", "Vai trò"};
    private final String[] fieldNames = {"profile.fullName", "email", "profile.phone", "profile.gender", "profile.dateOfBirth", "role_id"};

    public StaffGUI() {
        this.userController = new UserController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        header = new ContentHeader("Quản lý nhân viên"); // Đổi title
        table = new CustomTable(columnNames, fieldNames, true);
        footer = new ContentFooter();
        calculatePageSize();
    }

    private void calculatePageSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int availableHeight = screenHeight - 240;
        int rowHeight = 45;
        int calculatedPageSize = Math.max(8, availableHeight / rowHeight);
        this.pageSize = Math.min(25, calculatedPageSize);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(java.awt.Color.WHITE);
        add(header, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        header.addAddButtonListener(e -> showAddStaffDialog());
        header.addSearchButtonListener(e -> performSearch());
        header.addExportExcelButtonListener(e -> exportToExcel());
        header.addSearchFieldListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performSearch();
            }
        });

        table.setEditActionListener(e -> {
            int row = e.getID();
            List<User> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                User staff = pageData.get(row);
                showEditStaffDialog(staff);
            }
        });

        table.setDeleteActionListener(e -> {
            int row = e.getID();
            List<User> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                int staffId = pageData.get(row).getId();
                deleteStaff(staffId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) showStaffDetailsDialog(filteredData.get(row));
        });

        footer.addFirstButtonListener(e -> goToPage(1));
        footer.addPrevButtonListener(e -> goToPage(currentPage - 1));
        footer.addNextButtonListener(e -> goToPage(currentPage + 1));
        footer.addLastButtonListener(e -> goToPage(getTotalPages()));
        footer.addPageSizeChangeListener(e -> {
            pageSize = footer.getPageSize();
            currentPage = 1;
            updateTable();
        });
    }

    private void loadData() {
        List<User> allUsers = userController.getAllUsers();
        if (allUsers != null) {
            currentData = allUsers.stream()
                .filter(user -> user.getRole() != 5) // Lọc nhân viên (không phải khách hàng)
                .map(user -> userController.getUserWithProfile(user.getId()))
                .collect(Collectors.toList());
        } else {
            currentData = new ArrayList<>();
        }
        filteredData = new ArrayList<>(currentData);
        updateTable();
    }

    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase();
        if (currentSearchText.isEmpty()) {
            filteredData = new ArrayList<>(currentData);
        } else {
            filteredData = currentData.stream()
                .filter(user -> {
                    String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
                    String fullName = user.getProfile() != null && user.getProfile().getFullName() != null 
                        ? user.getProfile().getFullName().toLowerCase() : "";
                    String phone = user.getProfile() != null && user.getProfile().getPhone() != null 
                        ? user.getProfile().getPhone().toLowerCase() : "";
                    
                    return email.contains(currentSearchText) || 
                           fullName.contains(currentSearchText) || 
                           phone.contains(currentSearchText);
                })
                .collect(Collectors.toList());
        }
        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        List<User> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }

    private List<User> getCurrentPageData() {
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, filteredData.size());
        return startIndex >= filteredData.size() ? new ArrayList<>() : filteredData.subList(startIndex, endIndex);
    }

    private int getTotalPages() {
        return (int) Math.ceil((double) filteredData.size() / pageSize);
    }

    private void goToPage(int page) {
        int totalPages = getTotalPages();
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            updateTable();
        }
    }

    private void showAddStaffDialog() {
        StaffFormDialog dialog = new StaffFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm nhân viên mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User staff = dialog.getStaff();
            if (userController.createUserWithProfile(staff, staff.getProfile())) {
                loadData();
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditStaffDialog(User staff) {
        StaffFormDialog dialog = new StaffFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa nhân viên", staff);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User updatedStaff = dialog.getStaff();
            if (userController.updateUserWithProfile(updatedStaff, updatedStaff.getProfile())) {
                loadData();
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStaffDetailsDialog(User staff) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(staff.getId()).append("\n");
        details.append("Email: ").append(staff.getEmail()).append("\n");
        details.append("Vai trò: ").append(getRoleName(staff.getRole())).append("\n");
        details.append("Trạng thái: ").append(staff.getIs_active() ? "Hoạt động" : "Không hoạt động").append("\n");
        
        if (staff.getProfile() != null) {
            details.append("Họ tên: ").append(staff.getProfile().getFullName()).append("\n");
            details.append("Số điện thoại: ").append(staff.getProfile().getPhone()).append("\n");
            details.append("Giới tính: ").append(staff.getProfile().getGender()).append("\n");
            details.append("Ngày sinh: ").append(staff.getProfile().getDateOfBirth()).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, details.toString(), "Chi tiết nhân viên", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteStaff(int id) {
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa nhân viên này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (userController.deleteUser(id)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("staff.xlsx")); // Đổi tên file
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }
            ExcelExporter.exportToExcel(columnNames, filteredData, fieldNames, filePath);
            JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!");
        }
    }
    
    // Helper method để convert role ID thành tên vai trò
    private String getRoleName(int roleId) {
        switch (roleId) {
            case 1: return "Admin";
            case 2: return "Quản lý";
            case 3: return "Nhân viên bán hàng";
            case 4: return "Nhân viên kho";
            case 5: return "Khách hàng";
            default: return "Không xác định";
        }
    }
}