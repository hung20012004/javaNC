/*
 * Click nfs://.netbeans.org/projects/nbplatform/Licenses/license-default.txt to change this license
 * Click nfs://.netbeans.org/projects/nbplatform/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Banner;

import com.mycompany.storeapp.model.entity.Banner;
import com.mycompany.storeapp.view.component.admin.CloudinaryService;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

/**
 * Dialog form để thêm/sửa banner
 * @author ADMIN
 */
public class BannerFormDialog extends JDialog {
    private JTextField titleField;
    private JTextField subtitleField;
    private JTextField buttonTextField;
    private JTextField buttonLinkField;
    private JTextField orderSequenceField;
    private JComboBox<String> statusComboBox;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JLabel imagePreviewLabel;
    private JButton selectImageButton;
    private JButton removeImageButton;
    private JProgressBar uploadProgressBar;
    private JLabel uploadStatusLabel;
    private File selectedImageFile;
    private String uploadedImageUrl;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    private Banner banner;
    private boolean isEditMode;
    private boolean confirmed = false;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public BannerFormDialog(JFrame parent, String title, Banner banner) {
        super(parent, title, true);
        this.banner = banner != null ? banner : new Banner();
        this.isEditMode = (banner != null);
        
        initComponents();
        setupLayout();
        setupEventListeners();
        loadData();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(800, 750); // Tăng kích thước để phù hợp với bố cục
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initComponents() {
        Dimension fieldSize = new Dimension(250, 35); // Chiều rộng 250px, chiều cao 35px

        titleField = new JTextField();
        titleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleField.setPreferredSize(fieldSize);
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        subtitleField = new JTextField();
        subtitleField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleField.setPreferredSize(fieldSize);
        subtitleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        buttonTextField = new JTextField();
        buttonTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        buttonTextField.setPreferredSize(fieldSize);
        buttonTextField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        buttonLinkField = new JTextField();
        buttonLinkField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        buttonLinkField.setPreferredSize(fieldSize);
        buttonLinkField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        orderSequenceField = new JTextField();
        orderSequenceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderSequenceField.setPreferredSize(fieldSize);
        orderSequenceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        statusComboBox = new JComboBox<>(new String[]{"Hoạt động", "Không hoạt động"});
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusComboBox.setPreferredSize(fieldSize);
        statusComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        startDateChooser = new JDateChooser();
        startDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startDateChooser.setPreferredSize(fieldSize); // Sử dụng kích thước chung
        startDateChooser.setDateFormatString("dd/MM/yyyy"); // Định dạng ngày
        startDateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        endDateChooser = new JDateChooser();
        endDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endDateChooser.setPreferredSize(fieldSize); // Sử dụng kích thước chung
        endDateChooser.setDateFormatString("dd/MM/yyyy"); // Định dạng ngày
        endDateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        initImageComponents();
        
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
        
        selectImageButton = new JButton("Chọn ảnh");
        selectImageButton.setPreferredSize(new Dimension(100, 35));
        selectImageButton.setBackground(new Color(46, 204, 113));
        selectImageButton.setForeground(Color.WHITE);
        selectImageButton.setFocusPainted(false);
        selectImageButton.setBorderPainted(false);
        selectImageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        selectImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        removeImageButton = new JButton("Xóa ảnh");
        removeImageButton.setPreferredSize(new Dimension(100, 35));
        removeImageButton.setBackground(new Color(231, 76, 60));
        removeImageButton.setForeground(Color.WHITE);
        removeImageButton.setFocusPainted(false);
        removeImageButton.setBorderPainted(false);
        removeImageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        removeImageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeImageButton.setVisible(false);
        
        uploadProgressBar = new JProgressBar(0, 100);
        uploadProgressBar.setStringPainted(true);
        uploadProgressBar.setString("Sẵn sàng");
        uploadProgressBar.setPreferredSize(new Dimension(0, 25));
        uploadProgressBar.setVisible(false);
        
        uploadStatusLabel = new JLabel("Chưa chọn ảnh");
        uploadStatusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        uploadStatusLabel.setForeground(new Color(127, 140, 141));
        
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

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // Trường đơn cột
        mainPanel.add(createFieldPanel("Tiêu đề *        ", titleField, null, null));
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(createFieldPanel("Phụ đề           ", subtitleField, null, null));
        mainPanel.add(Box.createVerticalStrut(15));

        // Cặp trường "Văn bản nút" và "Liên kết nút" trên cùng một dòng
        mainPanel.add(createFieldPanel("Văn bản nút", buttonTextField, "Liên kết nút", buttonLinkField));
        mainPanel.add(Box.createVerticalStrut(15));

        // Trường đơn cột
        mainPanel.add(createFieldPanel("Thứ tự           ", orderSequenceField, null, null));
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(createFieldPanel("Trạng thái      ", statusComboBox, null, null));
        mainPanel.add(Box.createVerticalStrut(15));

        // Cặp trường "Ngày bắt đầu" và "Ngày kết thúc" trên cùng một dòng
        mainPanel.add(createFieldPanel("Ngày bắt đầu", startDateChooser, "Ngày kết thúc", endDateChooser));
        mainPanel.add(Box.createVerticalStrut(15));

        // Panel hình ảnh
        mainPanel.add(createImagePanel());
        mainPanel.add(Box.createVerticalGlue());

        // Panel nút điều khiển
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFieldPanel(String labelText1, JComponent field1, String labelText2, JComponent field2) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 5, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST; // Căn trái để thẳng hàng

        // Cột đầu tiên: Label và Field 1
        JLabel label1 = new JLabel(labelText1);
        label1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label1.setForeground(new Color(51, 51, 51));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3; // Tỷ lệ cho label
        gbc.gridwidth = 1;
        panel.add(label1, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7; // Tỷ lệ cho field
        gbc.gridwidth = 1;
        panel.add(field1, gbc);

        // Cột thứ hai: Label và Field 2 (nếu có)
        if (labelText2 != null && field2 != null) {
            JLabel label2 = new JLabel(labelText2);
            label2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label2.setForeground(new Color(51, 51, 51));

            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.weightx = 0.3;
            gbc.gridwidth = 1;
            panel.add(label2, gbc);

            gbc.gridx = 3;
            gbc.gridy = 0;
            gbc.weightx = 0.7;
            gbc.gridwidth = 1;
            panel.add(field2, gbc);
        } else {
            // Nếu không có cột thứ hai, lấp đầy không gian còn lại
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.gridwidth = 2;
            panel.add(new JLabel(), gbc); // Placeholder để giữ bố cục
        }

        return panel;
    }
    
    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("Hình ảnh banner");
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(51, 51, 51));
        
        JPanel imageContentPanel = new JPanel(new BorderLayout(10, 10));
        imageContentPanel.setBackground(Color.WHITE);
        
        imageContentPanel.add(imagePreviewLabel, BorderLayout.WEST);
        
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setBackground(Color.WHITE);
        
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
        selectImageButton.addActionListener(e -> selectImage());
        removeImageButton.addActionListener(e -> removeImage());
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateForm()) {
                    saveBanner();
                }
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        ActionListener saveAction = e -> {
            if (validateForm()) {
                saveBanner();
            }
        };
        
        titleField.addActionListener(saveAction);
    }
    
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Hình ảnh (*.jpg, *.jpeg, *.png, *.gif, *.bmp, *.webp)", 
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
        );
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            
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
        if (isEditMode && banner != null) {
            titleField.setText(banner.getTitle() != null ? banner.getTitle() : "");
            subtitleField.setText(banner.getSubtitle() != null ? banner.getSubtitle() : "");
            buttonTextField.setText(banner.getButtonText() != null ? banner.getButtonText() : "");
            buttonLinkField.setText(banner.getButtonLink() != null ? banner.getButtonLink() : "");
            orderSequenceField.setText(String.valueOf(banner.getOrderSequence()));
            statusComboBox.setSelectedIndex(banner.getIsActive() == 1 ? 0 : 1);
            startDateChooser.setDate(banner.getStartDate()); // Sử dụng setDate
            endDateChooser.setDate(banner.getEndDate());    // Sử dụng setDate
            
            if (banner.getImageUrl() != null && !banner.getImageUrl().trim().isEmpty()) {
                uploadedImageUrl = banner.getImageUrl();
                updateImageStatus("Đã có ảnh: " + getFileNameFromUrl(uploadedImageUrl));
            }
        }
    }
    
    private String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) return "";
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
    
    private boolean validateForm() {
        if (titleField.getText().trim().isEmpty()) {
            showError("Tiêu đề không được để trống!");
            titleField.requestFocus();
            return false;
        }
        if (titleField.getText().trim().length() > 100) {
            showError("Tiêu đề không được vượt quá 100 ký tự!");
            titleField.requestFocus();
            return false;
        }
        if (subtitleField.getText().length() > 200) {
            showError("Phụ đề không được vượt quá 200 ký tự!");
            subtitleField.requestFocus();
            return false;
        }
        if (buttonTextField.getText().length() > 50) {
            showError("Văn bản nút không được vượt quá 50 ký tự!");
            buttonTextField.requestFocus();
            return false;
        }
        if (buttonLinkField.getText().length() > 255) {
            showError("Liên kết nút không được vượt quá 255 ký tự!");
            buttonLinkField.requestFocus();
            return false;
        }
        try {
            Integer.parseInt(orderSequenceField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Thứ tự phải là số nguyên!");
            orderSequenceField.requestFocus();
            return false;
        }
        
        // Kiểm tra ngày bắt đầu
        Date currentDate = new Date(); // Ngày hiện tại (04:45 PM +07, 24/06/2025)
        Date startDate = startDateChooser.getDate();
        if (startDate != null && startDate.before(currentDate)) {
            showError("Ngày bắt đầu phải là ngày hiện tại hoặc trong tương lai!");
            startDateChooser.requestFocusInWindow();
            return false;
        }
        
        // Kiểm tra ngày kết thúc
        Date endDate = endDateChooser.getDate();
        if (endDate != null && startDate != null && endDate.before(startDate)) {
            showError("Ngày kết thúc phải sau ngày bắt đầu!");
            endDateChooser.requestFocusInWindow();
            return false;
        }
        
        return true;
    }
    
    private void saveBanner() {
        saveButton.setEnabled(false);
        saveButton.setText("Đang xử lý...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private String finalImageUrl = uploadedImageUrl;
            
            @Override
            protected Void doInBackground() throws Exception {
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
                    get();
                    
                    if (banner == null) {
                        banner = new Banner();
                        banner.setCreated_at(new Date());
                    }
                    
                    banner.setTitle(titleField.getText().trim());
                    banner.setSubtitle(subtitleField.getText().trim());
                    banner.setButtonText(buttonTextField.getText().trim());
                    banner.setButtonLink(buttonLinkField.getText().trim());
                    banner.setOrderSequence(Integer.parseInt(orderSequenceField.getText().trim()));
                    banner.setIsActive(statusComboBox.getSelectedIndex() == 0 ? 1 : 0);
                    banner.setStartDate(startDateChooser.getDate()); // Lấy ngày từ JDateChooser
                    banner.setEndDate(endDateChooser.getDate());     // Lấy ngày từ JDateChooser
                    banner.setImageUrl(finalImageUrl);
                    banner.setUpdated_at(new Date());
                    
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
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    public Banner getBanner() {
        return banner;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public String getUploadedImageUrl() {
        return uploadedImageUrl;
    }
}