package com.mycompany.storeapp.view.component.shop.cart;

import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.entity.CartItem;
import com.mycompany.storeapp.model.entity.ProductVariant;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class CartTableComponent extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(55, 65, 81);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);

    private JTable cartTable;
    private DefaultTableModel tableModel;
    private ProductVariantController variantController;
    private DecimalFormat currencyFormat;
    private CartTableListener listener;
    private List<CartItem> cartItems; // Lưu reference để update

    public interface CartTableListener {
        void onQuantityChanged(int index, int newQuantity);
        void onItemRemoved(int index);
    }

    public CartTableComponent() {
        variantController = new ProductVariantController();
        currencyFormat = new DecimalFormat("#,###,### ₫");
        setupComponent();
    }

    private void setupComponent() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(0, 15, 15, 15));

        String[] columns = {"Sản phẩm", "SL", "Giá", "Thành tiền", ""};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 4;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return JPanel.class;
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
            
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 1) {
                    return new QuantityPanelEditor();
                }
                return super.getCellEditor(row, column);
            }
        };

        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartTable.setRowHeight(40);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 1));
        cartTable.setSelectionBackground(new Color(239, 246, 255));

        cartTable.getColumn("").setCellRenderer(new DeleteButtonRenderer());
        cartTable.getColumn("").setCellEditor(new DeleteButtonEditor());

        cartTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(30);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.setPreferredSize(new Dimension(0, 300));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void setListener(CartTableListener listener) {
        this.listener = listener;
    }

    public void updateTable(List<CartItem> cartItems) {
        this.cartItems = cartItems; // Lưu reference
        tableModel.setRowCount(0);

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            ProductVariant variant = variantController.getVariantById(item.getVariantId());
            if (variant != null) {
                BigDecimal price = variant.getPrice();
                String productName = (variant.getName() != null && !variant.getName().equals("Không xác định"))
                    ? variant.getName()
                    : "Sản phẩm không xác định (ID: " + item.getVariantId() + ")";
                
                // Tạo một object để store quantity info
                QuantityInfo quantityInfo = new QuantityInfo(i, item.getQuantity());
                
                Object[] row = {
                    productName,
                    quantityInfo,
                    currencyFormat.format(price),
                    currencyFormat.format(price.multiply(BigDecimal.valueOf(item.getQuantity()))),
                    "Xóa"
                };
                tableModel.addRow(row);
            }
        }
        
        // Force repaint để update UI
        cartTable.revalidate();
        cartTable.repaint();
    }

    // Helper class để lưu thông tin quantity
    private static class QuantityInfo {
        public final int index;
        public final int quantity;
        
        public QuantityInfo(int index, int quantity) {
            this.index = index;
            this.quantity = quantity;
        }
    }

    private JPanel createQuantityPanel(int index, int currentQuantity) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(false);

        JButton decreaseButton = new JButton("-");
        decreaseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        decreaseButton.setBackground(DANGER_COLOR);
        decreaseButton.setForeground(Color.WHITE);
        decreaseButton.setBorder(new EmptyBorder(2, 6, 2, 6));
        decreaseButton.setFocusPainted(false);
        decreaseButton.addActionListener(e -> {
            if (currentQuantity > 1 && listener != null) {
                listener.onQuantityChanged(index, currentQuantity - 1);
                
                // Cập nhật ngay lập tức UI sau khi thay đổi
                SwingUtilities.invokeLater(() -> {
                    // Stop editing để refresh cell
                    if (cartTable.isEditing()) {
                        cartTable.getCellEditor().stopCellEditing();
                    }
                    
                    // Cập nhật lại data trong table model
                    if (index < cartItems.size()) {
                        CartItem item = cartItems.get(index);
                        ProductVariant variant = variantController.getVariantById(item.getVariantId());
                        if (variant != null) {
                            BigDecimal price = variant.getPrice();
                            // Cập nhật quantity info
                            QuantityInfo newQuantityInfo = new QuantityInfo(index, item.getQuantity());
                            tableModel.setValueAt(newQuantityInfo, index, 1);
                            // Cập nhật thành tiền
                            tableModel.setValueAt(currencyFormat.format(price.multiply(BigDecimal.valueOf(item.getQuantity()))), index, 3);
                        }
                    }
                });
            }
        });

        JLabel quantityLabel = new JLabel(String.valueOf(currentQuantity));
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        quantityLabel.setForeground(TEXT_COLOR);

        JButton increaseButton = new JButton("+");
        increaseButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        increaseButton.setBackground(SUCCESS_COLOR);
        increaseButton.setForeground(Color.WHITE);
        increaseButton.setBorder(new EmptyBorder(2, 6, 2, 6));
        increaseButton.setFocusPainted(false);
        increaseButton.addActionListener(e -> {
            if (listener != null) {
                listener.onQuantityChanged(index, currentQuantity + 1);
                
                // Cập nhật ngay lập tức UI sau khi thay đổi
                SwingUtilities.invokeLater(() -> {
                    // Stop editing để refresh cell
                    if (cartTable.isEditing()) {
                        cartTable.getCellEditor().stopCellEditing();
                    }
                    
                    // Cập nhật lại data trong table model
                    if (index < cartItems.size()) {
                        CartItem item = cartItems.get(index);
                        ProductVariant variant = variantController.getVariantById(item.getVariantId());
                        if (variant != null) {
                            BigDecimal price = variant.getPrice();
                            // Cập nhật quantity info
                            QuantityInfo newQuantityInfo = new QuantityInfo(index, item.getQuantity());
                            tableModel.setValueAt(newQuantityInfo, index, 1);
                            // Cập nhật thành tiền
                            tableModel.setValueAt(currencyFormat.format(price.multiply(BigDecimal.valueOf(item.getQuantity()))), index, 3);
                        }
                    }
                });
            }
        });

        panel.add(decreaseButton);
        panel.add(quantityLabel);
        panel.add(increaseButton);

        return panel;
    }

    private class QuantityPanelRenderer extends JPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof QuantityInfo) {
                QuantityInfo info = (QuantityInfo) value;
                return createQuantityPanel(info.index, info.quantity);
            }
            return new JPanel();
        }
    }

    // Thêm Editor cho quantity panel
    private class QuantityPanelEditor extends DefaultCellEditor {
        private JPanel panel;
        private QuantityInfo quantityInfo;

        public QuantityPanelEditor() {
            super(new JCheckBox());
            setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof QuantityInfo) {
                quantityInfo = (QuantityInfo) value;
                panel = createQuantityPanel(quantityInfo.index, quantityInfo.quantity);
                return panel;
            }
            return new JPanel();
        }

        @Override
        public Object getCellEditorValue() {
            return quantityInfo;
        }
    }

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
            if (isPushed && listener != null) {
                listener.onItemRemoved(row);
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