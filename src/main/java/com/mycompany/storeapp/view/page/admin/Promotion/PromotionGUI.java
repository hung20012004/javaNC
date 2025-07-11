package com.mycompany.storeapp.view.page.admin.Promotion;

import com.mycompany.storeapp.controller.admin.PromotionController;
import com.mycompany.storeapp.model.entity.Promotion;
import com.mycompany.storeapp.service.ExcelExporter;
import com.mycompany.storeapp.view.component.CustomTable;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentFooter;
import com.mycompany.storeapp.view.component.admin.ContentComponent.ContentHeader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PromotionGUI extends JPanel {
    private PromotionController promotionController;
    private ContentHeader header;
    private CustomTable table;
    private ContentFooter footer;
    private List<Promotion> currentData;
    private List<Promotion> filteredData;
    private String currentSearchText = "";
    private int currentPage = 1;
    private int pageSize = 12;

    private final String[] columnNames = {"ID", "Mã khuyến mãi", "Tên khuyến mãi", "Loại giảm giá", "Giá trị giảm giá", "Trạng thái", "Ngày bắt đầu", "Ngày kết thúc"};
    private final String[] fieldNames = {"promotionId", "code", "name", "discountType", "discountValue", "active", "startDate", "endDate"};

    public PromotionGUI() {
        this.promotionController = new PromotionController();
        this.currentData = new ArrayList<>();
        this.filteredData = new ArrayList<>();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        header = new ContentHeader("Quản lý khuyến mãi");
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
        header.addAddButtonListener(e -> showAddPromotionDialog());
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
            List<Promotion> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                Promotion promotion = pageData.get(row);
                showEditPromotionDialog(promotion);
            }
        });

        table.setDeleteActionListener(e -> {
            int row = e.getID();
            List<Promotion> pageData = getCurrentPageData();
            if (row >= 0 && row < pageData.size()) {
                int promotionId = pageData.get(row).getPromotionId();
                deletePromotion(promotionId);
            }
        });

        table.setRowDoubleClickListener(e -> {
            int row = e.getID();
            if (row >= 0 && row < filteredData.size()) 
                showPromotionDetailsDialog(filteredData.get(row));
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
        currentData = promotionController.getAllPromotions() != null ? promotionController.getAllPromotions() : new ArrayList<>();
        filteredData = new ArrayList<>(currentData);
        updateTable();
    }

    private void performSearch() {
        currentSearchText = header.getSearchText().toLowerCase();
        filteredData = currentSearchText.isEmpty() ? new ArrayList<>(currentData) :
            currentData.stream().filter(p -> 
                p.getCode().toLowerCase().contains(currentSearchText) ||
                p.getName().toLowerCase().contains(currentSearchText) ||
                (p.getDescription() != null && p.getDescription().toLowerCase().contains(currentSearchText)) ||
                p.getDiscountType().toLowerCase().contains(currentSearchText))
                .collect(java.util.stream.Collectors.toList());
        currentPage = 1;
        updateTable();
    }

    private void updateTable() {
        List<Promotion> pageData = getCurrentPageData();
        table.setData(pageData);
        footer.updatePagination(filteredData.size(), currentPage, pageSize);
    }

    private List<Promotion> getCurrentPageData() {
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

    private void showAddPromotionDialog() {
        PromotionFormDialog dialog = new PromotionFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Thêm khuyến mãi mới", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (promotionController.createPromotion(dialog.getPromotion())) 
                loadData();
        }
    }

    private void showEditPromotionDialog(Promotion promotion) {
        PromotionFormDialog dialog = new PromotionFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa khuyến mãi", promotion);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            if (promotionController.updatePromotion(dialog.getPromotion())) 
                loadData();
        }
    }

    private void showPromotionDetailsDialog(Promotion promotion) {
        StringBuilder details = new StringBuilder();
        details.append("Mã khuyến mãi: ").append(promotion.getCode()).append("\n");
        details.append("Tên khuyến mãi: ").append(promotion.getName()).append("\n");
        details.append("Mô tả: ").append(promotion.getDescription() != null ? promotion.getDescription() : "Không có").append("\n");
        details.append("Loại giảm giá: ").append(promotion.getDiscountType()).append("\n");
        details.append("Giá trị giảm giá: ").append(promotion.getDiscountValue()).append("\n");
        details.append("Giá trị đơn hàng tối thiểu: ").append(promotion.getMinOrderValue()).append("\n");
        details.append("Giá trị giảm giá tối đa: ").append(promotion.getMaxDiscount()).append("\n");
        details.append("Giới hạn sử dụng: ").append(promotion.getUsageLimit()).append("\n");
        details.append("Đã sử dụng: ").append(promotion.getUsedCount()).append("\n");
        details.append("Trạng thái: ").append(promotion.isActive() ? "Đang hoạt động" : "Không hoạt động").append("\n");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (promotion.getStartDate() != null) {
            details.append("Ngày bắt đầu: ").append(promotion.getStartDate().format(formatter)).append("\n");
        }
        if (promotion.getEndDate() != null) {
            details.append("Ngày kết thúc: ").append(promotion.getEndDate().format(formatter)).append("\n");
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Chi tiết khuyến mãi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deletePromotion(int id) {
        if (promotionController.deletePromotion(id)) 
            loadData();
    }
    
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file Excel");
        fileChooser.setSelectedFile(new File("promotions.xlsx"));
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