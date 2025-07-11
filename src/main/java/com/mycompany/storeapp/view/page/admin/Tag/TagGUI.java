/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Tag;

/**
 *
 * @author Hi
 */
import com.mycompany.storeapp.controller.admin.TagController;
import com.mycompany.storeapp.model.entity.Tag;
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

public class TagGUI extends JPanel {
    private TagController tagController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<Tag> currentData;
    private List<Tag> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    private final String[] columnNames = {"ID", "Tên tag", "Slug"};
    private final String[] fieldNames = {"tagId", "name", "slug"};

    public TagGUI() {
        this.tagController = new TagController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        header = new ContentHeader("Quản lý thẻ tag");
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
        // Header event listeners
        header.addAddButtonListener(e -> showAddTagDialog());
        header.addSearchButtonListener(e -> performSearch());
        header.addExportExcelButtonListener(e -> exportToExcel());
        header.addSearchFieldListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });

        // Table event listeners
        table.setEditActionListener(e -> {
            int row = e.getID();
            List<Tag> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                Tag tag = pageData.get(row);
                showEditTagDialog(tag);
            }
        });

        table.setDeleteActionListener(e -> {
            int row = e.getID();
            List<Tag> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                int tagId = pageData.get(row).getTagId();
                deleteTag(tagId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            List<Tag> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                showTagDetailsDialog(pageData.get(row));
            }
        });

        // Footer event listeners
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
        currentData = tagController.getAllTags();
        if (currentData == null) {
            currentData = new ArrayList<>();
        }
        filteredData = new ArrayList<>(currentData);
        updateTable();
    }

    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase().trim();
        
        if (currentSearchText.isEmpty()) {
            filteredData = new ArrayList<>(currentData);
        } else {
            filteredData = currentData.stream()
                    .filter(tag -> 
                        tag.getName().toLowerCase().contains(currentSearchText) ||
                        (tag.getSlug() != null && tag.getSlug().toLowerCase().contains(currentSearchText))
                    )
                    .collect(java.util.stream.Collectors.toList());
        }
        
        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        List<Tag> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }

    private List<Tag> getCurrentPageData() {
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, filteredData.size());
        
        if (startIndex >= filteredData.size()) {
            return new ArrayList<>();
        }
        
        return filteredData.subList(startIndex, endIndex);
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

    private void showAddTagDialog() {
        TagFormDialog dialog = new TagFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Thêm tag mới", 
            null
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            if (tagController.createTag(dialog.getTag())) {
                loadData();
            }
        }
    }

    private void showEditTagDialog(Tag tag) {
        TagFormDialog dialog = new TagFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Chỉnh sửa tag", 
            tag
        );
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            if (tagController.updateTag(dialog.getTag())) {
                loadData();
            }
        }
    }

    private void showTagDetailsDialog(Tag tag) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(tag.getTagId()).append("\n");
        details.append("Tên tag: ").append(tag.getName()).append("\n");
        details.append("Slug: ").append(tag.getSlug() != null ? tag.getSlug() : "Không có");
        
        JOptionPane.showMessageDialog(
            this, 
            details.toString(), 
            "Chi tiết tag", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void deleteTag(int tagId) {
        if (tagController.deleteTag(tagId)) {
            loadData();
        }
    }
    
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("tags.xlsx"));
        
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

    /**
     * Refresh dữ liệu từ database
     */
    public void refreshData() {
        loadData();
    }

    /**
     * Lấy tag được chọn hiện tại
     * @return Tag được chọn hoặc null nếu không có
     */
//    public Tag getSelectedTag() {
//        int selectedRow = table.getSelectedRow();
//        if (selectedRow >= 0) {
//            List<Tag> pageData = getCurrentPageData();
//            if (selectedRow < pageData.size()) {
//                return pageData.get(selectedRow);
//            }
//        }
//        return null;
//    }

    
//    public void searchTag(String searchText) {
//        header.setSearchText(searchText);
//        performSearch();
//    }
}