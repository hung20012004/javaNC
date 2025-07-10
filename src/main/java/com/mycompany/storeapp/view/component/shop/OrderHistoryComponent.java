
package com.mycompany.storeapp.view.component.shop;

import com.mycompany.storeapp.controller.admin.OrderController;
import com.mycompany.storeapp.model.entity.Order;
import com.mycompany.storeapp.config.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class OrderHistoryComponent extends JPanel {
    private static final Color CONTENT_BACKGROUND = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    
    private final OrderController orderController;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,###,### ₫");
    
    public OrderHistoryComponent() {
        this.orderController = new OrderController(new DatabaseConnection());
        initializeComponent();
        setupLayout();
        loadOrders(null); // Tải tất cả đơn hàng ban đầu
    }
    
    private void initializeComponent() {
        setBackground(CONTENT_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Khởi tạo bảng
        String[] columns = {"Mã đơn", "Ngày đặt", "Khách hàng", "Tổng tiền", "Trạng thái", "Hành động"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Chỉ cột hành động (nút) có thể tương tác
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setRowHeight(30);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderTable.getTableHeader().setBackground(PRIMARY_COLOR);
        orderTable.getTableHeader().setForeground(Color.WHITE);
        
        // Tùy chỉnh renderer cho cột hành động
        orderTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Tùy chỉnh chiều rộng cột
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Bộ lọc trạng thái
        statusFilter = new JComboBox<>(new String[]{
            "Tất cả", "pending", "confirmed", "processing", "shipping", "delivered", "cancelled"
        });
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.addActionListener(e -> filterOrders());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel bộ lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(CONTENT_BACKGROUND);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        filterPanel.add(new JLabel("Lọc theo trạng thái:"));
        filterPanel.add(statusFilter);
        
        // Panel bảng
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadOrders(String status) {
        tableModel.setRowCount(0);
        
        List<Order> orders = status == null || status.equals("Tất cả") 
            ? orderController.getAllOrders()
            : orderController.getOrdersByStatus(status);
        
        if (orders == null || orders.isEmpty()) {
            tableModel.addRow(new Object[]{"", "Không có dữ liệu", "", "", "", ""});
            return;
        }
        
        for (Order order : orders) {
            Object[] row = {
                order.getOrderId(),
                order.getOrderDate() != null ? order.getOrderDate().toString() : "N/A",
                order.getCustomerName() != null ? order.getCustomerName() : "Khách vãng lai",
                currencyFormat.format(order.getTotalAmount()),
                getStatusDisplayName(order.getOrderStatus()),
                "Xem chi tiết"
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterOrders() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        loadOrders(selectedStatus);
    }
    
    private String getStatusDisplayName(String status) {
        switch (status) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "processing": return "Đang xử lý";
            case "shipping": return "Đang giao";
            case "delivered": return "Đã giao";
            case "cancelled": return "Đã hủy";
            default: return status;
        }
    }
    
    // Renderer cho nút trong cột hành động
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBackground(PRIMARY_COLOR);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    // Editor cho nút trong cột hành động
    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int orderId = (int) tableModel.getValueAt(orderTable.getSelectedRow(), 0);
                showOrderDetails(orderId);
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
    
    private void showOrderDetails(int orderId) {
        System.out.println("Loading details for orderId: " + orderId);
        Order order = orderController.getOrderDetails(orderId);
        if (order == null) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy thông tin đơn hàng #" + orderId + "!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((Dialog) SwingUtilities.getWindowAncestor(this), 
            "Chi tiết đơn hàng #" + orderId, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(CONTENT_BACKGROUND);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Mã đơn hàng:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(String.valueOf(order.getOrderId())), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        detailsPanel.add(new JLabel("Ngày đặt:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(order.getOrderDate() != null ? order.getOrderDate().toString() : "N/A"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        detailsPanel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(order.getCustomerName() != null ? order.getCustomerName() : "Khách vãng lai"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        detailsPanel.add(new JLabel("Địa chỉ giao:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "Chưa có địa chỉ"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        detailsPanel.add(new JLabel("Phương thức thanh toán:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(order.getPaymentMethodDisplayName() != null ? order.getPaymentMethodDisplayName() : "N/A"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        detailsPanel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(currencyFormat.format(order.getTotalAmount())), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        detailsPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(getStatusDisplayName(order.getOrderStatus())), gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        detailsPanel.add(new JLabel("Sản phẩm:"), gbc);
        gbc.gridx = 1;
//        detailsPanel.add(new JLabel(order.getProductNames() != null ? order.getProductNames() : "Chưa có sản phẩm"), gbc);
        
        dialog.add(detailsPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
}