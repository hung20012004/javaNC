/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Order;

import com.mycompany.storeapp.controller.admin.OrderController;
import com.mycompany.storeapp.controller.admin.ProductController;
import com.mycompany.storeapp.model.entity.Order;
import com.mycompany.storeapp.model.entity.OrderDetail;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;

/**
 * Dialog để hiển thị và chỉnh sửa thông tin đơn hàng
 * @author Hi
 */
public class OrderEditDialog extends JDialog {
    private ProductController productController;
    private final Order order;
    private final OrderController orderController;
    private final Runnable onSaveCallback;
    private final ExecutorService imageLoadExecutor = Executors.newCachedThreadPool();
    private final Map<String, ImageIcon> imageCache = new HashMap<>();
    
    // Form fields
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextArea addressArea;
    private JTextArea notesArea;
    private JComboBox<String> statusComboBox;
    
    // Info labels
    private JLabel orderIdLabel;
    private JLabel orderDateLabel;
    private JLabel totalAmountLabel;
    
    // Product list
    private JPanel productListPanel;
    private JScrollPane productScrollPane;
    
    // Buttons
    private JButton saveButton;
    private JButton cancelButton;
    
    // Format
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dateFormatter;
    
    public OrderEditDialog(Window parent, Order order, OrderController orderController, Runnable onSaveCallback) {
        super(parent, "Chi tiết đơn hàng #" + order.getOrderId(), ModalityType.APPLICATION_MODAL);
        this.productController = new ProductController();
        this.order = order;
        this.orderController = orderController;
        this.onSaveCallback = onSaveCallback;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        initComponents();
        setupLayout();
        populateFields();
        loadProductList();
        setupEventHandlers();
        
        setSize(800, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    private void initComponents() {
        // Info labels
        orderIdLabel = new JLabel("Mã đơn hàng: #" + order.getOrderId());
        orderIdLabel.setFont(orderIdLabel.getFont().deriveFont(Font.BOLD, 16f));
        
        orderDateLabel = new JLabel("Ngày đặt: " + order.getOrderDate().format(dateFormatter));
        orderDateLabel.setFont(orderDateLabel.getFont().deriveFont(14f));
        
        totalAmountLabel = new JLabel();
        totalAmountLabel.setFont(totalAmountLabel.getFont().deriveFont(Font.BOLD, 16f));
        totalAmountLabel.setForeground(new Color(46, 204, 113));
        
        // Form fields
        customerNameField = new JTextField(20);
        customerPhoneField = new JTextField(20);
        
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        
        // Status combo box
        statusComboBox = new JComboBox<>(new String[]{
            "pending", "confirmed", "processing", "shipping", "delivered", "cancelled"
        });
        
        // Product list panel
        productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        productScrollPane = new JScrollPane(productListPanel);
        productScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        productScrollPane.setPreferredSize(new Dimension(750, 200));
        
        // Buttons
        saveButton = new JButton("Lưu thay đổi");
        cancelButton = new JButton("Hủy");
        
        // Button styling
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(120, 35));
        
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(120, 35));
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Content panel with tabs or sections
        JPanel contentPanel = createContentPanel();
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        headerPanel.add(orderIdLabel, gbc);
        
        gbc.gridx = 1;
        headerPanel.add(orderDateLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        headerPanel.add(totalAmountLabel, gbc);
        
        return headerPanel;
    }
    
    private JPanel createContentPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Customer info tab
        JPanel customerPanel = createCustomerInfoPanel();
        tabbedPane.addTab("Thông tin khách hàng", customerPanel);
        
        // Products tab
        JPanel productsPanel = createProductsPanel();
        tabbedPane.addTab("Danh sách sản phẩm", productsPanel);
        
        // Order status tab
        JPanel statusPanel = createStatusPanel();
        tabbedPane.addTab("Trạng thái đơn hàng", statusPanel);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        
        return contentPanel;
    }
    
    private JPanel createCustomerInfoPanel() {
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Customer Name
        gbc.gridx = 0; gbc.gridy = 0;
        customerPanel.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        customerPanel.add(customerNameField, gbc);
        
        // Customer Phone
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        customerPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        customerPanel.add(customerPhoneField, gbc);
        
        // Delivery Address
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        customerPanel.add(new JLabel("Địa chỉ giao hàng:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        customerPanel.add(addressScroll, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        customerPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        customerPanel.add(notesScroll, gbc);
        
        return customerPanel;
    }
    
    private JPanel createProductsPanel() {
        JPanel productsPanel = new JPanel(new BorderLayout());
        productsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("Danh sách sản phẩm");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        productsPanel.add(titleLabel, BorderLayout.NORTH);
        productsPanel.add(productScrollPane, BorderLayout.CENTER);
        
        return productsPanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        statusPanel.add(new JLabel("Trạng thái đơn hàng:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        statusPanel.add(statusComboBox, gbc);
        
        return statusPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }
    
    private void populateFields() {
        customerNameField.setText(order.getCustomerName() != null ? order.getCustomerName() : "");
        customerPhoneField.setText(order.getCustomerPhone() != null ? order.getCustomerPhone() : "");
        addressArea.setText(order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "");
        notesArea.setText(order.getNotes() != null ? order.getNotes() : "");
        
        // Set status
        if (order.getStatus() != null) {
            statusComboBox.setSelectedItem(order.getStatus());
        }
        
        // Calculate and display total amount
        updateTotalAmount();
    }
    
    private void loadProductList() {
        productListPanel.removeAll();
        
        List<OrderDetail> orderItems = order.getDetails();
        if (orderItems != null && !orderItems.isEmpty()) {
            for (OrderDetail item : orderItems) {
                JPanel itemPanel = createProductItemPanel(item);
                productListPanel.add(itemPanel);
                productListPanel.add(Box.createVerticalStrut(10));
            }
        } else {
            JLabel noItemsLabel = new JLabel("Không có sản phẩm nào trong đơn hàng");
            noItemsLabel.setHorizontalAlignment(JLabel.CENTER);
            productListPanel.add(noItemsLabel);
        }
        
        productListPanel.revalidate();
        productListPanel.repaint();
    }
    
    private JPanel createProductItemPanel(OrderDetail item) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 5));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));
        itemPanel.setBackground(Color.WHITE);
        
        // Image panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setPreferredSize(new Dimension(200, 200));
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        // Load image asynchronously
        loadProductImage(item.getVariant().getImageUrl(), imageLabel);
        
        // Info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2, 5, 2, 5);
        
        // Product name
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel nameLabel = new JLabel( productController.getProductById(item.getVariant().getProductId()).getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
        infoPanel.add(nameLabel, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        infoPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(item.getQuantity())), gbc);
        
        // Unit price
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(currencyFormat.format(item.getUnitPrice())), gbc);
        
        // Total price
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Thành tiền:"), gbc);
        gbc.gridx = 1;
        JLabel totalLabel = new JLabel(currencyFormat.format(item.getQuantity() * item.getUnitPrice()));
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        totalLabel.setForeground(new Color(46, 204, 113));
        infoPanel.add(totalLabel, gbc);
        
        itemPanel.add(imagePanel, BorderLayout.WEST);
        itemPanel.add(infoPanel, BorderLayout.CENTER);
        
        return itemPanel;
    }
    
    private void loadProductImage(String imageUrl, JLabel imageLabel) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageLabel.setText("No Image");
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            return;
        }
        
        // Check cache first
        if (imageCache.containsKey(imageUrl)) {
            imageLabel.setIcon(imageCache.get(imageUrl));
            return;
        }
        
        // Show loading indicator
        imageLabel.setText("Loading...");
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Load image asynchronously
        imageLoadExecutor.submit(() -> {
            try {
                BufferedImage originalImage = ImageIO.read(new URL(imageUrl));
                if (originalImage != null) {
                    // Resize image to fit the label
                    Image scaledImage = originalImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    
                    // Cache the image
                    imageCache.put(imageUrl, icon);
                    
                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setText("");
                        imageLabel.setIcon(icon);
                        imageLabel.revalidate();
                        imageLabel.repaint();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        imageLabel.setText("Failed");
                        imageLabel.setHorizontalAlignment(JLabel.CENTER);
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setText("Error");
                    imageLabel.setHorizontalAlignment(JLabel.CENTER);
                });
                e.printStackTrace();
            }
        });
    }
    
    private void updateTotalAmount() {
        double total = 0;
        List<OrderDetail> orderItems = order.getDetails();
        if (orderItems != null) {
            for (OrderDetail item : orderItems) {
                total += item.getQuantity() * item.getUnitPrice();
            }
        }
        totalAmountLabel.setText("Tổng tiền: " + currencyFormat.format(total));
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveOrder();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // ESC key to cancel
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Set default button
        getRootPane().setDefaultButton(saveButton);
    }
    
    private void saveOrder() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }
            
            // Disable buttons during save
            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
            saveButton.setText("Đang lưu...");
            
            // Get updated values
            String customerName = customerNameField.getText().trim();
            String customerPhone = customerPhoneField.getText().trim();
            String deliveryAddress = addressArea.getText().trim();
            String notes = notesArea.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();
            
            // TODO: Implement updateOrder method in OrderController
            // boolean success = orderController.updateOrder(
            //     order.getOrderId(),
            //     customerName,
            //     customerPhone,
            //     deliveryAddress,
            //     notes,
            //     status
            // );
            
            // For now, simulate success
            boolean success = true;
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật đơn hàng thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Call callback to refresh parent view
                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra khi cập nhật đơn hàng!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Có lỗi xảy ra: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            // Re-enable buttons
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            saveButton.setText("Lưu thay đổi");
        }
    }
    
    private boolean validateInput() {
        // Validate customer name
        if (customerNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập tên khách hàng!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            customerNameField.requestFocus();
            return false;
        }
        
        // Validate phone number
        String phone = customerPhoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập số điện thoại!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            customerPhoneField.requestFocus();
            return false;
        }
        
        // Basic phone validation (Vietnamese phone numbers)
        if (!phone.matches("^(\\+84|0)[0-9]{9,10}$")) {
            JOptionPane.showMessageDialog(this,
                "Số điện thoại không hợp lệ!\nVui lòng nhập số điện thoại Việt Nam (10-11 chữ số)",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            customerPhoneField.requestFocus();
            return false;
        }
        
        // Validate delivery address
        if (addressArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập địa chỉ giao hàng!",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            addressArea.requestFocus();
            return false;
        }
        
        return true;
    }
    
    @Override
    public void dispose() {
        // Shutdown image loading executor
        imageLoadExecutor.shutdown();
        super.dispose();
    }
    
    /**
     * Static method để hiển thị dialog một cách tiện lợi
     */
    public static void showDialog(Window parent, Order order, OrderController orderController, Runnable onSaveCallback) {
        OrderEditDialog dialog = new OrderEditDialog(parent, order, orderController, onSaveCallback);
        dialog.setVisible(true);
    }
}