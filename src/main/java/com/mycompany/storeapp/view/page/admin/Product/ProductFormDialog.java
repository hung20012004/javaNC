package com.mycompany.storeapp.view.page.admin.Product;

import com.mycompany.storeapp.controller.admin.ProductController;
import com.mycompany.storeapp.controller.admin.CategoryController;
import com.mycompany.storeapp.controller.admin.MaterialController;
import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.model.entity.Product;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.model.entity.ProductImage;
import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.model.entity.Material;
import com.mycompany.storeapp.model.entity.Color;
import com.mycompany.storeapp.model.entity.Size;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Main ProductFormDialog class - handles the overall dialog structure and coordination
 */
public class ProductFormDialog extends JDialog {
    
    // Enum for dialog modes
    public enum DialogMode {
        ADD("Thêm sản phẩm mới"),
        EDIT("Chỉnh sửa sản phẩm"),
        VIEW("Chi tiết sản phẩm");
        
        private final String title;
        
        DialogMode(String title) {
            this.title = title;
        }
        
        public String getTitle() {
            return title;
        }
    }
    
    // Core data
    private Product product;
    private List<ProductVariant> variants;
    private List<ProductImage> productImages;
    private boolean confirmed = false;
    private DialogMode mode;
    
    // Controllers
    private ProductController productController;
    private ProductVariantController productVariantController;
    private CategoryController categoryController;
    private MaterialController materialController;
    private ColorController colorController;
    private SizeController sizeController;
    private ProductImageController imageController;
    
    // Components
    private ProductFormPanel productFormPanel;
    private ProductVariantPanel variantPanel;
    private ProductImagePanel imagePanel;
    private JTabbedPane tabbedPane;
    
    // Form Buttons
    private JButton saveButton;
    private JButton editButton;
    private JButton cancelButton;
    
    // Constructors
    public ProductFormDialog(JFrame parent, String title, Product product) {
        this(parent, title, product, product == null ? DialogMode.ADD : DialogMode.VIEW);
    }
    
    public ProductFormDialog(JFrame parent, String title, Product product, DialogMode mode) {
        super(parent, title, true);
        this.product = product;
        this.mode = mode;
        this.variants = new ArrayList<>();
        this.productImages = new ArrayList<>();
        
        initControllers();
        initComponents();
        setupLayout();
        setupEventListeners();
        
        if (product != null) {
            loadProductData();
        }
        
        configureFormForMode();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(parent);
    }
    
    private void initControllers() {
        productController = new ProductController();
        productVariantController = new ProductVariantController();
        categoryController = new CategoryController();
        materialController = new MaterialController();
        colorController = new ColorController();
        sizeController = new SizeController();
        imageController = new ProductImageController();
    }
    
    private void initComponents() {
        // Initialize panels
        productFormPanel = new ProductFormPanel(categoryController, materialController);
        variantPanel = new ProductVariantPanel(productVariantController, colorController, sizeController);
        imagePanel = new ProductImagePanel(imageController);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Form Buttons
        saveButton = new JButton("Lưu");
        editButton = new JButton("Chỉnh sửa");
        cancelButton = new JButton("Hủy");
        
        // Style buttons
        setupButtonStyles();
    }
    
    private void setupButtonStyles() {
        saveButton.setBackground(new java.awt.Color(76, 175, 80));
        saveButton.setForeground(java.awt.Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 35));
        
        editButton.setBackground(new java.awt.Color(255, 193, 7));
        editButton.setForeground(java.awt.Color.BLACK);
        editButton.setFocusPainted(false);
        editButton.setPreferredSize(new Dimension(100, 35));
        
        cancelButton.setBackground(new java.awt.Color(244, 67, 54));
        cancelButton.setForeground(java.awt.Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 35));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Add tabs
        tabbedPane.addTab("Thông tin sản phẩm", productFormPanel);
        tabbedPane.addTab("Biến thể (" + variants.size() + ")", variantPanel);
        tabbedPane.addTab("Hình ảnh (" + productImages.size() + ")", imagePanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        if (mode == DialogMode.VIEW) {
            panel.add(editButton);
        } else {
            panel.add(saveButton);
        }
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Save button
        saveButton.addActionListener(e -> saveProduct());
        
        // Edit button (in view mode)
        editButton.addActionListener(e -> switchToEditMode());
        
        // Cancel button
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Listen to variant changes to update tab title
        variantPanel.addVariantChangeListener(() -> updateTabTitles());
        
        // Listen to image changes to update tab title
        imagePanel.addImageChangeListener(() -> updateTabTitles());
    }
    
    private void configureFormForMode() {
        boolean isEditable = (mode == DialogMode.ADD || mode == DialogMode.EDIT);
        
        // Configure panels based on mode
        productFormPanel.setEditable(isEditable);
        variantPanel.setEditable(isEditable);
        imagePanel.setEditable(isEditable);
        
        // Update dialog title
        setTitle(mode.getTitle());
    }
    
    private void switchToEditMode() {
        mode = DialogMode.EDIT;
        configureFormForMode();
        
        // Update button panel
        remove(((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.SOUTH));
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    private void loadProductData() {
        if (product == null) return;
        
        // Populate product form
        productFormPanel.populateForm(product);
        
        // Load variants
        loadVariants();
        
        // Load images
        loadImages();
        
        // Update tab titles
        updateTabTitles();
    }
    
    private void loadVariants() {
        try {
            variants = productVariantController.getVariantsByProductId(product.getProductId());
            if (variants == null) {
                variants = new ArrayList<>();
            }
            variantPanel.setVariants(variants);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải variants: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            variants = new ArrayList<>();
        }
    }
    
    private void loadImages() {
        try {
            productImages = imageController.getImagesByProductId(product.getProductId());
            if (productImages == null) {
                productImages = new ArrayList<>();
            }
            imagePanel.setImages(productImages);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải hình ảnh: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            productImages = new ArrayList<>();
        }
    }
    
    private void updateTabTitles() {
        tabbedPane.setTitleAt(1, "Biến thể (" + variantPanel.getVariantCount() + ")");
        tabbedPane.setTitleAt(2, "Hình ảnh (" + imagePanel.getImageCount() + ")");
    }
    
    private void saveProduct() {
        try {
            // Validate form
            if (!productFormPanel.validateForm()) {
                return;
            }
            
            // Get product data from form
            Product productToSave = productFormPanel.getProductFromForm();
            
            boolean success = false;
            
            if (mode == DialogMode.ADD) {
                // Create new product
                success = productController.createProduct(productToSave);
                if (success) {
                    // Get the created product (with ID)
                    this.product = productToSave;
                    
                    // Save variants
                    variantPanel.saveVariants(product.getProductId());
                    
                    // Save images
                    imagePanel.saveImages(product.getProductId());
                    
                    JOptionPane.showMessageDialog(this, 
                        "Tạo sản phẩm thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (mode == DialogMode.EDIT) {
                // Update existing product
                productToSave.setProductId(product.getProductId());
                productToSave.setCreatedAt(product.getCreatedAt());
                
                success = productController.updateProduct(productToSave);
                if (success) {
                    this.product = productToSave;
                    
                    // Save variants
                    variantPanel.saveVariants(product.getProductId());
                    
                    // Save images
                    imagePanel.saveImages(product.getProductId());
                    
                    JOptionPane.showMessageDialog(this, 
                        "Cập nhật sản phẩm thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (success) {
                confirmed = true;
                dispose();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi lưu sản phẩm: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Getters
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public List<ProductVariant> getVariants() {
        return variants;
    }
    
    public List<ProductImage> getProductImages() {
        return productImages;
    }
    
    public DialogMode getMode() {
        return mode;
    }
}