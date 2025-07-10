package com.mycompany.storeapp.view.component.shop.order;

import com.mycompany.storeapp.model.entity.CartItem;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class OrderItemsTablePanel extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    
    private JTable orderItemsTable;
    private List<CartItem> cartItems;
    private DecimalFormat currencyFormat;
    private ProductVariantController variantController;
    
    public OrderItemsTablePanel(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        this.currencyFormat = new DecimalFormat("#,###,### ₫");
        this.variantController = new ProductVariantController();
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(0, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("Sản phẩm đã chọn:");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(titleLabel, BorderLayout.NORTH);
        
        createTable();
        
        JScrollPane tableScroll = new JScrollPane(orderItemsTable);
        tableScroll.setPreferredSize(new Dimension(0, 150));
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        add(tableScroll, BorderLayout.CENTER);
    }
    
    private void createTable() {
        String[] columns = {"Sản phẩm", "SL", "Đơn giá", "Thành tiền"};
        String[][] data = new String[cartItems.size()][4];
        
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            ProductVariant variant = variantController.getVariantById(item.getVariantId());
            if (variant != null) {
                data[i][0] = variant.getName();
                data[i][1] = String.valueOf(item.getQuantity());
                data[i][2] = currencyFormat.format(variant.getPrice());
                data[i][3] = currencyFormat.format(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        
        orderItemsTable = new JTable(data, columns);
        orderItemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        orderItemsTable.setRowHeight(30);
        orderItemsTable.setEnabled(false);
        orderItemsTable.getTableHeader().setReorderingAllowed(false);
        orderItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        createTable();
        removeAll();
        initializeComponents();
        revalidate();
        repaint();
    }
    
    public JTable getTable() {
        return orderItemsTable;
    }
    
    public List<CartItem> getCartItems() {
        return cartItems;
    }
}