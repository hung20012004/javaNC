/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Color;

/**
 *
 * @author ADMIN
 */
import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.model.entity.Color;
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

public class ColorGUI extends JPanel {
    private ColorController colorController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<Color> currentData;
    private List<Color> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    private final String[] columnNames = {"ID", "Tên màu", "Mô tả", "Ngày tạo", "Ngày cập nhật"};
    private final String[] fieldNames = {"colorId", "name", "description", "created_at", "updated_at"};

    public ColorGUI() {
        this.colorController = new ColorController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        header = new ContentHeader("Quản lý màu sắc");
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
        header.addAddButtonListener(e -> showAddColorDialog());
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
            List<Color> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                Color color = pageData.get(row);
                showEditColorDialog(color);
            }
        });

        table.setDeleteActionListener(e -> {
            int row = e.getID();
            List<Color> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                long colorId = pageData.get(row).getColorId();
                deleteColor(colorId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) showColorDetailsDialog(filteredData.get(row));
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
        currentData = colorController.getAllColors() != null ? colorController.getAllColors() : new ArrayList<>();
        filteredData = new ArrayList<>(currentData);
        updateTable();
    }

    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase();
        filteredData = currentSearchText.isEmpty() ? new ArrayList<>(currentData) :
            currentData.stream().filter(c -> c.getName().toLowerCase().contains(currentSearchText) ||
                (c.getDescription() != null && c.getDescription().toLowerCase().contains(currentSearchText)))
                .collect(java.util.stream.Collectors.toList());
        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        List<Color> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }

    private List<Color> getCurrentPageData() {
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

    private void showAddColorDialog() {
        ColorFormDialog dialog = new ColorFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm màu mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (colorController.createColor(dialog.getColor())) loadData();
        }
    }

    private void showEditColorDialog(Color color) {
        ColorFormDialog dialog = new ColorFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa màu", color);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (colorController.updateColor(dialog.getColor())) loadData();
        }
    }

    private void showColorDetailsDialog(Color color) {
        JOptionPane.showMessageDialog(this, "Chi tiết màu: " + color.getName(), "Chi tiết", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteColor(long id) {
        if (colorController.deleteColor(id)) loadData();
    }
    
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("colors.xlsx"));
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