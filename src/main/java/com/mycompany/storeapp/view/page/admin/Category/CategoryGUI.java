/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Category;

import com.mycompany.storeapp.controller.admin.CategoryController;
import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.view.component.CustomTable;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentFooter;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel quản lý danh mục
 * @author Hi
 */
public class CategoryGUI extends JPanel {
    private CategoryController categoryController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    
    private List<Category> currentData;
    private List<Category> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;
    
    // Column names và field names cho Category
    private final String[] columnNames = {
        "ID","Hình ảnh", "Tên danh mục", "Slug", "Mô tả", "Trạng thái", "Ngày tạo"
    };
    
    private final String[] fieldNames = {
        "categoryId","imageUrl", "name", "slug", "description", "isActive", "created_at"
    };
    
    public CategoryGUI() {
        this.categoryController = new CategoryController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }
    
    public CategoryGUI(CategoryController categoryController) {
        this.categoryController = categoryController;
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }
    
    private void initComponents() {
        // Tạo header
        header = new ContentHeader("Quản lý danh mục");
        
        // Tạo table
        table = new CustomTable(columnNames, fieldNames, true);
        
        // Tạo footer
        footer = new ContentFooter();
        
        // Calculate page size based on screen size
        calculatePageSize();
    }
    
    private void calculatePageSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        
        // Tính toán số dòng hiển thị dựa trên chiều cao màn hình
        // Giả sử mỗi dòng cao 45px, header 80px, footer 60px, margin 100px
        int availableHeight = screenHeight - 240;
        int rowHeight = 45;
        int calculatedPageSize = Math.max(8, availableHeight / rowHeight);
        
        // Giới hạn page size từ 8 đến 25
        this.pageSize = Math.min(25, calculatedPageSize);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Add components
        add(header, BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Header events
        header.addAddButtonListener(e -> showAddCategoryDialog());
        
        header.addSearchButtonListener(e -> performSearch());
        
        header.addSearchFieldListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        
        // Table events
        table.setEditActionListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) {
                Category category = filteredData.get(row);
                showEditCategoryDialog(category);
            }
        });
        
        table.setDeleteActionListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) {
                Category category = filteredData.get(row);
                deleteCategory(category);
            }
        });
        
        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) {
                Category category = filteredData.get(row);
                showCategoryDetailsDialog(category);
            }
        });
        
        // Footer events
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
        try {
            currentData = categoryController.getAllCategories();
            if (currentData == null) {
                currentData = new ArrayList<>();
            }
            filteredData = new ArrayList<>(currentData);
            updateTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase();
        
        if (currentSearchText.isEmpty()) {
            filteredData = new ArrayList<>(currentData);
        } else {
            filteredData = currentData.stream()
                .filter(category -> 
                    category.getName().toLowerCase().contains(currentSearchText) ||
                    (category.getDescription() != null && 
                     category.getDescription().toLowerCase().contains(currentSearchText)) ||
                    (category.getSlug() != null && 
                     category.getSlug().toLowerCase().contains(currentSearchText))
                )
                .collect(java.util.stream.Collectors.toList());
        }
        
        currentPage = 1;
        updateTable();
    }
    
    private void updateTable() {
        // Get current page data
        List<Category> pageData = getCurrentPageData();
        
        // Update table
        table.setData(pageData);
        
        // Update footer
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }
    
    private List<Category> getCurrentPageData() {
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
    
    private void showAddCategoryDialog() {
        CategoryFormDialog dialog = new CategoryFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Thêm danh mục mới", 
            null
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Category newCategory = dialog.getCategory();
            if (categoryController.createCategory(newCategory)) {
                loadData(); // Reload data after successful creation
            }
        }
    }
    
    private void showEditCategoryDialog(Category category) {
        CategoryFormDialog dialog = new CategoryFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Chỉnh sửa danh mục", 
            category
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Category updatedCategory = dialog.getCategory();
            if (categoryController.updateCategory(updatedCategory)) {
                loadData(); // Reload data after successful update
            }
        }
    }
    
    private void showCategoryDetailsDialog(Category category) {
        CategoryDetailsDialog dialog = new CategoryDetailsDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            category
        );
        dialog.setVisible(true);
    }
    
    private void deleteCategory(Category category) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa danh mục \"" + category.getName() + "\"?\n" +
            "Hành động này không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (categoryController.deleteCategory(category.getCategoryId())) {
                loadData(); // Reload data after successful deletion
            }
        }
    }
    
    // Public methods để refresh data từ bên ngoài
    public void refreshData() {
        loadData();
    }
    
    public void clearSearch() {
        header.clearSearch();
        currentSearchText = "";
        filteredData = new ArrayList<>(currentData);
        currentPage = 1;
        updateTable();
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        currentPage = 1;
        updateTable();
    }
    
    public CategoryController getCategoryController() {
        return categoryController;
    }
    
    public void setCategoryController(CategoryController categoryController) {
        this.categoryController = categoryController;
        loadData();
    }
}