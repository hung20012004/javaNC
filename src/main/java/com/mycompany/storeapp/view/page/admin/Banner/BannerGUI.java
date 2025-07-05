/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Banner;

import com.mycompany.storeapp.controller.admin.BannerController;
import com.mycompany.storeapp.model.entity.Banner;
import com.mycompany.storeapp.view.component.CustomTable;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentFooter;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel quản lý banner
 * @author ADMIN
 */
public class BannerGUI extends JPanel {
    private BannerController bannerController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    
    private List<Banner> currentData;
    private List<Banner> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;
    
    private final String[] columnNames = {"ID", "Hình ảnh", "Tiêu đề", "Phụ đề", "Văn bản nút", "Liên kết nút", "Trạng thái", "Thứ tự", "Ngày bắt đầu", "Ngày kết thúc", "Ngày tạo", "Ngày cập nhật"};
    private final String[] fieldNames = {"bannerId", "imageUrl", "title", "subtitle", "buttonText", "buttonLink", "isActive", "orderSequence", "startDate", "endDate", "created_at", "updated_at"};
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public BannerGUI() {
        this.bannerController = new BannerController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }
    
    public BannerGUI(BannerController bannerController) {
        this.bannerController = bannerController;
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }
    
    private void initComponents() {
        header = new ContentHeader("Quản lý banner");
        table = new CustomTable(columnNames, fieldNames, true);
        footer = new ContentFooter();
        calculatePageSize();
    }
    
    private void calculatePageSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int availableHeight = screenHeight - 240;
        int rowHeight = 90; // Tăng chiều cao dòng để hiển thị hình ảnh
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
        header.addAddButtonListener(e -> showAddBannerDialog());
        header.addSearchButtonListener(e -> performSearch());
        header.addSearchFieldListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performSearch();
            }
        });

        table.setEditActionListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) {
                Banner banner = filteredData.get(row);
                showEditBannerDialog(banner);
            }
        });

        table.setDeleteActionListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) {
                Banner banner = filteredData.get(row);
                deleteBanner(banner);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) {
                Banner banner = filteredData.get(row);
                showBannerDetailsDialog(banner);
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
        try {
            currentData = bannerController.getAllBanners();
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
                .filter(banner -> 
                    (banner.getTitle() != null && banner.getTitle().toLowerCase().contains(currentSearchText)) ||
                    (banner.getSubtitle() != null && banner.getSubtitle().toLowerCase().contains(currentSearchText)) ||
                    (banner.getButtonText() != null && banner.getButtonText().toLowerCase().contains(currentSearchText)) ||
                    (banner.getButtonLink() != null && banner.getButtonLink().toLowerCase().contains(currentSearchText)))
                .collect(java.util.stream.Collectors.toList());
        }
        
        currentPage = 1;
        updateTable();
    }
    
    private void updateTable() {
        List<Banner> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }
    
    private List<Banner> getCurrentPageData() {
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
    
    private void showAddBannerDialog() {
        BannerFormDialog dialog = new BannerFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Thêm banner mới", 
            null
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Banner newBanner = dialog.getBanner();
            if (bannerController.createBanner(newBanner)) {
                loadData();
            }
        }
    }
    
    private void showEditBannerDialog(Banner banner) {
        BannerFormDialog dialog = new BannerFormDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            "Chỉnh sửa banner", 
            banner
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Banner updatedBanner = dialog.getBanner();
            if (bannerController.updateBanner(updatedBanner)) {
                loadData();
            }
        }
    }
    
    private void showBannerDetailsDialog(Banner banner) {
        BannerDetailsDialog dialog = new BannerDetailsDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            banner
        );
        dialog.setVisible(true);
    }
    
    private void deleteBanner(Banner banner) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa banner \"" + banner.getTitle() + "\"?\nHành động này không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (bannerController.deleteBanner((int) banner.getBannerId())) {
                loadData();
            }
        }
    }
    
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
    
    public BannerController getBannerController() {
        return bannerController;
    }
    
    public void setBannerController(BannerController bannerController) {
        this.bannerController = bannerController;
        loadData();
    }
}
