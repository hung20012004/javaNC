/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Category;

import com.mycompany.storeapp.model.entity.Category;
import com.mycompany.storeapp.view.component.admin.CloudinaryService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 * Dialog form để thêm/sửa category
 * @author Hi
 */
public class CategoryFormDialog extends JDialog {
    private JTextField nameField;
    private JTextField slugField;
    private JTextArea descriptionArea;
    private JCheckBox isActiveCheckBox;
    private JButton saveButton;
    private JButton cancelButton;
    
    // Image components
    private JLabel imagePreviewLabel;
    private JButton selectImageButton;
    private JButton removeImageButton;
    private JProgressBar uploadProgressBar;
    private JLabel uploadStatusLabel;
    private File selectedImageFile;
    private String uploadedImageUrl;
    
    private Category category;
    private boolean isEditMode;
    private boolean confirmed = false;
    
    public CategoryFormDialog(JFrame parent, String title, Category category) {
        super(parent, title, true);
        this.category = category;
        this.isEditMode = (category != null);
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        // Name field
        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(0, 35));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Slug field
        slugField = new JTextField();
        slugField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        slugField.setPreferredSize(new Dimension(0, 35));
        slugField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Description area
        descriptionArea = new JTextArea(3, 0);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // Active checkbox
        isActiveCheckBox = new JCheckBox("Danh mục hoạt động");
        isActiveCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        isActiveCheckBox.setSelected(true);
        
        // Image components
        initImageComponents();
        
        // Buttons
        saveButton = new JButton(isEditMode ? "Cập nhật" : "Thêm mới");
        saveButton.setPreferredSize(new Dimension(120, 40));
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effects
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(52, 152, 219));
            }
        });
        
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(127, 140, 141));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(149, 165, 166));
            }
        });
    }
    
    private void initImageComponents() {
        // Image preview label
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(200, 150));
        imagePreviewLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePreviewLabel.setVerticalAlignment(JLabel.CENTER);
        imagePreviewLabel.setText("<html><center>Không có ảnh<br>Kích thước đề xuất: 400x300px</center></html>");
        imagePreviewLabel.setForeground(new Color(127, 140, 141));
        imagePreviewLabel.setBackground(new Color(248, 249, 250));
        imagePreviewLabel.setOpaque(true);
        
        // Select image button
        selectImageButton = new JButton("Chọn ảnh");
        selectImageButton.setPreferredSize(new Dimension(100, 35));
        selectImageButton.setBackground(new Color(46, 204, 113));
        selectImageButton.setForeground(Color.WHITE);
        selectImageButton.setFocusPainted(false);
        selectImageButton.setBorderPainted(false);
        selectImageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        selectImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Remove image button
        removeImageButton = new JButton("Xóa ảnh");
        removeImageButton.setPreferredSize(new Dimension(100, 35));
        removeImageButton.setBackground(new Color(231, 76, 60));
        removeImageButton.setForeground(Color.WHITE);
        removeImageButton.setFocusPainted(false);
        removeImageButton.setBorderPainted(false);
        removeImageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        removeImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeImageButton.setVisible(false);
        
        // Upload progress bar
        uploadProgressBar = new JProgressBar(0, 100);
        uploadProgressBar.setStringPainted(true);
        uploadProgressBar.setString("Sẵn sàng");
        uploadProgressBar.setPreferredSize(new Dimension(0, 25));
        uploadProgressBar.setVisible(false);
        
        // Upload status label
        uploadStatusLabel = new JLabel("Chưa chọn ảnh");
        uploadStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        uploadStatusLabel.setForeground(new Color(127, 140, 141));
        
        // Hover effects for image buttons
        selectImageButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                selectImageButton.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                selectImageButton.setBackground(new Color(46, 204, 113));
            }
        });
        
        removeImageButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                removeImageButton.setBackground(new Color(192, 57, 43));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                removeImageButton.setBackground(new Color(231, 76, 60));
            }
        });
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);
        
        // Form fields
        mainPanel.add(createFieldPanel("Tên danh mục *", nameField));
        mainPanel.add(Box.createVerticalStrut(15));
        
        mainPanel.add(createFieldPanel("Slug", slugField));
        mainPanel.add(Box.createVerticalStrut(15));
        
        mainPanel.add(createFieldPanel("Mô tả", new JScrollPane(descriptionArea)));
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Image section
        mainPanel.add(createImagePanel());
        mainPanel.add(Box.createVerticalStrut(15));
        
        mainPanel.add(isActiveCheckBox);
        mainPanel.add(Box.createVerticalGlue());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(51, 51, 51));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("Hình ảnh danh mục");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(51, 51, 51));
        
        // Image content panel
        JPanel imageContentPanel = new JPanel(new BorderLayout(10, 10));
        imageContentPanel.setBackground(Color.WHITE);
        
        // Left side - image preview
        imageContentPanel.add(imagePreviewLabel, BorderLayout.WEST);
        
        // Right side - controls
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(Color.WHITE);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(selectImageButton);
        buttonPanel.add(removeImageButton);
        
        controlsPanel.add(buttonPanel);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(uploadStatusLabel);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(uploadProgressBar);
        controlsPanel.add(Box.createVerticalGlue());
        
        imageContentPanel.add(controlsPanel, BorderLayout.CENTER);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(imageContentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // Auto generate slug from name
        nameField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (!isEditMode || slugField.getText().trim().isEmpty()) {
                    String slug = generateSlug(nameField.getText());
                    slugField.setText(slug);
                }
            }
        });
        
        // Image button listeners
        selectImageButton.addActionListener(e -> selectImage());
        removeImageButton.addActionListener(e -> removeImage());
        
        // Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateForm()) {
                    saveCategory();
                }
            }
        });
        
        // Cancel button
        cancelButton.addActionListener(e -> dispose());
        
        // Enter key on text fields
        ActionListener saveAction = e -> {
            if (validateForm()) {
                saveCategory();
            }
        };
        
        nameField.addActionListener(saveAction);
        slugField.addActionListener(saveAction);
    }
    
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // Add image filter
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Hình ảnh (*.jpg, *.jpeg, *.png, *.gif, *.bmp, *.webp)", 
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
        );
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            
            // Validate file size (max 10MB)
            if (selectedImageFile.length() > 10 * 1024 * 1024) {
                showError("Kích thước file không được vượt quá 10MB!");
                return;
            }
            
            displayImagePreview(selectedImageFile);
            updateImageStatus("Đã chọn: " + selectedImageFile.getName());
            removeImageButton.setVisible(true);
            uploadedImageUrl = null; // Reset uploaded URL
        }
    }
    
    private void removeImage() {
        selectedImageFile = null;
        uploadedImageUrl = null;
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("<html><center>Không có ảnh<br>Kích thước đề xuất: 400x300px</center></html>");
        updateImageStatus("Chưa chọn ảnh");
        removeImageButton.setVisible(false);
        uploadProgressBar.setVisible(false);
    }
    
    private void displayImagePreview(File imageFile) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile);
            if (originalImage != null) {
                // Scale image to fit preview
                int previewWidth = 180;
                int previewHeight = 130;
                
                double scaleX = (double) previewWidth / originalImage.getWidth();
                double scaleY = (double) previewHeight / originalImage.getHeight();
                double scale = Math.min(scaleX, scaleY);
                
                int scaledWidth = (int) (originalImage.getWidth() * scale);
                int scaledHeight = (int) (originalImage.getHeight() * scale);
                
                BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
                g2d.dispose();
                
                imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                imagePreviewLabel.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Không thể hiển thị ảnh: " + e.getMessage());
        }
    }
    
    private void updateImageStatus(String status) {
        uploadStatusLabel.setText(status);
    }
    
    private void loadData() {
        if (isEditMode && category != null) {
            nameField.setText(category.getName());
            slugField.setText(category.getSlug());
            descriptionArea.setText(category.getDescription());
            isActiveCheckBox.setSelected(category.getIsActive());
            
            // Load existing image if available
            if (category.getImageUrl() != null && !category.getImageUrl().trim().isEmpty()) {
                uploadedImageUrl = category.getImageUrl();
                updateImageStatus("Đã có ảnh: " + getFileNameFromUrl(uploadedImageUrl));
                // TODO: Load and display image from URL if needed
            }
        }
    }
    
    private String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) return "";
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
    
    private boolean validateForm() {
        // Check required fields
        if (nameField.getText().trim().isEmpty()) {
            showError("Tên danh mục không được để trống!");
            nameField.requestFocus();
            return false;
        }
        
        if (nameField.getText().trim().length() > 100) {
            showError("Tên danh mục không được vượt quá 100 ký tự!");
            nameField.requestFocus();
            return false;
        }
        
        if (descriptionArea.getText().length() > 500) {
            showError("Mô tả không được vượt quá 500 ký tự!");
            descriptionArea.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void saveCategory() {
        // Disable save button during processing
        saveButton.setEnabled(false);
        saveButton.setText("Đang xử lý...");
        
        // Create worker thread for image upload and save
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private String finalImageUrl = uploadedImageUrl;
            
            @Override
            protected Void doInBackground() throws Exception {
                // Upload image if selected
                if (selectedImageFile != null) {
                    SwingUtilities.invokeLater(() -> {
                        uploadProgressBar.setVisible(true);
                        updateImageStatus("Đang upload ảnh...");
                    });
                    
                    finalImageUrl = CloudinaryService.uploadImage(selectedImageFile, new CloudinaryService.ProgressCallback() {
                        @Override
                        public void onProgress(int percentage) {
                            SwingUtilities.invokeLater(() -> {
                                uploadProgressBar.setValue(percentage);
                                uploadProgressBar.setString("Uploading... " + percentage + "%");
                            });
                        }
                    });
                    
                    if (finalImageUrl == null) {
                        throw new Exception("Upload ảnh thất bại");
                    }
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    
                    // Save category data
                    if (category == null) {
                        category = new Category();
                        category.setCreated_at(new Date());
                    }
                    
                    category.setName(nameField.getText().trim());
                    category.setSlug(slugField.getText().trim().isEmpty() ? 
                        generateSlug(nameField.getText()) : slugField.getText().trim());
                    category.setDescription(descriptionArea.getText().trim());
                    category.setDisplayOrder(0);
                    category.setIsActive(isActiveCheckBox.isSelected());
                    category.setImageUrl(finalImageUrl);
                    category.setUpdated_at(new Date());
                    
                    uploadedImageUrl = finalImageUrl;
                    confirmed = true;
                    
                    SwingUtilities.invokeLater(() -> {
                        updateImageStatus("Lưu thành công!");
                        dispose();
                    });
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        showError("Lỗi: " + e.getMessage());
                        updateImageStatus("Upload thất bại");
                        uploadProgressBar.setVisible(false);
                    });
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        saveButton.setEnabled(true);
                        saveButton.setText(isEditMode ? "Cập nhật" : "Thêm mới");
                    });
                }
            }
        };
        
        worker.execute();
    }
    
    private String generateSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        
        return name.trim()
                .toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    // Getter methods
    public Category getCategory() {
        return category;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getUploadedImageUrl() {
        return uploadedImageUrl;
    }
}