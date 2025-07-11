package com.mycompany.storeapp.view.page.admin.Product;

import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.controller.admin.ColorController;
import com.mycompany.storeapp.controller.admin.SizeController;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.model.entity.Color;
import com.mycompany.storeapp.model.entity.Size;
import com.mycompany.storeapp.view.component.CustomTable;
import com.mycompany.storeapp.service.CloudinaryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ProductVariantPanel extends JPanel {
    
    // Controllers
    private ProductVariantController variantController;
    private ColorController colorController;
    private SizeController sizeController;
    
    // Components
    private CustomTable variantTable;
    private JButton addButton;
    private JButton duplicateButton;
    
    // Form components for add/edit
    private JComboBox<Color> colorComboBox;
    private JComboBox<Size> sizeComboBox;
    private JTextField imageUrlField;
    private JTextField stockQuantityField;
    private JTextField priceField;
    private JButton selectImageButton;
    private JLabel imagePreviewLabel;
    private JProgressBar uploadProgressBar;
    private File selectedImageFile;
    
    // Data
    private List<ProductVariant> variants;
    private List<Color> colors;
    private List<Size> sizes;
    private boolean editable = true;
    
    // Listeners
    private List<Runnable> variantChangeListeners = new ArrayList<>();
    
    public ProductVariantPanel(ProductVariantController variantController, 
                             ColorController colorController, 
                             SizeController sizeController) {
        this.variantController = variantController;
        this.colorController = colorController;
        this.sizeController = sizeController;
        this.variants = new ArrayList<>();
        
        initComponents();
        loadData();
        setupLayout();
        setupEventListeners();
    }
    
    private void initComponents() {
        // Initialize table with column definitions
        String[] columnNames = {"Màu sắc", "Kích cỡ", "Hình ảnh", "Tồn kho", "Giá"};
        String[] fieldNames = {"color", "size", "imageUrl", "stockQuantity", "price"};
        variantTable = new CustomTable(columnNames, fieldNames, true);
        
        // Initialize buttons
        addButton = new JButton("Thêm biến thể");
        duplicateButton = new JButton("Nhân bản");
        
        // Style buttons
        addButton.setBackground(new java.awt.Color(76, 175, 80));
        addButton.setForeground(java.awt.Color.WHITE);
        addButton.setFocusPainted(false);
        
        duplicateButton.setBackground(new java.awt.Color(33, 150, 243));
        duplicateButton.setForeground(java.awt.Color.WHITE);
        duplicateButton.setFocusPainted(false);
        
        // Initialize form components
        colorComboBox = new JComboBox<>();
        sizeComboBox = new JComboBox<>();
        imageUrlField = new JTextField();
        stockQuantityField = new JTextField();
        priceField = new JTextField();
        
        // Initialize image components
        selectImageButton = new JButton("Chọn ảnh");
        imagePreviewLabel = new JLabel();
        uploadProgressBar = new JProgressBar(0, 100);
        
        // Style image button
        selectImageButton.setBackground(new java.awt.Color(156, 39, 176));
        selectImageButton.setForeground(java.awt.Color.WHITE);
        selectImageButton.setFocusPainted(false);
        
        // Style progress bar
        uploadProgressBar.setStringPainted(true);
        uploadProgressBar.setVisible(false);
        
        // Style preview label
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        imagePreviewLabel.setPreferredSize(new Dimension(120, 120));
        imagePreviewLabel.setText("Chưa có ảnh");
        
        // Set default values
        stockQuantityField.setText("0");
        priceField.setText("0.0");
        imageUrlField.setEditable(false); // Make URL field read-only
    }
    
    private void loadData() {
        try {
            // Load colors
            colors = colorController.getAllColors();
            colorComboBox.removeAllItems();
            if (colors != null) {
                for (Color color : colors) {
                    colorComboBox.addItem(color);
                }
            }
            
            // Load sizes
            sizes = sizeController.getAllSizes();
            sizeComboBox.removeAllItems();
            if (sizes != null) {
                for (Size size : sizes) {
                    sizeComboBox.addItem(size);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(addButton);
        topPanel.add(duplicateButton);
        
        add(topPanel, BorderLayout.NORTH);
        add(variantTable, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        addButton.addActionListener(e -> showAddVariantDialog());
        duplicateButton.addActionListener(e -> duplicateSelectedVariant());
        
        // Set up table action listeners
        variantTable.setEditActionListener(e -> {
            if (!editable) return; // Chỉ cho phép sửa khi editable = true
            int row = variantTable.getSelectedRowIndex();
            if (row >= 0 && row < variants.size()) {
                ProductVariant variant = variants.get(row);
                showVariantDialog(variant, "Chỉnh sửa biến thể");
            }
        });
        
        variantTable.setDeleteActionListener(e -> {
            if (!editable) return; // Chỉ cho phép xóa khi editable = true
            int row = variantTable.getSelectedRowIndex();
            if (row >= 0 && row < variants.size()) {
                deleteVariant(row);
            }
        });
        
        // Image selection listener
        selectImageButton.addActionListener(e -> selectImage());
    }

    private void duplicateSelectedVariant() {
        if (!editable) return;
        int row = variantTable.getSelectedRowIndex();
        if (row >= 0 && row < variants.size()) {
            ProductVariant original = variants.get(row);
            ProductVariant clone = cloneVariant(original);
            // Reset ID for new variant
            clone.setVariantId(0);
            if (showVariantDialog(clone, "Nhân bản biến thể")) {
                variants.add(clone);
                refreshTable();
                notifyVariantChanged();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một biến thể để nhân bản.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void refreshTable() {
        variantTable.setData(variants);
    }
    
    private void deleteVariant(int row) {
        if (!editable) return;
        
        int option = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa biến thể này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            variants.remove(row);
            refreshTable();
            notifyVariantChanged();
        }
    }
    
    private void showAddVariantDialog() {
        if (!editable) return;
        
        ProductVariant variant = new ProductVariant();
        if (showVariantDialog(variant, "Thêm biến thể Mới")) {
            variants.add(variant);
            refreshTable();
            notifyVariantChanged();
        }
    }
    
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh sản phẩm");
        
        // Set file filter for images
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Ảnh (*.jpg, *.jpeg, *.png, *.gif, *.bmp, *.webp)", 
            "jpg", "jpeg", "png", "gif", "bmp", "webp");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            
            // Validate file size (max 10MB)
            if (selectedImageFile.length() > 10 * 1024 * 1024) {
                JOptionPane.showMessageDialog(this, 
                    "Kích thước ảnh không được vượt quá 10MB!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                selectedImageFile = null;
                return;
            }
            
            // Load and display preview
            loadImagePreview(selectedImageFile);
        }
    }
    
    private void loadImagePreview(File imageFile) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage != null) {
                // Resize image for preview
                int previewWidth = 120;
                int previewHeight = 120;
                
                // Calculate aspect ratio
                double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
                if (aspectRatio > 1) {
                    previewHeight = (int) (previewWidth / aspectRatio);
                } else {
                    previewWidth = (int) (previewHeight * aspectRatio);
                }
                
                Image scaledImage = originalImage.getScaledInstance(previewWidth, previewHeight, Image.SCALE_SMOOTH);
                ImageIcon imageIcon = new ImageIcon(scaledImage);
                imagePreviewLabel.setIcon(imageIcon);
                imagePreviewLabel.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Không thể tải ảnh xem trước: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void uploadImageToCloudinary(Runnable onSuccess, Runnable onError) {
        if (selectedImageFile == null) {
            onError.run();
            return;
        }
        
        // Show progress bar
        uploadProgressBar.setVisible(true);
        uploadProgressBar.setValue(0);
        selectImageButton.setEnabled(false);
        
        // Upload in background thread
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return CloudinaryService.uploadImage(selectedImageFile, new CloudinaryService.ProgressCallback() {
                    @Override
                    public void onProgress(int percentage) {
                        SwingUtilities.invokeLater(() -> {
                            uploadProgressBar.setValue(percentage);
                            uploadProgressBar.setString(percentage + "%");
                        });
                    }
                });
            }
            
            @Override
            protected void done() {
                try {
                    String imageUrl = get();
                    if (imageUrl != null) {
                        imageUrlField.setText(imageUrl);
                        onSuccess.run();
                    } else {
                        onError.run();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ProductVariantPanel.this,
                        "Lỗi khi upload ảnh: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    onError.run();
                } finally {
                    // Reset UI
                    uploadProgressBar.setVisible(false);
                    selectImageButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private boolean showVariantDialog(ProductVariant variant, String title) {
        JDialog dialog = new JDialog((JDialog) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Color
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Màu sắc:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(colorComboBox, gbc);
        
        // Size
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Kích cỡ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(sizeComboBox, gbc);
        
        // Image selection panel
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Hình ảnh:"), gbc);
        
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        
        // Image preview and button panel
        JPanel imageControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imageControlPanel.add(imagePreviewLabel);
        
        JPanel imageButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        imageButtonPanel.add(selectImageButton);
        imageButtonPanel.add(uploadProgressBar);
        
        imageControlPanel.add(imageButtonPanel);
        imagePanel.add(imageControlPanel, BorderLayout.NORTH);
        
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(imagePanel, gbc);
        
        // Image URL (read-only)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("URL ảnh:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(imageUrlField, gbc);
        
        // Stock Quantity
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Tồn kho:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(stockQuantityField, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(priceField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        saveButton.setBackground(new java.awt.Color(76, 175, 80));
        saveButton.setForeground(java.awt.Color.WHITE);
        saveButton.setFocusPainted(false);
        
        cancelButton.setBackground(new java.awt.Color(244, 67, 54));
        cancelButton.setForeground(java.awt.Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        
        // Reset form state
        selectedImageFile = null;
        uploadProgressBar.setVisible(false);
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("Chưa có ảnh");
        
        // Populate form with variant data
        populateVariantForm(variant);
        
        final boolean[] result = {false};
        
        saveButton.addActionListener(e -> {
            if (validateVariantForm()) {
                // Nếu có ảnh được chọn, upload trước khi lưu
                if (selectedImageFile != null) {
                    uploadImageToCloudinary(
                        () -> {
                            // Success callback
                            updateVariantFromForm(variant);
                            result[0] = true;
                            dialog.dispose();
                        },
                        () -> {
                            // Error callback
                            JOptionPane.showMessageDialog(dialog,
                                "Lỗi khi upload ảnh. Vui lòng thử lại!",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    );
                } else {
                    // Không có ảnh mới, lưu trực tiếp
                    updateVariantFromForm(variant);
                    result[0] = true;
                    dialog.dispose();
                }
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
        return result[0];
    }
    
    private void populateVariantForm(ProductVariant variant) {
        if (variant.getColor() != null) {
            colorComboBox.setSelectedItem(variant.getColor());
        } else if (variant.getColorId() > 0) {
            // Find color by ID
            for (Color color : colors) {
                if (color.getColorId() == variant.getColorId()) {
                    colorComboBox.setSelectedItem(color);
                    break;
                }
            }
        }
        
        if (variant.getSize() != null) {
            sizeComboBox.setSelectedItem(variant.getSize());
        } else if (variant.getSizeId() > 0) {
            // Find size by ID
            for (Size size : sizes) {
                if (size.getSizeId() == variant.getSizeId()) {
                    sizeComboBox.setSelectedItem(size);
                    break;
                }
            }
        }
        
        imageUrlField.setText(variant.getImageUrl() != null ? variant.getImageUrl() : "");
        stockQuantityField.setText(String.valueOf(variant.getStockQuantity()));
        priceField.setText(variant.getPrice() != null ? variant.getPrice().toString() : "0.0");
        
        // Load existing image preview if available
        if (variant.getImageUrl() != null && !variant.getImageUrl().trim().isEmpty()) {
            // You can implement loading image from URL for preview if needed
            imagePreviewLabel.setText("Đã có ảnh");
        }
    }
    
    private boolean validateVariantForm() {
        if (colorComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn màu sắc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (sizeComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn kích cỡ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            int stock = Integer.parseInt(stockQuantityField.getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "Tồn kho không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tồn kho phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Giá không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void updateVariantFromForm(ProductVariant variant) {
        Color selectedColor = (Color) colorComboBox.getSelectedItem();
        Size selectedSize = (Size) sizeComboBox.getSelectedItem();
        
        variant.setColor(selectedColor);
        variant.setColorId(selectedColor.getColorId());
        variant.setSize(selectedSize);
        variant.setSizeId(selectedSize.getSizeId());
        variant.setImageUrl(imageUrlField.getText().trim());
        variant.setStockQuantity(Integer.parseInt(stockQuantityField.getText().trim()));
        variant.setPrice(new BigDecimal(priceField.getText().trim()));
    }
    
    private ProductVariant cloneVariant(ProductVariant original) {
        ProductVariant clone = new ProductVariant();
        clone.setVariantId(original.getVariantId());
        clone.setProductId(original.getProductId());
        clone.setColorId(original.getColorId());
        clone.setSizeId(original.getSizeId());
        clone.setImageUrl(original.getImageUrl());
        clone.setStockQuantity(original.getStockQuantity());
        clone.setPrice(original.getPrice());
        clone.setColor(original.getColor());
        clone.setSize(original.getSize());
        return clone;
    }
    
    // Public methods
    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants != null ? new ArrayList<>(variants) : new ArrayList<>();
        refreshTable();
    }
    
    public List<ProductVariant> getVariants() {
        return new ArrayList<>(variants);
    }
    
    public int getVariantCount() {
        return variants.size();
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
        addButton.setEnabled(editable);
        duplicateButton.setEnabled(editable);
        // Update table editability - buttons in table will be controlled by event listeners
        variantTable.setEnabled(editable);
    }
    
    public void saveVariants(long productId) {
        try {
            for (ProductVariant variant : variants) {
                variant.setProductId(productId);
                if (variant.getVariantId() == 0) {
                    // Create new variant
                    variantController.createVariant(variant);
                } else {
                    // Update existing variant
                    variantController.updateVariant(variant);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi lưu variants: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void addVariantChangeListener(Runnable listener) {
        variantChangeListeners.add(listener);
    }
    
    private void notifyVariantChanged() {
        for (Runnable listener : variantChangeListeners) {
            listener.run();
        }
    }
}