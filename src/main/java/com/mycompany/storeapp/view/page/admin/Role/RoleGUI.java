package com.mycompany.storeapp.view.page.admin.Role;

import com.mycompany.storeapp.controller.admin.RoleController;
import com.mycompany.storeapp.model.entity.Role;
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

public class RoleGUI extends JPanel {
    private RoleController roleController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<Role> currentData;
    private List<Role> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    private final String[] columnNames = {"ID", "Tên vai trò", "Mô tả"};
    private final String[] fieldNames = {"roleId", "name", "description"};

    public RoleGUI() {
        this.roleController = new RoleController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        header = new ContentHeader("Quản lý vai trò");
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
        header.addAddButtonListener(e -> showAddRoleDialog());
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
            List<Role> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                Role role = pageData.get(row);
                showEditRoleDialog(role);
            }
        });

        table.setDeleteActionListener(e -> {
            int row = e.getID();
            List<Role> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                int roleId = pageData.get(row).getRoleId();
                deleteRole(roleId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) showRoleDetailsDialog(filteredData.get(row));
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
        currentData = roleController.getAllRoles() != null ? roleController.getAllRoles() : new ArrayList<>();
        filteredData = new ArrayList<>(currentData);
        updateTable();
    }

    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase();
        filteredData = currentSearchText.isEmpty() ? new ArrayList<>(currentData) :
            currentData.stream().filter(r -> r.getName().toLowerCase().contains(currentSearchText) ||
                (r.getDescription() != null && r.getDescription().toLowerCase().contains(currentSearchText)))
                .collect(java.util.stream.Collectors.toList());
        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        List<Role> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }

    private List<Role> getCurrentPageData() {
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

    private void showAddRoleDialog() {
        RoleFormDialog dialog = new RoleFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm vai trò mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (roleController.createRole(dialog.getRole())) loadData();
        }
    }

    private void showEditRoleDialog(Role role) {
        RoleFormDialog dialog = new RoleFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa vai trò", role);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (roleController.updateRole(dialog.getRole())) loadData();
        }
    }

    private void showRoleDetailsDialog(Role role) {
        JOptionPane.showMessageDialog(this, "Chi tiết vai trò: " + role.getName(), "Chi tiết", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteRole(int id) {
        if (roleController.deleteRole(id)) loadData();
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("roles.xlsx"));
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