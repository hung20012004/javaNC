
/*
 * Click nbfs://SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.shop.order;

import com.mycompany.storeapp.view.component.shop.order.OrderInfoPanel;
import com.mycompany.storeapp.view.component.shop.order.HeaderPanel;
import com.mycompany.storeapp.view.component.shop.order.FooterPanel;
import com.mycompany.storeapp.view.component.shop.order.CustomerInfoPanel;
import com.mycompany.storeapp.view.component.shop.order.AddressPanel;
import com.mycompany.storeapp.model.entity.CartItem;
import com.mycompany.storeapp.controller.admin.OrderController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.ShippingAddress;
import com.mycompany.storeapp.session.Session;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class CreateOrderDialog extends JDialog {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    
    // Components
    private HeaderPanel headerPanel;
    private CustomerInfoPanel customerInfoPanel;
    private AddressPanel addressPanel;
    private OrderInfoPanel orderInfoPanel;
    private FooterPanel footerPanel;
    
    private List<CartItem> cartItems;
    private double discountPercent;
    private DecimalFormat currencyFormat;
    private ProductVariantController variantController;
    private OrderController orderController;
    private boolean orderCreated = false;

    public CreateOrderDialog(Frame parent, List<CartItem> cartItems, double discountPercent) {
        super(parent, "Tạo đơn hàng", true);
        this.cartItems = cartItems;
        this.discountPercent = discountPercent;
        this.currencyFormat = new DecimalFormat("#,###,### ₫");
        this.variantController = new ProductVariantController();
        this.orderController = new OrderController(new DatabaseConnection());
        
        initializeDialog();
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeDialog() {
        setSize(1000, 750);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void initializeComponents() {
        // Header
        headerPanel = new HeaderPanel(
            "Tạo đơn hàng mới", 
            "Điền thông tin khách hàng và xác nhận đơn hàng",
            "📋"
        );
        
        // Customer info and address
        customerInfoPanel = new CustomerInfoPanel();
        addressPanel = new AddressPanel();
        
        // Order info - pass cartItems and discountPercent
        orderInfoPanel = new OrderInfoPanel(cartItems, discountPercent);
        
        // Footer
        footerPanel = new FooterPanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content với scroll
        JPanel mainPanel = createMainPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        
        // Footer
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        // Panel trái chứa thông tin khách hàng và địa chỉ
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.add(customerInfoPanel, BorderLayout.NORTH);
        leftPanel.add(addressPanel, BorderLayout.CENTER);
        
        // Panel phải chứa thông tin đơn hàng
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.add(orderInfoPanel, BorderLayout.CENTER);
        
        // Chia layout 50-50 cho cân bằng hơn
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setDividerSize(10);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void setupEventListeners() {
        // Footer buttons
        footerPanel.addCancelButton("Hủy", e -> dispose());
        footerPanel.addSubmitButton("Tạo đơn hàng", e -> createOrder());
        
        orderInfoPanel.addDeliveryMethodListener(e -> {
            // Update UI if needed
        });
    }

    private void createOrder() {
        // Validate customer info
        if (!customerInfoPanel.validateFields()) {
            return;
        }
        
        // Validate address based on delivery method
        if (!addressPanel.validateFields(orderInfoPanel.getDeliveryMethod())) {
            return;
        }
        
        // Show confirmation dialog
        StringBuilder confirmMessage = new StringBuilder();
        confirmMessage.append("Xác nhận tạo đơn hàng với thông tin sau:\n\n");
        confirmMessage.append("Khách hàng: ").append(customerInfoPanel.getCustomerName()).append("\n");
        
        if (!orderInfoPanel.getDeliveryMethod().equals("Tại cửa hàng")) {
            confirmMessage.append("Địa chỉ: ").append(addressPanel.getFullAddress()).append("\n");
        }
        
        confirmMessage.append("Phương thức thanh toán: ").append(orderInfoPanel.getPaymentMethod()).append("\n");
        confirmMessage.append("Phương thức giao hàng: ").append(orderInfoPanel.getDeliveryMethod()).append("\n");
        confirmMessage.append("Tổng tiền: ").append(currencyFormat.format(orderInfoPanel.getTotal()));
        
        int option = JOptionPane.showConfirmDialog(
            this,
            confirmMessage.toString(),
            "Xác nhận tạo đơn hàng",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                // Get user ID from session or customer info
                String customerIdStr = customerInfoPanel.getCustomerId();
                int userId = customerIdStr != null && !customerIdStr.trim().isEmpty() 
                    ? Integer.parseInt(customerIdStr) : 0;
                
                if (userId <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xác định ID người dùng!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Get current user ID from session for processed_by_user_id
                Session session = Session.getInstance();
                Integer processedByUserId = session.getCurrentUser() != null 
                    ? session.getCurrentUser().getId() : null;
                
                // Create order using OrderController
                OrderController.OrderTransitionResult result = orderController.createOrder(
                    userId,
                    addressPanel.getSelectedAddress(),
                    cartItems,
                    discountPercent,
                    orderInfoPanel.getPaymentMethod(),
                    orderInfoPanel.getDeliveryMethod(),
                    addressPanel.getNote(),
                    processedByUserId
                );
                
                if (result.isSuccess()) {
                    orderCreated = true;
                    JOptionPane.showMessageDialog(this, 
                        "Đơn hàng đã được tạo thành công!\nTổng tiền: " + 
                        currencyFormat.format(orderInfoPanel.getTotal()), 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Tạo đơn hàng thất bại: " + result.getMessage(), 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi định dạng ID người dùng: " + e.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi hệ thống khi tạo đơn hàng: " + e.getMessage(), 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Getters for order data
    public boolean isOrderCreated() {
        return orderCreated;
    }
    
    // Customer information
    public String getCustomerName() { 
        return customerInfoPanel.getCustomerName(); 
    }
    
    public String getCustomerEmail() { 
        return customerInfoPanel.getCustomerEmail(); 
    }
    
    // Address information
    public String getAddress() { 
        return addressPanel.getAddress(); 
    }
    
    public String getWard() { 
        return addressPanel.getWard(); 
    }
    
    public String getDistrict() { 
        return addressPanel.getDistrict(); 
    }
    
    public String getCity() { 
        return addressPanel.getCity(); 
    }
    
    public String getNote() { 
        return addressPanel.getNote(); 
    }
    
    public String getFullAddress() {
        return addressPanel.getFullAddress();
    }
    
    // Order information
    public String getPaymentMethod() { 
        return orderInfoPanel.getPaymentMethod(); 
    }
    
    public String getDeliveryMethod() { 
        return orderInfoPanel.getDeliveryMethod(); 
    }
    
    public double getShippingFee() {
        return orderInfoPanel.getShippingFee();
    }
    
    public double getSubtotal() {
        return orderInfoPanel.getSubtotal();
    }
    
    public double getDiscount() {
        return orderInfoPanel.getDiscount();
    }
    
    public double getTotal() {
        return orderInfoPanel.getTotal();
    }
    
    // Cart items
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    public double getDiscountPercent() {
        return discountPercent;
    }
}
