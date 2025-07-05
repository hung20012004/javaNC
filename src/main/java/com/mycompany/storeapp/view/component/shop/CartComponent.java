/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.shop;

/**
 *
 * @author Manh Hung
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartComponent extends JPanel {
    
    // Colors
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color HEADER_COLOR = new Color(249, 250, 251);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);
    private static final Color SECONDARY_TEXT_COLOR = new Color(107, 114, 128);
    
    // Components
    private JLabel headerLabel;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JLabel itemCountLabel;
    private JButton clearCartButton;
    private JButton checkoutButton;
    private JButton discountButton;
    private JTextField customerField;
    private JTextArea notesArea;
    private JLabel discountLabel;
    
    // Data
    private List<CartItem> cartItems;
    private double discountPercent = 0.0;
    private DecimalFormat currencyFormat;
    
    public CartComponent() {
        cartItems = new ArrayList<>();
        currencyFormat = new DecimalFormat("#,###,### ₫");
        
        initializeCart();
        setupComponents();
        setupLayout();
    }
    
    private void initializeCart() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER_COLOR),
            new EmptyBorder(0, 0, 0, 0)
        ));
    }
    
    private void setupComponents() {
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Cart table
        JPanel tablePanel = createTablePanel();
        
        // Customer info
        JPanel customerPanel = createCustomerPanel();
        
        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        
        // Action buttons
        JPanel actionPanel = createActionPanel();
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(tablePanel, BorderLayout.CENTER);
        topPanel.add(customerPanel, BorderLayout.SOUTH);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(summaryPanel, BorderLayout.NORTH);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        
        contentPanel.add(topPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        headerLabel = new JLabel("🛒 Giỏ hàng");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(TEXT_COLOR);
        
        itemCountLabel = new JLabel("0 sản phẩm");
        itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemCountLabel.setForeground(SECONDARY_TEXT_COLOR);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(headerLabel, BorderLayout.NORTH);
        leftPanel.add(itemCountLabel, BorderLayout.SOUTH);
        
        clearCartButton = new JButton("🗑️");
        clearCartButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        clearCartButton.setBackground(DANGER_COLOR);
        clearCartButton.setForeground(Color.WHITE);
        clearCartButton.setBorder(new EmptyBorder(8, 12, 8, 12));
        clearCartButton.setFocusPainted(false);
        clearCartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearCartButton.setToolTipText("Xóa tất cả sản phẩm");
        clearCartButton.addActionListener(e -> clearCart());
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(clearCartButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 15, 15, 15));
        
        String[] columns = {"Sản phẩm", "SL", "Giá", "Thành tiền", ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 4; // Only quantity and delete button
            }
        };
        
        cartTable = new JTable(tableModel);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.setRowHeight(35);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 1));
        cartTable.setSelectionBackground(new Color(239, 246, 255));
        
        // Custom cell renderer for delete button
        cartTable.getColumn("").setCellRenderer(new DeleteButtonRenderer());
        cartTable.getColumn("").setCellEditor(new DeleteButtonEditor());
        
        // Set column widths
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Product
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(40);  // Quantity
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Price
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Total
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(30);  // Delete
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel customerLabel = new JLabel("Khách hàng:");
        customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        customerLabel.setForeground(TEXT_COLOR);
        
        customerField = new JTextField("Khách lẻ");
        customerField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
        
        JLabel notesLabel = new JLabel("Ghi chú:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        notesLabel.setForeground(TEXT_COLOR);
        
        notesArea = new JTextArea(2, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(6, 8, 6, 8)
        ));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        notesScroll.setPreferredSize(new Dimension(0, 50));
        
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(customerLabel, BorderLayout.NORTH);
        topRow.add(customerField, BorderLayout.CENTER);
        
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.add(notesLabel, BorderLayout.NORTH);
        bottomRow.add(notesScroll, BorderLayout.CENTER);
        bottomRow.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        panel.add(topRow, BorderLayout.NORTH);
        panel.add(bottomRow, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel summaryContent = new JPanel(new GridLayout(4, 2, 5, 8));
        summaryContent.setOpaque(false);
        
        // Subtotal
        JLabel subtotalLabel = new JLabel("Tạm tính:");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtotalLabel.setForeground(TEXT_COLOR);
        
        JLabel subtotalValue = new JLabel("0 ₫");
        subtotalValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtotalValue.setForeground(TEXT_COLOR);
        subtotalValue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Discount
        JLabel discountLabelText = new JLabel("Giảm giá:");
        discountLabelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountLabelText.setForeground(TEXT_COLOR);
        
        discountLabel = new JLabel("0 ₫");
        discountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountLabel.setForeground(DANGER_COLOR);
        discountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Tax (if applicable)
        JLabel taxLabel = new JLabel("Thuế (0%):");
        taxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taxLabel.setForeground(TEXT_COLOR);
        
        JLabel taxValue = new JLabel("0 ₫");
        taxValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taxValue.setForeground(TEXT_COLOR);
        taxValue.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Total
        JLabel totalLabelText = new JLabel("Tổng cộng:");
        totalLabelText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabelText.setForeground(TEXT_COLOR);
        
        totalLabel = new JLabel("0 ₫");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(PRIMARY_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        summaryContent.add(subtotalLabel);
        summaryContent.add(subtotalValue);
        summaryContent.add(discountLabelText);
        summaryContent.add(discountLabel);
        summaryContent.add(taxLabel);
        summaryContent.add(taxValue);
        summaryContent.add(totalLabelText);
        summaryContent.add(totalLabel);
        
        panel.add(summaryContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Discount button
        discountButton = new JButton("💰 Giảm giá");
        discountButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountButton.setBackground(new Color(251, 191, 36));
        discountButton.setForeground(Color.WHITE);
        discountButton.setBorder(new EmptyBorder(10, 15, 10, 15));
        discountButton.setFocusPainted(false);
        discountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        discountButton.addActionListener(e -> showDiscountDialog());
        
        // Checkout button
        checkoutButton = new JButton("💳 Thanh toán");
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutButton.setBackground(SUCCESS_COLOR);
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setBorder(new EmptyBorder(12, 20, 12, 20));
        checkoutButton.setFocusPainted(false);
        checkoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutButton.addActionListener(e -> processCheckout());
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        buttonPanel.add(discountButton);
        buttonPanel.add(checkoutButton);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupLayout() {
        updateCartSummary();
    }
    
    // Cart operations
    public void addItem(String productName, String price, int quantity) {
        // Check if item already exists
        for (CartItem item : cartItems) {
            if (item.getName().equals(productName)) {
                item.setQuantity(item.getQuantity() + quantity);
                refreshTable();
                updateCartSummary();
                return;
            }
        }
        
        // Add new item
        double priceValue = parsePrice(price);
        CartItem newItem = new CartItem(productName, priceValue, quantity);
        cartItems.add(newItem);
        
        refreshTable();
        updateCartSummary();
    }
    
    public void removeItem(int index) {
        if (index >= 0 && index < cartItems.size()) {
            cartItems.remove(index);
            refreshTable();
            updateCartSummary();
        }
    }
    
    public void updateQuantity(int index, int quantity) {
        if (index >= 0 && index < cartItems.size()) {
            if (quantity <= 0) {
                removeItem(index);
            } else {
                cartItems.get(index).setQuantity(quantity);
                refreshTable();
                updateCartSummary();
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
            cartItems.clear();
            discountPercent = 0.0;
            refreshTable();
            updateCartSummary();
        }
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            Object[] row = {
                item.getName(),
                item.getQuantity(),
                currencyFormat.format(item.getPrice()),
                currencyFormat.format(item.getTotal()),
                "Xóa"
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateCartSummary() {
        double subtotal = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        double discount = subtotal * (discountPercent / 100.0);
        double total = subtotal - discount;
        
        itemCountLabel.setText(cartItems.size() + " sản phẩm");
        discountLabel.setText("-" + currencyFormat.format(discount));
        totalLabel.setText(currencyFormat.format(total));
        
        // Update subtotal label (need to find it)
        Component[] components = ((JPanel) getComponent(0)).getComponents();
        // This is a simplified way - in real implementation you'd store references
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
        
        // Show payment dialog
        String[] paymentMethods = {"Tiền mặt", "Thẻ", "Chuyển khoản"};
        String selectedMethod = (String) JOptionPane.showInputDialog(
            this,
            "Chọn phương thức thanh toán:",
            "Thanh toán",
            JOptionPane.QUESTION_MESSAGE,
            null,
            paymentMethods,
            paymentMethods[0]
        );
        
        if (selectedMethod != null) {
            double total = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
            total = total * (1 - discountPercent / 100.0);
            
            int option = JOptionPane.showConfirmDialog(
                this,
                String.format("Xác nhận thanh toán %s bằng %s?", currencyFormat.format(total), selectedMethod),
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION
            );
            
            if (option == JOptionPane.YES_OPTION) {
                // Process payment
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearCart();
                customerField.setText("Khách lẻ");
                notesArea.setText("");
            }
        }
    }
    
    private double parsePrice(String priceString) {
        try {
            return Double.parseDouble(priceString.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    // Public methods
    public boolean hasItems() {
        return !cartItems.isEmpty();
    }
    
    public int getItemCount() {
        return cartItems.size();
    }
    
    public double getTotal() {
        double subtotal = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        return subtotal * (1 - discountPercent / 100.0);
    }
    
    // Inner classes
    private class CartItem {
        private String name;
        private double price;
        private int quantity;
        
        public CartItem(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
        
        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getTotal() { return price * quantity; }
    }
    
    // Custom cell renderer and editor for delete button
    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("🗑️");
            setOpaque(true);
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            setBackground(DANGER_COLOR);
            setForeground(Color.WHITE);
            setBorder(new EmptyBorder(2, 6, 2, 6));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    private class DeleteButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        
        public DeleteButtonEditor() {
            super(new JCheckBox());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText("🗑️");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            button.setBackground(DANGER_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorder(new EmptyBorder(2, 6, 2, 6));
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                removeItem(row);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
        
        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}