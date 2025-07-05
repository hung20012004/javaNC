/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.page.admin.Order;

import com.mycompany.storeapp.controller.admin.OrderController;
import com.mycompany.storeapp.model.entity.Order;
import com.mycompany.storeapp.view.page.admin.Order.OrderEditDialog;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Content component cho Kanban board - chứa các cột và cards
 * @author Hi
 */
public class KanbanContent extends JPanel {
    
    private final OrderController orderController;
    private JPanel kanbanPanel;
    private Map<String, JPanel> statusColumns;
    private Map<String, List<Order>> ordersData;
    private Runnable refreshCallback;
    
    // Color scheme cho các trạng thái
    private static final Color PENDING_COLOR = new Color(241, 196, 15);
    private static final Color CONFIRMED_COLOR = new Color(52, 152, 219);
    private static final Color PROCESSING_COLOR = new Color(155, 89, 182);
    private static final Color SHIPPING_COLOR = new Color(230, 126, 34);
    private static final Color DELIVERED_COLOR = new Color(46, 204, 113);
    private static final Color CANCELLED_COLOR = new Color(231, 76, 60);
    
    private final NumberFormat currencyFormat;
    private final DateTimeFormatter dateFormatter;
    
    public KanbanContent(OrderController orderController) {
        this.orderController = orderController;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.statusColumns = new java.util.HashMap<>();
        
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        // Kanban panel
        kanbanPanel = new JPanel();
        kanbanPanel.setBackground(new Color(236, 240, 241));
        
        setupKanbanBoard();
    }
    
    private void setupKanbanBoard() {
        kanbanPanel.setLayout(new BoxLayout(kanbanPanel, BoxLayout.X_AXIS));
        
        // Tạo các cột cho từng trạng thái
        createStatusColumn("pending", "Chờ xử lý", PENDING_COLOR);
        createStatusColumn("confirmed", "Đã xác nhận", CONFIRMED_COLOR);
        createStatusColumn("processing", "Đang xử lý", PROCESSING_COLOR);
        createStatusColumn("shipping", "Đang giao", SHIPPING_COLOR);
        createStatusColumn("delivered", "Đã giao", DELIVERED_COLOR);
        createStatusColumn("cancelled", "Đã hủy", CANCELLED_COLOR);
    }
    
    private void createStatusColumn(String status, String title, Color headerColor) {
        JPanel column = new JPanel(new BorderLayout());
        column.setBackground(Color.WHITE);
        column.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        // Header của cột
        JPanel columnHeader = new JPanel(new BorderLayout());
        columnHeader.setBackground(headerColor);
        columnHeader.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel countLabel = new JLabel("0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(Color.WHITE);
        countLabel.setOpaque(true);
        countLabel.setBackground(new Color(0, 0, 0, 50));
        countLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        countLabel.setName(status + "_count");
        
        columnHeader.add(titleLabel, BorderLayout.CENTER);
        columnHeader.add(countLabel, BorderLayout.EAST);
        
        // Content area - scrollable
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Setup drop target
        setupDropTarget(contentPanel, status);
        
        column.add(columnHeader, BorderLayout.NORTH);
        column.add(scrollPane, BorderLayout.CENTER);
        
        kanbanPanel.add(column);
        
        statusColumns.put(status, contentPanel);
    }
    
    private void setupDropTarget(JPanel panel, String targetStatus) {
        new DropTarget(panel, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                panel.setBackground(new Color(232, 245, 233));
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                // Highlight drop zone
            }
            
            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}
            
            @Override
            public void dragExit(DropTargetEvent dte) {
                panel.setBackground(Color.WHITE);
            }
            
            @Override
            public void drop(DropTargetDropEvent dtde) {
                panel.setBackground(Color.WHITE);
                handleDrop(dtde, targetStatus);
            }
        });
    }
    
    private void handleDrop(DropTargetDropEvent dtde, String targetStatus) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);

            Transferable transferable = dtde.getTransferable();
            OrderTransferData orderData = (OrderTransferData) transferable.getTransferData(OrderTransferData.ORDER_FLAVOR);

            // Kiểm tra null và validate trạng thái
            if (orderData == null || orderData.currentStatus == null) {
                showErrorDialog("Dữ liệu đơn hàng không hợp lệ!");
                dtde.dropComplete(false);
                return;
            }

            // Normalize status (trim)
            String normalizedCurrentStatus = orderData.currentStatus.trim();
            String normalizedTargetStatus = targetStatus.trim();

            // Nếu trạng thái giống nhau thì không cần làm gì
            if (normalizedCurrentStatus.equals(normalizedTargetStatus)) {
                dtde.dropComplete(true);
                return;
            }

            // Lấy danh sách trạng thái hợp lệ
            List<String> validNextStatuses = orderController.getValidNextStatuses(normalizedCurrentStatus);

            // Kiểm tra tính hợp lệ của việc chuyển đổi
            if (validNextStatuses != null && validNextStatuses.contains(normalizedTargetStatus)) {
                // Thực hiện chuyển đổi trạng thái
                OrderController.OrderTransitionResult result = 
                    orderController.changeOrderStatus(orderData.orderId, normalizedTargetStatus);

                if (result.isSuccess()) {
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                    showSuccessDialog("Cập nhật trạng thái thành công!");
                } else {
                    showErrorDialog(result.getMessage());
                }
            } else {
                String validStatusesStr = validNextStatuses != null ? 
                    String.join(", ", validNextStatuses) : "Không có";
                showWarningDialog(String.format(
                    "Không thể chuyển từ '%s' sang '%s'.\nCác trạng thái hợp lệ: %s", 
                    normalizedCurrentStatus, normalizedTargetStatus, validStatusesStr));
            }

            dtde.dropComplete(true);
        } catch (Exception e) {
            dtde.dropComplete(false);
            e.printStackTrace();
            showErrorDialog("Lỗi khi chuyển đổi trạng thái: " + e.getMessage());
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));
        
        // Wrap kanban panel in scroll pane for horizontal scrolling
        JScrollPane horizontalScrollPane = new JScrollPane(kanbanPanel);
        horizontalScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        horizontalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        horizontalScrollPane.setBorder(null);
        
        add(horizontalScrollPane, BorderLayout.CENTER);
    }
    
    public void loadData(Map<String, List<Order>> ordersData, Map<String, Integer> counts) {
        this.ordersData = ordersData;
        
        // Clear existing cards
        for (JPanel panel : statusColumns.values()) {
            panel.removeAll();
        }
        
        // Add cards to each column
        for (Map.Entry<String, List<Order>> entry : ordersData.entrySet()) {
            String status = entry.getKey();
            List<Order> orders = entry.getValue();
            JPanel column = statusColumns.get(status);
            
            if (column != null) {
                for (Order order : orders) {
                    JPanel orderCard = createOrderCard(order);
                    column.add(orderCard);
                    column.add(Box.createVerticalStrut(10));
                }
            }
            
            // Update count labels
            updateCountLabel(status, counts.getOrDefault(status, 0));
        }
        
        revalidate();
        repaint();
    }
    
    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setPreferredSize(new Dimension(200, 120));
        
        // Order info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        // Order ID and Date
        JLabel idLabel = new JLabel("Đơn hàng #" + order.getOrderId());
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(new Color(51, 51, 51));
        
        JLabel dateLabel = new JLabel(order.getOrderDate().format(dateFormatter));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(127, 140, 141));
        
        // Customer info
        JLabel customerLabel = new JLabel("KH: " + (order.getCustomerName() != null ? order.getCustomerName() : "N/A"));
        customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerLabel.setForeground(new Color(51, 51, 51));
        
        // Total amount
        JLabel totalLabel = new JLabel(currencyFormat.format(order.getTotalAmount()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(new Color(46, 204, 113));
        
        infoPanel.add(idLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(dateLabel);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(customerLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(totalLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        // Make card draggable
        setupDragSource(card, order);
        
        // Add click listener for editing (only for PENDING orders)
        if ("pending".equals(order.getOrderStatus())) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        showEditOrderDialog(order);
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(241, 248, 255));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(Color.WHITE);  
                }
            });
        }
        
        return card;
    }
    
    private void setupDragSource(JPanel card, Order order) {
        DragSource ds = new DragSource();
        ds.createDefaultDragGestureRecognizer(card, DnDConstants.ACTION_MOVE, 
            new DragGestureListener() {
                @Override
                public void dragGestureRecognized(DragGestureEvent dge) {
                    OrderTransferData transferData = new OrderTransferData(
                        order.getOrderId(), 
                        order.getOrderStatus()
                    );
                    
                    dge.startDrag(DragSource.DefaultMoveDrop, transferData);
                }
            });
        
        // Visual feedback during drag
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                card.setBackground(new Color(255, 249, 196));
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });
    }
    
    private void updateCountLabel(String status, int count) {
        Component[] components = kanbanPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel column = (JPanel) comp;
                Component header = column.getComponent(0);
                if (header instanceof JPanel) {
                    JPanel headerPanel = (JPanel) header;
                    Component[] headerComps = headerPanel.getComponents();
                    for (Component headerComp : headerComps) {
                        if (headerComp instanceof JLabel) {
                            JLabel label = (JLabel) headerComp;
                            if ((status + "_count").equals(label.getName())) {
                                label.setText(String.valueOf(count));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void showEditOrderDialog(Order order) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        OrderEditDialog.showDialog(parentWindow, order, orderController, refreshCallback);
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }
    
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }
    
    public Map<String, List<Order>> getOrdersData() {
        return ordersData;
    }
    
    public Map<String, JPanel> getStatusColumns() {
        return statusColumns;
    }
    
    // Inner class for drag & drop data transfer
    public static class OrderTransferData implements Transferable {
        public static final DataFlavor ORDER_FLAVOR = new DataFlavor(OrderTransferData.class, "Order");
        
        private final int orderId;
        private final String currentStatus;
        
        public OrderTransferData(int orderId, String currentStatus) {
            this.orderId = orderId;
            this.currentStatus = currentStatus;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{ORDER_FLAVOR};
        }
        
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return ORDER_FLAVOR.equals(flavor);
        }
        
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return this;
        }
    }
}
