/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Product;

import com.mycompany.storeapp.controller.admin.ProductController;
import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductImage;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.view.component.CustomTable;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentFooter;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Panel quản lý sản phẩm - Sử dụng Product entity trực tiếp
 * @author Hi
 */
public class ProductGUI extends JPanel {
    private ProductController productController;
    private ProductVariantController productVariantController;
    private ProductImageController imageController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    
    private List<Product> currentData;
    private List<Product> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;
    
    // Maps để cache dữ liệu liên quan
    private Map<Long, List<ProductVariant>> productVariantsMap;
    private Map<Long, String> productImageMap;
    
    // Column names và field names cho Product
    private final String[] columnNames = {
        "Hình ảnh", "Tên sản phẩm", "Giá gốc", 
        "Tổng kho", "Variants", "Trạng thái", "Ngày thêm"
    };
    
    private final String[] fieldNames = {
        "primaryImageUrl", "name", "formattedPrice", 
        "totalStock", "variantSummary", "statusDisplay", "formattedCreatedAt"
    };
    
    public ProductGUI() {
        this.productController = new ProductController();
        this.productVariantController = new ProductVariantController();
        this.imageController = new ProductImageController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        this.productVariantsMap = new HashMap<>();
        this.productImageMap = new HashMap<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }
    
    public ProductGUI(ProductController productController, ProductVariantController productVariantController, ProductImageController imageController) {
        this.productController = productController;
        this.productVariantController = productVariantController;
        this.imageController = imageController;
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        this.productVariantsMap = new HashMap<>();
        this.productImageMap = new HashMap<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }
    
    private void initComponents() {
        // Tạo header
        header = new ContentHeader("Quản lý sản phẩm");
        
        // Tạo table với Custom Table
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
        int availableHeight = screenHeight - 260;
        int rowHeight = 120; // Chiều cao cho hình ảnh và variant
        int calculatedPageSize = Math.max(5, availableHeight / rowHeight);
        
        // Giới hạn page size từ 5 đến 15
        this.pageSize = Math.min(15, calculatedPageSize);
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
        header.addAddButtonListener(e -> showAddProductDialog());
        
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
            int tableRow = e.getID(); // Chỉ số trong bảng hiện tại (0-based)
            int actualRow = getActualRowIndex(tableRow); // Chỉ số thực tế trong filteredData

            if (actualRow >= 0 && actualRow < filteredData.size()) {
                Product product = filteredData.get(actualRow);
                showEditProductDialog(product);
            }
        });
        
        table.setDeleteActionListener(e -> {
            int tableRow = e.getID(); // Chỉ số trong bảng hiện tại (0-based)
            int actualRow = getActualRowIndex(tableRow); // Chỉ số thực tế trong filteredData

            if (actualRow >= 0 && actualRow < filteredData.size()) {
                Product product = filteredData.get(actualRow);
                deleteProduct(product);
            }
        });
        
        table.setRowDoubleClickListener(e -> {
            int tableRow = e.getID(); // Chỉ số trong bảng hiện tại (0-based)
            int actualRow = getActualRowIndex(tableRow); // Chỉ số thực tế trong filteredData

            if (actualRow >= 0 && actualRow < filteredData.size()) {
                Product product = filteredData.get(actualRow);
                showProductDetailsDialog(product);
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
            currentData = productController.getAllProducts();
            if (currentData == null) {
                currentData = new ArrayList<>();
            }
            
            // Tải trước tất cả dữ liệu liên quan
            loadRelatedData();
            
            // Thiết lập các thuộc tính động cho Product
            enrichProductData();
            
            filteredData = new ArrayList<>(currentData);
            updateTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadRelatedData() {
        productVariantsMap.clear();
        productImageMap.clear();
        
        for (Product product : currentData) {
            try {
                // Load variants
                List<ProductVariant> variants = productVariantController.getVariantsByProductId(product.getProductId());
                productVariantsMap.put(product.getProductId(), variants != null ? variants : new ArrayList<>());
                
                // Load primary image
                List<ProductImage> images = imageController.getImagesByProductId(product.getProductId());
                String imageUrl = "";
                if (images != null && !images.isEmpty()) {
                    // Tìm hình ảnh primary trước, nếu không có thì lấy hình đầu tiên
                    imageUrl = images.stream()
                        .filter(ProductImage::isPrimary)
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(images.get(0).getImageUrl());
                }
                productImageMap.put(product.getProductId(), imageUrl);
                
            } catch (Exception e) {
                System.err.println("Error loading related data for product " + product.getProductId() + ": " + e.getMessage());
                productVariantsMap.put(product.getProductId(), new ArrayList<>());
                productImageMap.put(product.getProductId(), "");
            }
        }
    }
    
    private void enrichProductData() {
        for (Product product : currentData) {
            // Thiết lập variants cho product
            List<ProductVariant> variants = productVariantsMap.get(product.getProductId());
            product.setVariants(variants);
            
            // Có thể thêm các thuộc tính khác nếu cần
        }
    }
    
    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase();
        
        if (currentSearchText.isEmpty()) {
            filteredData = new ArrayList<>(currentData);
        } else {
            filteredData = currentData.stream()
                .filter(product -> 
                    product.getName().toLowerCase().contains(currentSearchText) ||
                    (product.getBrand() != null && 
                     product.getBrand().toLowerCase().contains(currentSearchText)) ||
                    (product.getDescription() != null && 
                     product.getDescription().toLowerCase().contains(currentSearchText)) ||
                    (product.getSku() != null && 
                     product.getSku().toLowerCase().contains(currentSearchText)) ||
                    (product.getSlug() != null && 
                     product.getSlug().toLowerCase().contains(currentSearchText)) ||
                    // Tìm kiếm trong variant
                    hasVariantMatch(product, currentSearchText)
                )
                .collect(Collectors.toList());
        }
        
        currentPage = 1;
        updateTable();
    }
    
    private boolean hasVariantMatch(Product product, String searchText) {
        List<ProductVariant> variants = productVariantsMap.get(product.getProductId());
        if (variants == null || variants.isEmpty()) {
            return false;
        }
        
        return variants.stream().anyMatch(variant -> 
            (variant.getColor() != null && 
             variant.getColor().getName().toLowerCase().contains(searchText)) ||
            (variant.getSize() != null && 
             variant.getSize().getName().toLowerCase().contains(searchText))
        );
    }
    
    private void updateTable() {
        // Get current page data
        List<Product> pageData = getCurrentPageData();
        
        // Tạo enhanced products với các thuộc tính động
        List<EnhancedProduct> enhancedProducts = pageData.stream()
            .map(this::createEnhancedProduct)
            .collect(Collectors.toList());
        
        // Update table
        table.setData(enhancedProducts);
        
        // Update footer
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }
    
    private EnhancedProduct createEnhancedProduct(Product product) {
        EnhancedProduct enhanced = new EnhancedProduct();
        
        // Copy tất cả thuộc tính từ Product gốc
        enhanced.setProductId(product.getProductId());
        enhanced.setCategoryId(product.getCategoryId());
        enhanced.setMaterialId(product.getMaterialId());
        enhanced.setBrand(product.getBrand());
        enhanced.setName(product.getName());
        enhanced.setGender(product.getGender());
        enhanced.setCareInstruction(product.getCareInstruction());
        enhanced.setSlug(product.getSlug());
        enhanced.setDescription(product.getDescription());
        enhanced.setPrice(product.getPrice());
        enhanced.setSalePrice(product.getSalePrice());
        enhanced.setStockQuantity(product.getStockQuantity());
        enhanced.setSku(product.getSku());
        enhanced.setMinPurchaseQuantity(product.getMinPurchaseQuantity());
        enhanced.setMaxPurchaseQuantity(product.getMaxPurchaseQuantity());
        enhanced.setActive(product.isActive());
        enhanced.setCreatedAt(product.getCreatedAt());
        enhanced.setUpdatedAt(product.getUpdatedAt());
        enhanced.setVariants(product.getVariants());
        
        // Thiết lập các thuộc tính động
        List<ProductVariant> variants = productVariantsMap.get(product.getProductId());
        String imageUrl = productImageMap.get(product.getProductId());
        
        enhanced.setupDynamicProperties(variants, imageUrl);
        
        return enhanced;
    }
    
    private List<Product> getCurrentPageData() {
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
    
    private void showAddProductDialog() {
        ProductFormDialog dialog = new ProductFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Thêm sản phẩm mới", 
            null
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Product newProduct = dialog.getProduct();
            if (productController.createProduct(newProduct)) {
                loadData(); // Reload data after successful creation
            }
        }
    }
    
    private void showEditProductDialog(Product product) {
        ProductFormDialog dialog = new ProductFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Chỉnh sửa sản phẩm", 
            product
        );
        
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Product updatedProduct = dialog.getProduct();
            if (productController.updateProduct(updatedProduct)) {
                loadData(); // Reload data after successful update
            }
        }
    }
    
    private void showProductDetailsDialog(Product product) {
        ProductFormDialog dialog = new ProductFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Chi tiết sản phẩm",
            product
        );
        dialog.setVisible(true);
    }
    
    private void deleteProduct(Product product) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getName() + "\"?\n" +
            "Hành động này sẽ xóa cả các variant liên quan và không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (productController.deleteProduct(product.getProductId())) {
                loadData();
            }
        }
    }
    
    // Public methods
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
    
    public ProductController getProductController() {
        return productController;
    }
    
    public void setProductController(ProductController productController) {
        this.productController = productController;
        loadData();
    }
    
    /**
     * Extended Product class với các thuộc tính động cho hiển thị
     */
    public static class EnhancedProduct extends Product {
        private String primaryImageUrl;
        private String formattedPrice;
        private String totalStock;
        private String variantSummary;
        private String statusDisplay;
        private String formattedCreatedAt;
        
        public void setupDynamicProperties(List<ProductVariant> variants, String imageUrl) {
            this.primaryImageUrl = imageUrl != null ? imageUrl : "";
            this.formattedPrice = String.format("%,.0f VNĐ", getPrice());
            this.totalStock = calculateTotalStock(variants);
            this.variantSummary = createVariantSummary(variants);
            this.statusDisplay = createStatusDisplay(variants);
            this.formattedCreatedAt = formatCreatedAt();
        }
        
        private String calculateTotalStock(List<ProductVariant> variants) {
            if (variants == null || variants.isEmpty()) {
                return String.valueOf(getStockQuantity());
            }
            
            int totalStock = variants.stream()
                .mapToInt(ProductVariant::getStockQuantity)
                .sum();
            
            return String.valueOf(totalStock);
        }
        
        private String createVariantSummary(List<ProductVariant> variants) {
            if (variants == null || variants.isEmpty()) {
                return "<html><div style='color: #999; font-style: italic; text-align: center; padding: 10px;'>Chưa có variant</div></html>";
            }
            
            // Nhóm variants theo color
            Map<String, List<ProductVariant>> colorGroups = new HashMap<>();
            for (ProductVariant variant : variants) {
                String color = variant.getColor() != null ? variant.getColor().getName() : "Mặc định";
                colorGroups.computeIfAbsent(color, k -> new ArrayList<>()).add(variant);
            }
            
            StringBuilder html = new StringBuilder("<html><div style='font-size: 11px; padding: 2px;'>");
            
            int colorCount = 0;
            for (Map.Entry<String, List<ProductVariant>> entry : colorGroups.entrySet()) {
                if (colorCount > 0) {
                    html.append("<br/>");
                }
                
                String color = entry.getKey();
                List<ProductVariant> colorVariants = entry.getValue();
                
                // Hiển thị tên màu
                html.append("<div style='margin: 2px 0;'>");
                html.append("<span style='font-weight: bold; color: #2c3e50; background: #ecf0f1; padding: 1px 4px; border-radius: 2px;'>")
                    .append(color)
                    .append("</span>");
                
                // Hiển thị sizes cho màu này
                html.append("<div style='margin-left: 8px; margin-top: 2px;'>");
                
                // Nhóm theo size và tính tổng stock
                Map<String, Integer> sizeStockMap = new HashMap<>();
                for (ProductVariant variant : colorVariants) {
                    String size = variant.getSize() != null ? variant.getSize().getName() : "Free";
                    sizeStockMap.put(size, sizeStockMap.getOrDefault(size, 0) + variant.getStockQuantity());
                }
                
                int sizeCount = 0;
                for (Map.Entry<String, Integer> sizeEntry : sizeStockMap.entrySet()) {
                    if (sizeCount > 0) {
                        html.append(" | ");
                    }
                    
                    String size = sizeEntry.getKey();
                    int stock = sizeEntry.getValue();
                    
                    if (stock > 0) {
                        html.append("<span style='color: #27ae60; font-weight: bold; background: #d5f4e6; padding: 1px 3px; border-radius: 2px;'>")
                            .append(size)
                            .append(" (")
                            .append(stock)
                            .append(")")
                            .append("</span>");
                    } else {
                        html.append("<span style='color: #e74c3c; text-decoration: line-through; background: #fadbd8; padding: 1px 3px; border-radius: 2px;'>")
                            .append(size)
                            .append(" (0)")
                            .append("</span>");
                    }
                    
                    sizeCount++;
                }
                
                html.append("</div>");
                html.append("</div>");
                
                colorCount++;
            }
            
            // Hiển thị tổng số variant
            html.append("<div style='margin-top: 4px; padding-top: 2px; border-top: 1px solid #ecf0f1; font-size: 10px; color: #7f8c8d; text-align: center;'>");
            html.append("Tổng: ").append(variants.size()).append(" variant");
            html.append("</div>");
            
            html.append("</div></html>");
            return html.toString();
        }
        
        private String createStatusDisplay(List<ProductVariant> variants) {
            if (!isActive()) {
                return "<html><span style='color: #e74c3c; font-weight: bold;'>Ngừng bán</span></html>";
            }
            
            // Kiểm tra xem có variant nào còn hàng không
            if (variants != null && !variants.isEmpty()) {
                boolean hasStock = variants.stream().anyMatch(v -> v.getStockQuantity() > 0);
                if (hasStock) {
                    return "<html><span style='color: #27ae60; font-weight: bold;'>Đang bán</span></html>";
                } else {
                    return "<html><span style='color: #f39c12; font-weight: bold;'>Hết hàng</span></html>";
                }
            }
            
            return "<html><span style='color: #27ae60; font-weight: bold;'>Đang bán</span></html>";
        }
        
        private String formatCreatedAt() {
            if (getCreatedAt() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                return sdf.format(getCreatedAt());
            }
            return "";
        }
        
        // Getters cho các thuộc tính động
        public String getPrimaryImageUrl() { return primaryImageUrl; }
        public String getFormattedPrice() { return formattedPrice; }
        public String getTotalStock() { return totalStock; }
        public String getVariantSummary() { return variantSummary; }
        public String getStatusDisplay() { return statusDisplay; }
        public String getFormattedCreatedAt() { return formattedCreatedAt; }
    }
    private int getActualRowIndex(int tableRowIndex) {
    // Tính chỉ số bắt đầu của trang hiện tại
    int startIndex = (currentPage - 1) * pageSize;
    
    // Tính chỉ số thực tế trong filteredData
    return startIndex + tableRowIndex;
}
}