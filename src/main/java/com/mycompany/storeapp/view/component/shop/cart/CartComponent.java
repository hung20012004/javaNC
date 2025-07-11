package com.mycompany.storeapp.view.component.shop.cart;

import com.mycompany.storeapp.view.component.shop.order.CreateOrderDialog;
import com.mycompany.storeapp.controller.shop.CartController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.CartItem;
import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartComponent extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private CartHeaderComponent headerComponent;
    private CartTableComponent tableComponent;
    private CartCustomerComponent customerComponent;
    private CartSummaryComponent summaryComponent;
    private CartActionsComponent actionsComponent;
    private CartController cartController;
    private ProductVariantController variantController;
    private List<CartItem> cartItems;
    private double discountPercent = 0.0;
    private int cartId = 1;

    public CartComponent() {
        cartItems = new ArrayList<>();
        cartController = new CartController();
        variantController = new ProductVariantController();
        loadCartFromDB();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        refreshData();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR),
            new EmptyBorder(0, 0, 0, 0)
        ));

        headerComponent = new CartHeaderComponent();
        tableComponent = new CartTableComponent();
        customerComponent = new CartCustomerComponent();
        summaryComponent = new CartSummaryComponent();
        actionsComponent = new CartActionsComponent();
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerComponent, BorderLayout.NORTH);
        topPanel.add(tableComponent, BorderLayout.CENTER);
        topPanel.add(customerComponent, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(summaryComponent, BorderLayout.NORTH);
        bottomPanel.add(actionsComponent, BorderLayout.SOUTH);

        contentPanel.add(topPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        headerComponent.setClearCartListener(e -> clearCart());
        tableComponent.setListener(new CartTableComponent.CartTableListener() {
            @Override
            public void onQuantityChanged(int index, int newQuantity) {
                updateQuantity(index, newQuantity);
            }

            @Override
            public void onItemRemoved(int index) {
                removeItem(index);
            }
        });
        actionsComponent.setDiscountListener(e -> showDiscountDialog());
        actionsComponent.setCheckoutListener(e -> processCheckout());
    }

    private void refreshData() {
        tableComponent.updateTable(cartItems);
        updateCartSummary();
    }

    public void addItem(int variantId, int quantity) {
        ProductVariant variant = variantController.getVariantById(variantId);
        if (variant != null) {
            CartItem newItem = new CartItem(cartId, variantId, quantity);
            boolean added = cartController.addItem(newItem);
            if (added) {
                loadCartFromDB();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không thể thêm sản phẩm: Số lượng trong kho không đủ!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Sản phẩm không tồn tại!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < cartItems.size()) {
            cartController.removeItem(cartItems.get(index).getCartItemId());
            loadCartFromDB();
            refreshData();
        }
    }

    public void updateQuantity(int index, int quantity) {
        if (index >= 0 && index < cartItems.size()) {
            if (quantity <= 0) {
                removeItem(index);
            } else {
                boolean updated = cartController.updateQuantity(cartItems.get(index).getCartItemId(), quantity);
                if (updated) {
                    loadCartFromDB();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể cập nhật số lượng: Số lượng trong kho không đủ!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void clearCart() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng đã trống!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa tất cả sản phẩm?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            cartController.clearCart(cartId);
            cartItems.clear();
            refreshData();
        }
    }

    public boolean hasItems() {
        return !cartItems.isEmpty();
    }

    private void updateCartSummary() {
        double subtotal = cartItems.stream()
            .mapToDouble(item -> {
                ProductVariant variant = variantController.getVariantById(item.getVariantId());
                if (variant != null) {
                    return variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue();
                }
                return 0.0;
            }).sum();

        double discount = subtotal * (discountPercent / 100.0);
        double total = subtotal - discount;

        headerComponent.updateItemCount(cartItems.size());
        summaryComponent.updateSummary(subtotal, discount, total);
    }

    private void showDiscountDialog() {
        String input = JOptionPane.showInputDialog(
            this,
            "Nhập phần trăm giảm giá (0-100):",
            "Giảm giá",
            JOptionPane.QUESTION_MESSAGE
        );

        if (input != null && !input.trim().isEmpty()) {
            try {
                double discount = Double.parseDouble(input.trim());
                if (discount >= 0 && discount <= 100) {
                    discountPercent = discount;
                    updateCartSummary();
                } else {
                    JOptionPane.showMessageDialog(this, "Giá trị phải từ 0 đến 100!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        CreateOrderDialog orderDialog = new CreateOrderDialog(parentFrame, cartItems, discountPercent);
        orderDialog.setVisible(true);

        if (orderDialog.isOrderCreated()) {
            cartController.clearCart(cartId);
            cartItems.clear();
            customerComponent.reset();
            discountPercent = 0.0;
            updateCartSummary();
        }
    }

    private void loadCartFromDB() {
        cartItems.clear();
        cartItems.addAll(cartController.getAllItems(cartId));
    }

    public String getCustomerName() {
        return customerComponent.getCustomerName();
    }

    public String getNotes() {
        return customerComponent.getNotes();
    }
}