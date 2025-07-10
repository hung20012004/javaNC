package com.mycompany.storeapp.view.page.admin.Product;

import com.mycompany.storeapp.controller.admin.ProductImageController;
import com.mycompany.storeapp.model.entity.ProductImage;
import com.mycompany.storeapp.service.CloudinaryService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Panel quản lý hình ảnh sản phẩm với tích hợp Cloudinary
 */
public class ProductImagePanel extends JPanel {
    
    private ProductImageController imageController;
    private List<ProductImage> images;
    private boolean isEditable = true;
    private List<Runnable> imageChangeListeners;
    
    // Components
    private JPanel imageListPanel;
    private JScrollPane scrollPane;
    private JButton addButton;
    private JButton removeButton;
    private JButton setPrimaryButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JList<ProductImage> imageList;
    private DefaultListModel<ProductImage> listModel;
    private JLabel previewLabel;
    private JPanel previewPanel;
    private JProgressBar uploadProgressBar;
    private JLabel uploadStatusLabel;
    
    public ProductImagePanel(ProductImageController imageController) {
        this.imageController = imageController;
        this.images = new ArrayList<>();
        this.imageChangeListeners = new ArrayList<>();
        
        initComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initComponents() {
        // List model and JList
        listModel = new DefaultListModel<>();
        imageList = new JList<>(listModel);
        imageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        imageList.setCellRenderer(new ImageListCellRenderer());
        
        scrollPane = new JScrollPane(imageList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        
        // Buttons
        addButton = new JButton("Thêm hình ảnh");
        removeButton = new JButton("Xóa");
        setPrimaryButton = new JButton("Đặt làm ảnh chính");
        moveUpButton = new JButton("↑");
        moveDownButton = new JButton("↓");
        
        // Upload progress components
        uploadProgressBar = new JProgressBar(0, 100);
        uploadProgressBar.setStringPainted(true);
        uploadProgressBar.setVisible(false);
        
        uploadStatusLabel = new JLabel(" ");
        uploadStatusLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Preview panel
        previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(new TitledBorder("Xem trước"));
        previewPanel.setPreferredSize(new Dimension(400, 400));
        
        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(JLabel.CENTER);
        previewLabel.setVerticalAlignment(JLabel.CENTER);
        previewLabel.setText("Chọn hình ảnh để xem trước");
        previewPanel.add(previewLabel, BorderLayout.CENTER);
        
        // Upload status panel
        JPanel uploadPanel = new JPanel(new BorderLayout());
        uploadPanel.add(uploadProgressBar, BorderLayout.CENTER);
        uploadPanel.add(uploadStatusLabel, BorderLayout.SOUTH);
        previewPanel.add(uploadPanel, BorderLayout.SOUTH);
        
        // Style buttons
        setupButtonStyles();
    }
    
    private void setupButtonStyles() {
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        
        removeButton.setBackground(new Color(244, 67, 54));
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        
        setPrimaryButton.setBackground(new Color(255, 193, 7));
        setPrimaryButton.setForeground(Color.BLACK);
        setPrimaryButton.setFocusPainted(false);
        
        moveUpButton.setPreferredSize(new Dimension(50, 30));
        moveDownButton.setPreferredSize(new Dimension(50, 30));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Left panel - Image list and buttons
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(new TitledBorder("Danh sách hình ảnh"));
        
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(setPrimaryButton);
        
        // Order buttons
        JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderPanel.add(new JLabel("Thứ tự:"));
        orderPanel.add(moveUpButton);
        orderPanel.add(moveDownButton);
        
        JPanel allButtonsPanel = new JPanel(new BorderLayout());
        allButtonsPanel.add(buttonPanel, BorderLayout.NORTH);
        allButtonsPanel.add(orderPanel, BorderLayout.SOUTH);
        
        leftPanel.add(allButtonsPanel, BorderLayout.SOUTH);
        
        // Add panels to main panel
        add(leftPanel, BorderLayout.WEST);
        add(previewPanel, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        // Add button
        addButton.addActionListener(e -> addImage());
        
        // Remove button
        removeButton.addActionListener(e -> removeSelectedImage());
        
        // Set primary button
        setPrimaryButton.addActionListener(e -> setPrimaryImage());
        
        // Move buttons
        moveUpButton.addActionListener(e -> moveImage(-1));
        moveDownButton.addActionListener(e -> moveImage(1));
        
        // List selection
        imageList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updatePreview();
                updateButtonStates();
            }
        });
        
        // Double click to view full size
        imageList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showFullSizeImage();
                }
            }
        });
    }
    
    private void addImage() {
        if (!isEditable) return;
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif", "bmp", "webp"));
        fileChooser.setMultiSelectionEnabled(true);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            
            // Disable UI during upload
            setUIEnabled(false);
            uploadProgressBar.setVisible(true);
            uploadStatusLabel.setText("Đang upload hình ảnh...");
            
            // Upload files in background thread
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (int i = 0; i < selectedFiles.length; i++) {
                        File file = selectedFiles[i];
                        final int fileIndex = i;
                        final int totalFiles = selectedFiles.length;
                        
                        SwingUtilities.invokeLater(() -> {
                            uploadStatusLabel.setText(String.format(
                                "Đang upload file %d/%d: %s", 
                                fileIndex + 1, totalFiles, file.getName()
                            ));
                        });
                        
                        // Upload to Cloudinary with progress callback
                        String imageUrl = CloudinaryService.uploadImage(file, new CloudinaryService.ProgressCallback() {
                            @Override
                            public void onProgress(int percentage) {
                                SwingUtilities.invokeLater(() -> {
                                    int overallProgress = (fileIndex * 100 + percentage) / totalFiles;
                                    uploadProgressBar.setValue(overallProgress);
                                });
                            }
                        });
                        
                        if (imageUrl != null) {
                            SwingUtilities.invokeLater(() -> {
                                ProductImage image = new ProductImage();
                                image.setImageUrl(imageUrl);
                                image.setDisplayOrder(images.size() + 1);
                                image.setPrimary(images.isEmpty()); // First image is primary
                                
                                images.add(image);
                                listModel.addElement(image);
                                
                                publish("Upload thành công: " + file.getName());
                            });
                        } else {
                            publish("Upload thất bại: " + file.getName());
                        }
                    }
                    
                    return null;
                }
                
                @Override
                protected void process(List<String> chunks) {
                    for (String message : chunks) {
                        uploadStatusLabel.setText(message);
                    }
                }
                
                @Override
                protected void done() {
                    // Re-enable UI
                    setUIEnabled(true);
                    uploadProgressBar.setVisible(false);
                    uploadProgressBar.setValue(0);
                    uploadStatusLabel.setText("Upload hoàn tất");
                    
                    // Clear status after 3 seconds
                    Timer timer = new Timer(3000, e -> uploadStatusLabel.setText(" "));
                    timer.setRepeats(false);
                    timer.start();
                    
                    notifyImageChangeListeners();
                }
            };
            
            worker.execute();
        }
    }
    
    private void setUIEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        setPrimaryButton.setEnabled(enabled);
        moveUpButton.setEnabled(enabled);
        moveDownButton.setEnabled(enabled);
        imageList.setEnabled(enabled);
    }
    
    private void removeSelectedImage() {
        if (!isEditable) return;
        
        int selectedIndex = imageList.getSelectedIndex();
        if (selectedIndex >= 0) {
            ProductImage selectedImage = images.get(selectedIndex);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa hình ảnh này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                images.remove(selectedIndex);
                listModel.removeElementAt(selectedIndex);
                
                // If removed image was primary, set first image as primary
                if (selectedImage.isPrimary() && !images.isEmpty()) {
                    images.get(0).setPrimary(true);
                }
                
                // Update display order
                updateDisplayOrders();
                
                notifyImageChangeListeners();
            }
        }
    }
    
    private void setPrimaryImage() {
        if (!isEditable) return;
        
        int selectedIndex = imageList.getSelectedIndex();
        if (selectedIndex >= 0) {
            // Remove primary flag from all images
            for (ProductImage image : images) {
                image.setPrimary(false);
            }
            
            // Set selected image as primary
            images.get(selectedIndex).setPrimary(true);
            
            // Refresh the list to show changes
            imageList.repaint();
            
            notifyImageChangeListeners();
        }
    }
    
    private void moveImage(int direction) {
        if (!isEditable) return;
        
        int selectedIndex = imageList.getSelectedIndex();
        if (selectedIndex >= 0) {
            int newIndex = selectedIndex + direction;
            
            if (newIndex >= 0 && newIndex < images.size()) {
                // Swap images
                ProductImage temp = images.get(selectedIndex);
                images.set(selectedIndex, images.get(newIndex));
                images.set(newIndex, temp);
                
                // Update list model
                listModel.setElementAt(images.get(selectedIndex), selectedIndex);
                listModel.setElementAt(images.get(newIndex), newIndex);
                
                // Update selection
                imageList.setSelectedIndex(newIndex);
                
                // Update display orders
                updateDisplayOrders();
                
                notifyImageChangeListeners();
            }
        }
    }
    
    private void updateDisplayOrders() {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setDisplayOrder(i + 1);
        }
    }
    
    private void updatePreview() {
        int selectedIndex = imageList.getSelectedIndex();
        if (selectedIndex >= 0) {
            ProductImage selectedImage = images.get(selectedIndex);
            displayImagePreview(selectedImage.getImageUrl());
        } else {
            previewLabel.setIcon(null);
            previewLabel.setText("Chọn hình ảnh để xem trước");
        }
    }
    
    private void displayImagePreview(String imageUrl) {
        try {
            // Show loading text
            previewLabel.setText("Đang tải hình ảnh...");
            previewLabel.setIcon(null);
            
            // Load image in background
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    ImageIcon imageIcon;
                    
                    if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                        // Load from URL (Cloudinary)
                        imageIcon = new ImageIcon(new java.net.URL(imageUrl));
                    } else {
                        // Load from local file
                        imageIcon = new ImageIcon(imageUrl);
                    }
                    
                    Image image = imageIcon.getImage();
                    
                    // Scale image to fit preview
                    int maxWidth = 350;
                    int maxHeight = 350;
                    int width = image.getWidth(null);
                    int height = image.getHeight(null);
                    
                    if (width > 0 && height > 0) {
                        double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
                        int scaledWidth = (int) (width * scale);
                        int scaledHeight = (int) (height * scale);
                        
                        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaledImage);
                    }
                    
                    return imageIcon;
                }
                
                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        previewLabel.setIcon(icon);
                        previewLabel.setText("");
                    } catch (Exception e) {
                        previewLabel.setIcon(null);
                        previewLabel.setText("Không thể tải hình ảnh");
                    }
                }
            };
            
            worker.execute();
            
        } catch (Exception e) {
            previewLabel.setIcon(null);
            previewLabel.setText("Không thể tải hình ảnh");
        }
    }
    
    private void showFullSizeImage() {
        int selectedIndex = imageList.getSelectedIndex();
        if (selectedIndex >= 0) {
            ProductImage selectedImage = images.get(selectedIndex);
            
            JDialog dialog = new JDialog();
            dialog.setTitle("Xem hình ảnh đầy đủ");
            dialog.setModal(true);
            
            JLabel fullSizeLabel = new JLabel();
            fullSizeLabel.setHorizontalAlignment(JLabel.CENTER);
            fullSizeLabel.setText("Đang tải hình ảnh...");
            
            JScrollPane scrollPane = new JScrollPane(fullSizeLabel);
            dialog.add(scrollPane);
            
            dialog.setSize(800, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            // Load image in background
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    String imageUrl = selectedImage.getImageUrl();
                    if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                        return new ImageIcon(new java.net.URL(imageUrl));
                    } else {
                        return new ImageIcon(imageUrl);
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        fullSizeLabel.setIcon(icon);
                        fullSizeLabel.setText("");
                    } catch (Exception e) {
                        fullSizeLabel.setText("Không thể tải hình ảnh");
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    private void updateButtonStates() {
        boolean hasSelection = imageList.getSelectedIndex() >= 0;
        boolean hasImages = !images.isEmpty();
        
        removeButton.setEnabled(isEditable && hasSelection);
        setPrimaryButton.setEnabled(isEditable && hasSelection);
        moveUpButton.setEnabled(isEditable && hasSelection && imageList.getSelectedIndex() > 0);
        moveDownButton.setEnabled(isEditable && hasSelection && imageList.getSelectedIndex() < images.size() - 1);
    }
    
    // Custom cell renderer for image list
    private class ImageListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ProductImage) {
                ProductImage image = (ProductImage) value;
                String imageUrl = image.getImageUrl();
                
                // Extract filename from URL or path
                String fileName;
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    // For Cloudinary URLs, extract the public ID or use a generic name
                    fileName = "Hình ảnh sản phẩm thứ: " + (index + 1);
                } else {
                    fileName = new File(imageUrl).getName();
                }
                
                String text = (index + 1) + ". " + fileName;
                
                if (image.isPrimary()) {
                    text += " (Ảnh chính)";
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                
                setText(text);
                
                // Add thumbnail (load in background to avoid blocking UI)
                setIcon(null); // Clear previous icon
                
                // Load thumbnail in background
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        try {
                            ImageIcon icon;
                            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                                icon = new ImageIcon(new java.net.URL(imageUrl));
                            } else {
                                icon = new ImageIcon(imageUrl);
                            }
                            
                            Image img = icon.getImage();
                            if (img.getWidth(null) > 0 && img.getHeight(null) > 0) {
                                Image scaledImg = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                                return new ImageIcon(scaledImg);
                            }
                        } catch (Exception e) {
                            // Ignore and return null
                        }
                        return null;
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            ImageIcon icon = get();
                            if (icon != null) {
                                setIcon(icon);
                                repaint();
                            }
                        } catch (Exception e) {
                            // Ignore
                        }
                    }
                };
                
                worker.execute();
            }
            
            return this;
        }
    }
    
    // Public methods
    public void setImages(List<ProductImage> images) {
        this.images = new ArrayList<>(images);
        
        listModel.clear();
        for (ProductImage image : this.images) {
            listModel.addElement(image);
        }
        
        updateButtonStates();
        notifyImageChangeListeners();
    }
    
    public List<ProductImage> getImages() {
        return new ArrayList<>(images);
    }
    
    public void setEditable(boolean editable) {
        this.isEditable = editable;
        
        addButton.setEnabled(editable);
        updateButtonStates();
    }
    
    public int getImageCount() {
        return images.size();
    }
    
    public void addImageChangeListener(Runnable listener) {
        imageChangeListeners.add(listener);
    }
    
    private void notifyImageChangeListeners() {
        for (Runnable listener : imageChangeListeners) {
            listener.run();
        }
    }
    
    public void saveImages(long productId) {
        try {
            for (ProductImage image : images) {
                image.setProductId(productId);
                
                if (image.getImageId() == 0) {
                    // New image
                    imageController.createImage(image);
                } else {
                    // Update existing image
                    imageController.updateImage(image);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu hình ảnh: " + e.getMessage(), e);
        }
    }
}