 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Product;

import com.mycompany.storeapp.controller.admin.ProductController;
import com.mycompany.storeapp.controller.admin.CategoryController;
import com.mycompany.storeapp.controller.admin.MaterialController;
import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.model.entity.Material;
import com.mycompany.storeapp.model.entity.Color;
import com.mycompany.storeapp.model.entity.Size;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;

/**
 * Dialog để thêm/sửa sản phẩm với thông tin chi tiết và variant
 * @author Hi
 */
public class ProductFormDialog extends JDialog {
    private Product product;
    private List<ProductVariant> variants;
    private boolean confirmed = false;
    private boolean isEditMode = false;
    
    // Controllers
    private ProductController productController;
    private ProductVariantController productVariantController;
    private CategoryController categoryController;
    private MaterialController materialController;
    private ColorController colorController;
    private SizeController sizeController;
    
    // Product Info Components
    private JTextField nameField;
    private JTextField brandField;
    private JComboBox<Category> categoryCombo;
    private JComboBox<Material> materialCombo;
    private JComboBox<String> genderCombo;
    private JTextField slugField;
    private JTextArea descriptionArea;
    private JTextArea careInstructionArea;
    private JFormattedTextField priceField;
    private JFormattedTextField salePriceField;
    private JSpinner stockQuantitySpinner;
    private JTextField skuField;
    private JSpinner minPurchaseSpinner;
    private JSpinner maxPurchaseSpinner;
    private JCheckBox activeCheckBox;
    
    // Variant Components
    private JTable variantTable;
    private DefaultTableModel variantTableModel;
    private JButton addVariantButton;
    private JButton editVariantButton;
    private JButton deleteVariantButton;
    private JButton uploadImageButton;
    private JLabel imagePreviewLabel;
    private JPanel imagePanel;
    private JScrollPane imageScrollPane;
    private JComboBox<Color> colorCombo;
    private JComboBox<Size> sizeCombo;
    private JFormattedTextField variantPriceField;
    private JSpinner variantStockSpinner;
    private JTextField variantImageField;
    private JButton variantAddSaveButton;
    
    // Form Buttons
    private JButton saveButton;
    private JButton cancelButton;
    
    // Constants
    private static final String[] GENDER_OPTIONS = {"Nam", "Nữ", "Unisex"};
    private static final String[] VARIANT_COLUMNS = {"Màu sắc", "Kích thước", "Giá", "Số lượng", "Hình ảnh"};
    
    public ProductFormDialog(JFrame parent, String title, Product product) {
        super(parent, title, true);
        this.product = product;
        this.isEditMode = (product != null);
        this.variants = new ArrayList<>();
        
        initControllers();
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        if (isEditMode) {
            populateForm();
            loadVariants();
        }
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(parent);
    }
    
    private void initControllers() {
        productController = new ProductController();
        productVariantController = new ProductVariantController();
        categoryController = new CategoryController();
        materialController = new MaterialController();
        colorController = new ColorController();
        sizeController = new SizeController();
    }
    
    private void initComponents() {
        // Product Info Components
        nameField = new JTextField(20);
        brandField = new JTextField(20);
        categoryCombo = new JComboBox<>();
        materialCombo = new JComboBox<>();
        genderCombo = new JComboBox<>(GENDER_OPTIONS);
        slugField = new JTextField(20);
        descriptionArea = new JTextArea(4, 20);
        careInstructionArea = new JTextArea(3, 20);
        
        // Format currency fields
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        priceField = new JFormattedTextField(currencyFormat);
        salePriceField = new JFormattedTextField(currencyFormat);
        
        stockQuantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        skuField = new JTextField(20);
        minPurchaseSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        maxPurchaseSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 999, 1));
        activeCheckBox = new JCheckBox("Kích hoạt", true);
        
        // Variant Components
        variantTableModel = new DefaultTableModel(VARIANT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        variantTable = new JTable(variantTableModel);
        variantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        variantTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        variantTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        variantTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        variantTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        variantTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        variantTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        addVariantButton = new JButton("Thêm Variant");
        editVariantButton = new JButton("Sửa Variant");
        deleteVariantButton = new JButton("Xóa Variant");
        uploadImageButton = new JButton("Upload Hình ảnh");
        
        // Image Preview
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(200, 200));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setText("Không có hình ảnh");
        
        imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imageScrollPane = new JScrollPane(imagePanel);
        imageScrollPane.setPreferredSize(new Dimension(400, 150));
        
        // Form Buttons
        saveButton = new JButton("Lưu");
        cancelButton = new JButton("Hủy");
        
        // Style components
        setupComponentStyles();
    }
    
    private void setupComponentStyles() {
        // Set text area properties
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        careInstructionArea.setWrapStyleWord(true);
        careInstructionArea.setLineWrap(true);
        
        // Set button styles
        saveButton.setBackground(new java.awt.Color(76, 175, 80));
        saveButton.setForeground(java.awt.Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setBackground(new java.awt.Color(244, 67, 54));
        cancelButton.setForeground(java.awt.Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        addVariantButton.setBackground(new java.awt.Color(33, 150, 243));
        addVariantButton.setForeground(java.awt.Color.WHITE);
        addVariantButton.setFocusPainted(false);
        
        editVariantButton.setBackground(new java.awt.Color(255, 193, 7));
        editVariantButton.setForeground(java.awt.Color.BLACK);
        editVariantButton.setFocusPainted(false);
        
        deleteVariantButton.setBackground(new java.awt.Color(244, 67, 54));
        deleteVariantButton.setForeground(java.awt.Color.WHITE);
        deleteVariantButton.setFocusPainted(false);
        
        uploadImageButton.setBackground(new java.awt.Color(156, 39, 176));
        uploadImageButton.setForeground(java.awt.Color.WHITE);
        uploadImageButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Product Info Tab
        JPanel productPanel = createProductInfoPanel();
        tabbedPane.addTab("Thông tin sản phẩm", productPanel);
        
        // Variants Tab
        JPanel variantPanel = createVariantPanel();
        tabbedPane.addTab("Variants", variantPanel);
        
        // Images Tab
        JPanel imageTabPanel = createImagePanel();
        tabbedPane.addTab("Hình ảnh", imageTabPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createProductInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Name and Brand
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên sản phẩm:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Thương hiệu:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(brandField, gbc);
        
        // Row 2: Category and Material
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Danh mục:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(categoryCombo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Chất liệu:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(materialCombo, gbc);
        
        // Row 3: Gender and SKU
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(genderCombo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("SKU:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(skuField, gbc);
        
        // Row 4: Slug
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Slug:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(slugField, gbc);
        
        // Row 5: Price and Sale Price
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Giá khuyến mãi:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(salePriceField, gbc);
        
        // Row 6: Stock and Purchase Limits
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Số lượng tồn:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(stockQuantitySpinner, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Kích hoạt:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(activeCheckBox, gbc);
        
        // Row 7: Min and Max Purchase
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Số lượng mua tối thiểu:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(minPurchaseSpinner, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Số lượng mua tối đa:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(maxPurchaseSpinner, gbc);
        
        // Row 8: Description
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Row 9: Care Instructions
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        formPanel.add(new JLabel("Hướng dẫn bảo quản:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        formPanel.add(new JScrollPane(careInstructionArea), gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createVariantPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Variant Table
        JScrollPane scrollPane = new JScrollPane(variantTable);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Variant Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addVariantButton);
        buttonPanel.add(editVariantButton);
        buttonPanel.add(deleteVariantButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Image preview and upload
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(imagePreviewLabel);
        
        JPanel uploadPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        uploadPanel.add(uploadImageButton);
        uploadPanel.add(new JLabel("Hình ảnh sản phẩm"));
        topPanel.add(uploadPanel);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Image gallery
        JPanel galleryPanel = new JPanel(new BorderLayout());
        galleryPanel.setBorder(new TitledBorder("Hình ảnh Variants"));
        galleryPanel.add(imageScrollPane, BorderLayout.CENTER);
        
        panel.add(galleryPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Save button
        saveButton.addActionListener(e -> saveProduct());
        
        // Cancel button
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Name field - auto generate slug
        nameField.addActionListener(e -> generateSlug());
        
        // Variant buttons
//        addVariantButton.addActionListener(e -> showAddVariantDialog());
//        editVariantButton.addActionListener(e -> showEditVariantDialog());
        deleteVariantButton.addActionListener(e -> deleteSelectedVariant());
        
        // Upload image button
        uploadImageButton.addActionListener(e -> uploadImage());
        
        // Table selection
        variantTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }
    
    private void loadData() {
        // Load categories
        try {
            List<Category> categories = categoryController.getAllCategories();
            categoryCombo.removeAllItems();
            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh mục: " + e.getMessage());
        }
        
        // Load materials
        try {
            List<Material> materials = materialController.getAllMaterials();
            materialCombo.removeAllItems();
            for (Material material : materials) {
                materialCombo.addItem(material);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải chất liệu: " + e.getMessage());
        }
    }
    
    private void populateForm() {
        if (product == null) return;
        
        nameField.setText(product.getName());
        brandField.setText(product.getBrand());
        genderCombo.setSelectedItem(product.getGender());
        slugField.setText(product.getSlug());
        descriptionArea.setText(product.getDescription());
        careInstructionArea.setText(product.getCareInstruction());
        priceField.setValue(product.getPrice());
        salePriceField.setValue(product.getSalePrice());
        stockQuantitySpinner.setValue(product.getStockQuantity());
        skuField.setText(product.getSku());
        minPurchaseSpinner.setValue(product.getMinPurchaseQuantity());
        maxPurchaseSpinner.setValue(product.getMaxPurchaseQuantity());
        activeCheckBox.setSelected(product.isActive());
        
        // Set category
        if (product.getCategoryId() != null) {
            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                Category category = categoryCombo.getItemAt(i);
                if (category.getCategoryId() == product.getCategoryId()) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Set material
        for (int i = 0; i < materialCombo.getItemCount(); i++) {
            Material material = materialCombo.getItemAt(i);
            if (material.getMaterialId() == product.getMaterialId()) {
                materialCombo.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private void loadVariants() {
        if (product == null) return;
        
        try {
            variants = productVariantController.getVariantsByProductId(product.getProductId());
            refreshVariantTable();
            loadVariantImages();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải variants: " + e.getMessage());
        }
    }
    
    private void refreshVariantTable() {
        variantTableModel.setRowCount(0);
        
        for (ProductVariant variant : variants) {
            Object[] row = {
                variant.getVariantId(),
                variant.getColor() != null ? variant.getColor().getName() : "N/A",
                variant.getSize() != null ? variant.getSize().getName() : "N/A",
//                formatCurrency(variant.getPrice()),
                variant.getStockQuantity(),
                variant.getImageUrl() != null ? "Có hình ảnh" : "Không có"
            };
            variantTableModel.addRow(row);
        }
        
        updateButtonStates();
    }
    
    private void loadVariantImages() {
        imagePanel.removeAll();
        
        for (ProductVariant variant : variants) {
            if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty()) {
                try {
                    JLabel imageLabel = createImageLabel(variant.getImageUrl());
                    imagePanel.add(imageLabel);
                } catch (Exception e) {
                    System.err.println("Lỗi khi tải hình ảnh variant: " + e.getMessage());
                }
            }
        }
        
        imagePanel.revalidate();
        imagePanel.repaint();
    }
    
    private JLabel createImageLabel(String imagePath) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(100, 100));
        label.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        
        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            label.setText("Lỗi ảnh");
        }
        
        return label;
    }
    
    private void generateSlug() {
        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            String slug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
            slugField.setText(slug);
        }
    }
    
    private void deleteSelectedVariant() {
        int selectedRow = variantTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa variant này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                variants.remove(selectedRow);
                refreshVariantTable();
            }
        }
    }
    
    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // Load and display image
                BufferedImage image = ImageIO.read(file);
                Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                imagePreviewLabel.setText("");
                
                // Store image path (in real application, you would upload to server)
                // For now, just store the local path
                imagePreviewLabel.putClientProperty("imagePath", file.getAbsolutePath());
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải hình ảnh: " + e.getMessage());
            }
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = variantTable.getSelectedRow() >= 0;
        editVariantButton.setEnabled(hasSelection);
        deleteVariantButton.setEnabled(hasSelection);
    }
    
    private void saveProduct() {
//        if (!validateForm()) {
//            return;
//        }
        
        try {
            // Create or update product
            if (product == null) {
                product = new Product();
            }
            
            // Set product fields
            product.setName(nameField.getText().trim());
            product.setBrand(brandField.getText().trim());
            product.setGender((String) genderCombo.getSelectedItem());
            product.setSlug(slugField.getText().trim());
            product.setDescription(descriptionArea.getText().trim());
            product.setCareInstruction(careInstructionArea.getText().trim());
            product.setPrice(((Number) priceField.getValue()).doubleValue());
            product.setSalePrice(((Number) salePriceField.getValue()).doubleValue());
            product.setStockQuantity((Integer) stockQuantitySpinner.getValue());
            product.setSku(skuField.getText().trim());
            product.setMinPurchaseQuantity((Integer) minPurchaseSpinner.getValue());
            product.setMaxPurchaseQuantity((Integer) maxPurchaseSpinner.getValue());
            product.setActive(activeCheckBox.isSelected());
            
            // Set category
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory != null) {
                product.setCategoryId(selectedCategory.getCategoryId());
            }
            
            // Set material
            Material selectedMaterial = (Material) materialCombo.getSelectedItem();
            if (selectedMaterial != null) {
                product.setMaterialId(selectedMaterial.getMaterialId());
            }
            
            // Set timestamps
            if (!isEditMode) {
                product.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            }
            product.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            confirmed = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu sản phẩm: " + e.getMessage());
        }
    }
    public boolean isConfirmed() {
        return confirmed;
    }
    public Product getProduct() {
        return product;
    }
}
