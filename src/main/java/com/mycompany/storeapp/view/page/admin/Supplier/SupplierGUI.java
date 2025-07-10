/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Supplier;

/**
 *
 * @author ADMIN
 */
import com.mycompany.storeapp.controller.admin.SupplierController;
import com.mycompany.storeapp.model.entity.Supplier;
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

public class SupplierGUI extends JPanel {
    private SupplierController supplierController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<Supplier> currentData;
    private List<Supplier> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    private final String[] columnNames = {"ID", "Tên", "Tên liên hệ", "Số điện thoại", "Email", "Địa chỉ", "Mô tả", "Kích hoạt", "Ngày tạo", "Ngày cập nhật"};
    private final String[] fieldNames = {"supplierId", "name", "contactName", "phone", "email", "address", "description", "isActive", "createdAt", "updatedAt"};

    public SupplierGUI() {
        this.supplierController = new SupplierController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        header = new ContentHeader("Quản lý nhà cung cấp");
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
        setBackground(Color.WHITE);
        add(header, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        header.addAddButtonListener(e -> showAddSupplierDialog());
        header.addExportExcelButtonListener(e -> exportToExcel());
        header.addSearchButtonListener(e -> performSearch());
        header.addSearchFieldListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performSearch();
            }
        });

        table.setEditActionListener(e -> {
            int row = e.getID();
            List<Supplier> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                Supplier supplier = pageData.get(row);
                System.out.println("Editing Supplier ID: " + supplier.getSupplierId() + ", Name: " + supplier.getName());
                showEditSupplierDialog(supplier);
            } else {
                System.out.println("Invalid row index: " + row + ", PageData size: " + pageData.size());
            }
        });

        table.setDeleteActionListener(e -> {
            int row = e.getID();
            List<Supplier> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                int supplierId = pageData.get(row).getSupplierId();
                System.out.println("Deleting Supplier ID: " + supplierId);
                deleteSupplier(supplierId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            List<Supplier> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                Supplier supplier = pageData.get(row);
                System.out.println("Viewing Supplier ID: " + supplier.getSupplierId() + ", Name: " + supplier.getName());
                showSupplierDetailsDialog(supplier);
            }
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
        currentData = supplierController.getAllSuppliers() != null ? supplierController.getAllSuppliers() : new ArrayList<>();
        filteredData = new ArrayList<>(currentData);
        System.out.println("Loaded " + currentData.size() + " suppliers");
        updateTable();
    }

    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase();
        filteredData = currentSearchText.isEmpty() ? new ArrayList<>(currentData) :
            currentData.stream().filter(s -> 
                (s.getName() != null && s.getName().toLowerCase().contains(currentSearchText)) ||
                (s.getContactName() != null && s.getContactName().toLowerCase().contains(currentSearchText)) ||
                (s.getPhone() != null && s.getPhone().contains(currentSearchText)) ||
                (s.getEmail() != null && s.getEmail().toLowerCase().contains(currentSearchText)) ||
                (s.getAddress() != null && s.getAddress().toLowerCase().contains(currentSearchText)) ||
                (s.getDescription() != null && s.getDescription().toLowerCase().contains(currentSearchText)))
                .collect(java.util.stream.Collectors.toList());
        currentPage = 1;
        System.out.println("Filtered to " + filteredData.size() + " suppliers");
        updateTable();
    }

    private void updateTable() {
        List<Supplier> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
        table.revalidate();
        table.repaint();
    }

    private List<Supplier> getCurrentPageData() {
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

    private void showAddSupplierDialog() {
        SupplierFormDialog dialog = new SupplierFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm nhà cung cấp mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (supplierController.createSupplier(dialog.getSupplier())) loadData();
        }
    }

    private void showEditSupplierDialog(Supplier supplier) {
        SupplierFormDialog dialog = new SupplierFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa nhà cung cấp", supplier);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (supplierController.updateSupplier(dialog.getSupplier())) loadData();
        }
    }

    private void showSupplierDetailsDialog(Supplier supplier) {
        JOptionPane.showMessageDialog(this, "Chi tiết nhà cung cấp: " + supplier.getName(), "Chi tiết", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSupplier(int id) {
        if (supplierController.deleteSupplier(id)) loadData();
    }
    
        private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("Suppliers.xlsx"));
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
