/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Material;

/**
 *
 * @author ADMIN
 */
import com.mycompany.storeapp.controller.admin.MaterialController;
import com.mycompany.storeapp.model.entity.Material;
import com.mycompany.storeapp.view.component.CustomTable;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentFooter;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class MaterialGUI extends JPanel {
    private MaterialController materialController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<Material> currentData;
    private List<Material> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    private final String[] columnNames = {"ID", "Tên chất liệu", "Mô tả", "Ngày tạo", "Ngày cập nhật"};
    private final String[] fieldNames = {"materialId", "name", "description", "created_at", "updated_at"};

    public MaterialGUI() {
        this.materialController = new MaterialController();
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
        header.addAddButtonListener(e -> showAddMaterialDialog());
        header.addSearchButtonListener(e -> performSearch());
        header.addSearchFieldListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performSearch();
            }
        });

        table.setEditActionListener(e -> {
            int row = e.getID();
            List<Material> pageData = getCurrentPageData(); 
            if (row >= 0 && row < pageData.size()) {
                Material material = pageData.get(row); 
                //System.out.println("Editing Material ID: " + material.getMaterialId() + ", Name: " + material.getName()); // Debug
                showEditMaterialDialog(material);
            } else {
                System.out.println("Invalid row index: " + row + ", PageData size: " + pageData.size()); // Debug
            }
        });

        table.setDeleteActionListener(e -> {
           int row = e.getID();
            List<Material> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                long materialId = pageData.get(row).getMaterialId();
                System.out.println("Deleting Material ID: " + materialId); // Debug
                deleteMaterial(materialId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) showMaterialDetailsDialog(filteredData.get(row));
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
        currentData = materialController.getAllMaterials() != null ? materialController.getAllMaterials() : new ArrayList<>();
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
        List<Material> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }

    private List<Material> getCurrentPageData() {
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

    private void showAddMaterialDialog() {
        MaterialFormDialog dialog = new MaterialFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm chất liệu mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (materialController.createMaterial(dialog.getMaterial())) loadData();
        }
    }

    private void showEditMaterialDialog(Material material) {
        MaterialFormDialog dialog = new MaterialFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa chất liệu", material);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (materialController.updateMaterial(dialog.getMaterial())) loadData();
        }
    }

    private void showMaterialDetailsDialog(Material material) {
        JOptionPane.showMessageDialog(this, "Chi tiết chất liệu: " + material.getName(), "Chi tiết", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteMaterial(long id) {
        if (materialController.deleteMaterial(id)) loadData();
    }
}