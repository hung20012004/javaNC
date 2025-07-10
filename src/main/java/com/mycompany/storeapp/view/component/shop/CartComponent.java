package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.controller.admin.CartController;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.CartItem;
import com.mycompany.storeapp.model.entity.ProductVariant;
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
import java.math.BigDecimal;

public class CartComponent extends JPanel {
    private static final java.awt.Color BACKGROUND_COLOR = java.awt.Color.WHITE;
    private static final java.awt.Color BORDER_COLOR = new java.awt.Color(229, 231, 235);
    private static final java.awt.Color HEADER_COLOR = new java.awt.Color(249, 250, 251);
    private static final java.awt.Color PRIMARY_COLOR = new java.awt.Color(59, 130, 246);
    private static final java.awt.Color SUCCESS_COLOR = new java.awt.Color(16, 185, 129);
    private static final java.awt.Color DANGER_COLOR = new java.awt.Color(239, 68, 68);
    private static final java.awt.Color TEXT_COLOR = new java.awt.Color(55, 65, 81);
    private static final java.awt.Color SECONDARY_TEXT_COLOR = new java.awt.Color(107, 114, 128);

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
    
    private List<CartItem> cartItems;
    private double discountPercent = 0.0;
    private DecimalFormat currencyFormat;
    private CartController cartController;
    private ProductVariantController variantController;
    private int cartId = 1; // Giả sử cart_id = 1, cần thay bằng logic lấy cart_id hiện tại

    public CartComponent() {
        cartItems = new ArrayList<>();
        currencyFormat = new DecimalFormat("#,###,### ₫");
        cartController = new CartController();
        variantController = new ProductVariantController();
        loadCartFromDB();
        initializeCart();
        setupComponents();
        refreshTable();
        setupLayout();
        updateCartSummary();
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
        JPanel headerPanel = createHeaderPanel();
        JPanel tablePanel = createTablePanel();
        JPanel customerPanel = createCustomerPanel();
        JPanel summaryPanel = createSummaryPanel();
        JPanel actionPanel = createActionPanel();

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
        clearCartButton.setForeground(java.awt.Color.WHITE);
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
                return column == 1 || column == 4;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return JPanel.class; // Cột SL là JPanel chứa nút
                return Object.class;
            }
        };

        cartTable = new JTable(tableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 1) {
                    return new QuantityPanelRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.setRowHeight(40);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 1));
        cartTable.setSelectionBackground(new java.awt.Color(239, 246, 255));

        cartTable.getColumn("").setCellRenderer(new DeleteButtonRenderer());
        cartTable.getColumn("").setCellEditor(new DeleteButtonEditor());

        cartTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(80); // Điều chỉnh độ rộng cho panel SL
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(30);

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

        JLabel subtotalLabel = new JLabel("Tạm tính:");
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtotalLabel.setForeground(TEXT_COLOR);

        JLabel subtotalValue = new JLabel("0 ₫");
        subtotalValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtotalValue.setForeground(TEXT_COLOR);
        subtotalValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel discountLabelText = new JLabel("Giảm giá:");
        discountLabelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountLabelText.setForeground(TEXT_COLOR);

        discountLabel = new JLabel("0 ₫");
        discountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountLabel.setForeground(DANGER_COLOR);
        discountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel taxLabel = new JLabel("Thuế (0%):");
        taxLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taxLabel.setForeground(TEXT_COLOR);

        JLabel taxValue = new JLabel("0 ₫");
        taxValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taxValue.setForeground(TEXT_COLOR);
        taxValue.setHorizontalAlignment(SwingConstants.RIGHT);

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

        discountButton = new JButton("💰 Giảm giá");
        discountButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        discountButton.setBackground(new java.awt.Color(251, 191, 36));
        discountButton.setForeground(java.awt.Color.WHITE);
        discountButton.setBorder(new EmptyBorder(10, 15, 10, 15));
        discountButton.setFocusPainted(false);
        discountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        discountButton.addActionListener(e -> showDiscountDialog());

        checkoutButton = new JButton("💳 Thanh toán");
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutButton.setBackground(SUCCESS_COLOR);
        checkoutButton.setForeground(java.awt.Color.WHITE);
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

    public void addItem(int variantId, int quantity) {
        ProductVariant variant = variantController.getVariantById(variantId);
        if (variant != null) {
            CartItem newItem = new CartItem(cartId, variantId, quantity);
            cartController.addItem(newItem);
            loadCartFromDB();
            refreshTable();
            updateCartSummary();
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < cartItems.size()) {
            cartController.removeItem(cartItems.get(index).getCartItemId());
            loadCartFromDB();
            refreshTable();
            updateCartSummary();
        }
    }

    public void updateQuantity(int index, int quantity) {
        if (index >= 0 && index < cartItems.size()) {
            if (quantity <= 0) {
                removeItem(index);
            } else {
                cartController.updateQuantity(cartItems.get(index).getCartItemId(), quantity);
                loadCartFromDB();
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
            cartController.clearCart(cartId);
            cartItems.clear();
            refreshTable();
            updateCartSummary();
        }
    }

    public boolean hasItems() {
        return !cartItems.isEmpty();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            ProductVariant variant = variantController.getVariantById(item.getVariantId());
            if (variant != null) {
                BigDecimal price = variant.getPrice();
                String productName = (variant.getName() != null && !variant.getName().equals("Không xác định"))
                    ? variant.getName()
                    : "Sản phẩm không xác định (ID: " + item.getVariantId() + ")";
                JPanel quantityPanel = createQuantityPanel(i, item.getQuantity());
                Object[] row = {
                    productName,
                    quantityPanel, // Thay cột SL bằng panel chứa nút
                    currencyFormat.format(price),
                    currencyFormat.format(price.multiply(BigDecimal.valueOf(item.getQuantity()))),
                    "Xóa"
                };
                tableModel.addRow(row);
            } else {
                System.err.println("Không tìm thấy variant với ID: " + item.getVariantId());
            }
        }
    }

    private JPanel createQuantityPanel(int index, int currentQuantity) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        JButton decreaseButton = new JButton("-");
        decreaseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        decreaseButton.setBackground(DANGER_COLOR);
        decreaseButton.setForeground(java.awt.Color.WHITE);
        decreaseButton.setBorder(new EmptyBorder(2, 6, 2, 6));
        decreaseButton.setFocusPainted(false);
        decreaseButton.addActionListener(e -> {
            if (currentQuantity > 1) {
                updateQuantity(index, currentQuantity - 1);
            }
        });

        JLabel quantityLabel = new JLabel(String.valueOf(currentQuantity));
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        quantityLabel.setForeground(TEXT_COLOR);

        JButton increaseButton = new JButton("+");
        increaseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        increaseButton.setBackground(SUCCESS_COLOR);
        increaseButton.setForeground(java.awt.Color.WHITE);
        increaseButton.setBorder(new EmptyBorder(2, 6, 2, 6));
        increaseButton.setFocusPainted(false);
        increaseButton.addActionListener(e -> {
            updateQuantity(index, currentQuantity + 1);
        });

        panel.add(decreaseButton);
        panel.add(quantityLabel);
        panel.add(increaseButton);

        return panel;
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

        itemCountLabel.setText(cartItems.size() + " sản phẩm");
        discountLabel.setText("-" + currencyFormat.format(discount));
        totalLabel.setText(currencyFormat.format(total));
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
            double total = cartItems.stream()
                .mapToDouble(item -> {
                    ProductVariant variant = variantController.getVariantById(item.getVariantId());
                    if (variant != null) {
                        return variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue();
                    }
                    return 0.0;
                }).sum();
            total = total * (1 - discountPercent / 100.0);

            int option = JOptionPane.showConfirmDialog(
                this,
                String.format("Xác nhận thanh toán %s bằng %s?", currencyFormat.format(total), selectedMethod),
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION
            );

            if (option == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                clearCart();
                customerField.setText("Khách lẻ");
                notesArea.setText("");
            }
        }
    }

    private void loadCartFromDB() {
        cartItems.clear();
        cartItems.addAll(cartController.getAllItems(cartId));
    }

    private class QuantityPanelRenderer extends JPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return (JPanel) value;
        }
    }

    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("🗑️");
            setOpaque(true);
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            setBackground(DANGER_COLOR);
            setForeground(java.awt.Color.WHITE);
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
            button.setForeground(java.awt.Color.WHITE);
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