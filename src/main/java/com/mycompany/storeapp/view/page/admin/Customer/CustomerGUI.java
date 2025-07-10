package com.mycompany.storeapp.view.page.admin.Customer;

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

public class CustomerGUI extends JPanel {
    private UserController userController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<User> currentData;
    private List<User> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    private final String[] columnNames = {"Họ tên", "Email", "Số điện thoại", "Giới tính", "Ngày sinh"};
    private final String[] fieldNames = {"profile.fullName", "email", "profile.phone", "profile.gender", "profile.dateOfBirth"};

    public CustomerGUI() {
        this.userController = new UserController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        header = new ContentHeader("Quản lý khách hàng");
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
        header.addAddButtonListener(e -> showAddCustomerDialog());
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
                User customer = pageData.get(row);
                showEditCustomerDialog(customer);
            }
        });

        table.setDeleteActionListener(e -> {
            int row = e.getID();
            List<User> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                int customerId = pageData.get(row).getId();
                deleteCustomer(customerId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) showCustomerDetailsDialog(filteredData.get(row));
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
                .filter(user -> user.getRole() == 5)
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

    private void showAddCustomerDialog() {
        CustomerFormDialog dialog = new CustomerFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm khách hàng mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User customer = dialog.getCustomer();
            if (userController.createUserWithProfile(customer, customer.getProfile())) {
                loadData();
            }
        }
    }

    private void showEditCustomerDialog(User customer) {
        CustomerFormDialog dialog = new CustomerFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa khách hàng", customer);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User updatedCustomer = dialog.getCustomer();
            if (userController.updateUserWithProfile(updatedCustomer, updatedCustomer.getProfile())) {
                loadData();
            }
        }
    }

    private void showCustomerDetailsDialog(User customer) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(customer.getId()).append("\n");
        details.append("Email: ").append(customer.getEmail()).append("\n");
        details.append("Trạng thái: ").append(customer.getIs_active()? "Hoạt động" : "Không hoạt động").append("\n");
        
        if (customer.getProfile() != null) {
            details.append("Họ tên: ").append(customer.getProfile().getFullName()).append("\n");
            details.append("Số điện thoại: ").append(customer.getProfile().getPhone()).append("\n");
            details.append("Giới tính: ").append(customer.getProfile().getGender()).append("\n");
            details.append("Ngày sinh: ").append(customer.getProfile().getDateOfBirth()).append("\n");
        }
        
        JOptionPane.showMessageDialog(this, details.toString(), "Chi tiết khách hàng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteCustomer(int id) {
        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa khách hàng này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (userController.deleteUser(id)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("customers.xlsx"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".xlsx")) {
                filePath += ".xlsx";
            }
            ExcelExporter.exportToExcel(columnNames, filteredData, fieldNames, filePath);
        }
    }
}