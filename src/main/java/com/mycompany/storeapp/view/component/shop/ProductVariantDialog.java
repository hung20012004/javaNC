package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.Color;
import com.mycompany.storeapp.model.entity.Size;
import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProductVariantDialog extends JDialog {
    private static final java.awt.Color BACKGROUND_COLOR = java.awt.Color.WHITE;
    private static final java.awt.Color BORDER_COLOR = new java.awt.Color(229, 231, 235);
    private static final java.awt.Color PRIMARY_COLOR = new java.awt.Color(59, 130, 246);
    private static final java.awt.Color TEXT_COLOR = new java.awt.Color(55, 65, 81);
    private static final java.awt.Color SUCCESS_COLOR = new java.awt.Color(16, 185, 129);
    private static final java.awt.Color WARNING_COLOR = new java.awt.Color(245, 158, 11);

    private final Product product;
    private final DecimalFormat currencyFormat;
    private final Consumer<ProductVariant> addToCartCallback;
    private final ColorController colorController;
    private final SizeController sizeController;
    private final ProductVariantController variantController;
    
    // UI Components
    private JComboBox<String> colorComboBox;
    private JComboBox<String> sizeComboBox;
    private JLabel priceLabel;
    private JLabel stockLabel;
    private JButton addButton;
    private ProductVariant selectedVariant;
    
    // Cached data
    private List<Color> allColors;
    private List<Size> allSizes;
    private List<ProductVariant> productVariants;
    private boolean isUpdating = false;

    public ProductVariantDialog(JFrame parent, Product product, DecimalFormat currencyFormat, 
                              Consumer<ProductVariant> addToCartCallback, ColorController colorController,
                              SizeController sizeController, ProductVariantController variantController) {
        super(parent, "Chọn biến thể - " + product.getName(), true);
        this.product = product;
        this.currencyFormat = currencyFormat;
        this.addToCartCallback = addToCartCallback;
        this.colorController = colorController;
        this.sizeController = sizeController;
        this.variantController = variantController;
        
        System.out.println("ProductVariantDialog constructor called"); // Debug
        
        setupUI();
        loadData();
        initializeComponents();
        
        System.out.println("ProductVariantDialog initialized"); // Debug
    }

    private void loadData() {
        try {
            allColors = colorController.getAllColors();
            allSizes = sizeController.getAllSizes();
            productVariants = variantController.getVariantsByProductId(product.getProductId());
            
            // Debug: In ra thông tin để kiểm tra
            System.out.println("Loaded colors: " + (allColors != null ? allColors.size() : "null"));
            System.out.println("Loaded sizes: " + (allSizes != null ? allSizes.size() : "null"));
            System.out.println("Loaded variants: " + (productVariants != null ? productVariants.size() : "null"));
            
        } catch (Exception e) {
            e.printStackTrace(); // In ra stack trace để debug
            JOptionPane.showMessageDialog(this, 
                "Không thể tải dữ liệu sản phẩm: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            // Không dispose ngay, cho phép xem lỗi
            // dispose();
        }
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        // Đảm bảo dialog luôn hiển thị trên top
        setAlwaysOnTop(true);
        
        System.out.println("Dialog setup completed"); // Debug
    }

    private void initializeComponents() {
        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        // Initialize combo boxes
        populateComboBoxes();
        
        // Add listeners
        setupEventListeners();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Product name
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(createLabel("Sản phẩm:", Font.BOLD), gbc);
        gbc.gridx = 1;
        mainPanel.add(createLabel(product.getName(), Font.PLAIN), gbc);
        
        // Color selection
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(createLabel("Màu sắc:", Font.BOLD), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        colorComboBox = createComboBox();
        mainPanel.add(colorComboBox, gbc);
        
        // Size selection
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(createLabel("Kích thước:", Font.BOLD), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        sizeComboBox = createComboBox();
        mainPanel.add(sizeComboBox, gbc);
        
        // Price display
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(createLabel("Giá:", Font.BOLD), gbc);
        gbc.gridx = 1;
        priceLabel = createLabel("-", Font.PLAIN);
        priceLabel.setForeground(SUCCESS_COLOR);
        mainPanel.add(priceLabel, gbc);
        
        // Stock display
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(createLabel("Tồn kho:", Font.BOLD), gbc);
        gbc.gridx = 1;
        stockLabel = createLabel("-", Font.PLAIN);
        mainPanel.add(stockLabel, gbc);
        
        return mainPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton cancelButton = createButton("Hủy", BORDER_COLOR, TEXT_COLOR);
        cancelButton.addActionListener(e -> dispose());

        addButton = createButton("Thêm vào giỏ", PRIMARY_COLOR, java.awt.Color.WHITE);
        addButton.setEnabled(false);
        addButton.addActionListener(this::handleAddToCart);

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);
        
        return buttonPanel;
    }

    private JLabel createLabel(String text, int fontStyle) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", fontStyle, 12));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBackground(java.awt.Color.WHITE);
        comboBox.setPreferredSize(new Dimension(200, 30));
        return comboBox;
    }

    private JButton createButton(String text, java.awt.Color backgroundColor, java.awt.Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    private void setupEventListeners() {
        colorComboBox.addActionListener(e -> {
            if (!isUpdating) {
                handleColorSelection();
            }
        });

        sizeComboBox.addActionListener(e -> {
            if (!isUpdating) {
                handleSizeSelection();
            }
        });
    }

    private void populateComboBoxes() {
        populateColors(null);
        populateSizes(null);
    }

    private void populateColors(Integer filterSizeId) {
        isUpdating = true;
        String selectedColor = (String) colorComboBox.getSelectedItem();
        
        colorComboBox.removeAllItems();
        colorComboBox.addItem("-- Chọn màu sắc --");
        
        List<Long> availableColorIds = productVariants.stream()
            .filter(variant -> filterSizeId == null || variant.getSizeId()==filterSizeId)
            .map(ProductVariant::getColorId)
            .distinct()
            .collect(Collectors.toList());
        
        if (allColors != null) {
            for (Color color : allColors) {
                if (availableColorIds.contains(color.getColorId())) {
                    colorComboBox.addItem(color.getName());
                }
            }
        }
        
        // Restore selection if possible
        if (selectedColor != null && !selectedColor.startsWith("--")) {
            colorComboBox.setSelectedItem(selectedColor);
        }
        
        isUpdating = false;
    }

    private void populateSizes(Long filterColorId) {
        isUpdating = true;
        String selectedSize = (String) sizeComboBox.getSelectedItem();
        
        sizeComboBox.removeAllItems();
        sizeComboBox.addItem("-- Chọn kích thước --");
        
        List<Integer> availableSizeIds = productVariants.stream()
            .filter(variant -> filterColorId == null || variant.getColorId()==filterColorId)
            .map(ProductVariant::getSizeId)
            .distinct()
            .collect(Collectors.toList());
        
        if (allSizes != null) {
            for (Size size : allSizes) {
                if (availableSizeIds.contains(size.getSizeId())) {
                    sizeComboBox.addItem(size.getName());
                }
            }
        }
        
        // Restore selection if possible
        if (selectedSize != null && !selectedSize.startsWith("--")) {
            sizeComboBox.setSelectedItem(selectedSize);
        }
        
        isUpdating = false;
    }

    private void handleColorSelection() {
        String selectedColorName = (String) colorComboBox.getSelectedItem();
        
        if (selectedColorName != null && !selectedColorName.startsWith("--")) {
            Color selectedColor = findColorByName(selectedColorName);
            if (selectedColor != null) {
                populateSizes(selectedColor.getColorId());
            }
        } else {
            populateSizes(null);
        }
        
        updateVariantInfo();
    }

    private void handleSizeSelection() {
        String selectedSizeName = (String) sizeComboBox.getSelectedItem();
        
        if (selectedSizeName != null && !selectedSizeName.startsWith("--")) {
            Size selectedSize = findSizeByName(selectedSizeName);
            if (selectedSize != null) {
                populateColors(selectedSize.getSizeId());
            }
        } else {
            populateColors(null);
        }
        
        updateVariantInfo();
    }

    private void updateVariantInfo() {
        String selectedColorName = (String) colorComboBox.getSelectedItem();
        String selectedSizeName = (String) sizeComboBox.getSelectedItem();
        
        if (selectedColorName == null || selectedColorName.startsWith("--") ||
            selectedSizeName == null || selectedSizeName.startsWith("--")) {
            clearVariantInfo();
            return;
        }
        
        Color selectedColor = findColorByName(selectedColorName);
        Size selectedSize = findSizeByName(selectedSizeName);
        
        if (selectedColor == null || selectedSize == null) {
            clearVariantInfo();
            return;
        }
        
        selectedVariant = findVariant(selectedColor.getColorId(), selectedSize.getSizeId());
        
        if (selectedVariant != null) {
            displayVariantInfo(selectedVariant);
        } else {
            clearVariantInfo();
        }
    }

    private void displayVariantInfo(ProductVariant variant) {
        priceLabel.setText(currencyFormat.format(variant.getPrice()));
        priceLabel.setForeground(SUCCESS_COLOR);
        
        int stock = variant.getStockQuantity();
        stockLabel.setText(String.valueOf(stock));
        
        if (stock > 0) {
            stockLabel.setForeground(SUCCESS_COLOR);
            addButton.setEnabled(true);
        } else {
            stockLabel.setForeground(WARNING_COLOR);
            addButton.setEnabled(false);
        }
    }

    private void clearVariantInfo() {
        priceLabel.setText("-");
        priceLabel.setForeground(TEXT_COLOR);
        stockLabel.setText("-");
        stockLabel.setForeground(TEXT_COLOR);
        addButton.setEnabled(false);
        selectedVariant = null;
    }

    private Color findColorByName(String name) {
        return allColors.stream()
            .filter(color -> color.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    private Size findSizeByName(String name) {
        return allSizes.stream()
            .filter(size -> size.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    private ProductVariant findVariant(Long colorId, Integer sizeId) {
        return productVariants.stream()
            .filter(variant -> variant.getColorId()==colorId && 
                             variant.getSizeId()==sizeId)
            .findFirst()
            .orElse(null);
    }

    private void handleAddToCart(java.awt.event.ActionEvent e) {
        if (selectedVariant == null) {
            showWarningMessage("Vui lòng chọn màu sắc và kích thước!");
            return;
        }
        
        if (selectedVariant.getStockQuantity() <= 0) {
            showWarningMessage("Sản phẩm đã hết hàng!");
            return;
        }
        
        try {
            addToCartCallback.accept(selectedVariant);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Không thể thêm sản phẩm vào giỏ hàng: " + ex.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }
}