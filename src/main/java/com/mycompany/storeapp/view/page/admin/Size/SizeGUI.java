/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Size;

/**
 *
 * @author ADMIN
 */
import com.mycompany.storeapp.view.page.admin.Size.*;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.model.entity.Size;
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

public class SizeGUI extends JPanel {
    private SizeController sizeController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<Size> currentData;
    private List<Size> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    private final String[] columnNames = {"ID", "Tên kích cỡ", "Mô tả", "Ngày tạo", "Ngày cập nhật"};
    private final String[] fieldNames = {"sizeId", "name", "description", "created_at", "updated_at"};

    public SizeGUI() {
        this.sizeController = new SizeController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        header = new ContentHeader("Quản lý chất liệu");
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
        header.addAddButtonListener(e -> showAddSizeDialog());
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
            List<Size> pageData = getCurrentPageData(); 
            if (row >= 0 && row < pageData.size()) {
                Size size = pageData.get(row); 
                //System.out.println("Editing Size ID: " + size.getSizeId() + ", Name: " + size.getName()); // Debug
                showEditSizeDialog(size);
            } else {
                System.out.println("Invalid row index: " + row + ", PageData size: " + pageData.size()); // Debug
            }
        });

        table.setDeleteActionListener(e -> {
           int row = e.getID();
            List<Size> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                long sizeId = pageData.get(row).getSizeId();
                System.out.println("Deleting Size ID: " + sizeId); // Debug
                deleteSize(sizeId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) showSizeDetailsDialog(filteredData.get(row));
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
        currentData = sizeController.getAllSizes()!= null ? sizeController.getAllSizes(): new ArrayList<>();
        filteredData = new ArrayList<>(currentData);
        updateTable();
    }

    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase();
        filteredData = currentSearchText.isEmpty() ? new ArrayList<>(currentData) :
            currentData.stream().filter(m -> m.getName().toLowerCase().contains(currentSearchText) ||
                (m.getDescription() != null && m.getDescription().toLowerCase().contains(currentSearchText)))
                .collect(java.util.stream.Collectors.toList());
        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        List<Size> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }

    private List<Size> getCurrentPageData() {
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

    private void showAddSizeDialog() {
        SizeFormDialog dialog = new SizeFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm chất liệu mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (sizeController.createSize(dialog.getsize())) loadData();
        }
    }

    private void showEditSizeDialog(Size size) {
        SizeFormDialog dialog = new SizeFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa chất liệu", size);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (sizeController.updateSize(dialog.getsize())) loadData();
        }
    }

    private void showSizeDetailsDialog(Size size) {
        JOptionPane.showMessageDialog(this, "Chi tiết chất liệu: " + size.getName(), "Chi tiết", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSize(long id) {
        if (sizeController.deleteSize(id)) loadData();
    }
    
     private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("sizes.xlsx"));
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