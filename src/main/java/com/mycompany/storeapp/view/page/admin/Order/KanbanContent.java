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

public class KanbanContent extends JPanel {
    
    private final OrderController orderController;
    private JPanel kanbanPanel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private Map<String, JPanel> statusColumns;
    private Map<String, JScrollPane> columnScrollPanes;
    private Map<String, List<Order>> ordersData;
    private Runnable refreshCallback;
    
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
        this.columnScrollPanes = new java.util.HashMap<>();
        
        initComponents();
        setupLayout();
    }
    
    private void initComponents() {
        headerPanel = new JPanel();
        contentPanel = new JPanel();
        kanbanPanel = new JPanel(new BorderLayout());
        
        headerPanel.setBackground(new Color(236, 240, 241));
        contentPanel.setBackground(new Color(236, 240, 241));
        kanbanPanel.setBackground(new Color(236, 240, 241));
        
        setupKanbanBoard();
    }
    
    private void setupKanbanBoard() {
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
        
        createStatusColumn("pending", "Chờ xử lý", PENDING_COLOR);
        createStatusColumn("confirmed", "Đã xác nhận", CONFIRMED_COLOR);
        createStatusColumn("processing", "Đang xử lý", PROCESSING_COLOR);
        createStatusColumn("shipping", "Đang giao", SHIPPING_COLOR);
        createStatusColumn("delivered", "Đã giao", DELIVERED_COLOR);
        createStatusColumn("cancelled", "Đã hủy", CANCELLED_COLOR);
        
        kanbanPanel.add(headerPanel, BorderLayout.NORTH);
        kanbanPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createStatusColumn(String status, String title, Color headerColor) {
        JPanel columnHeader = new JPanel(new BorderLayout());
        columnHeader.setBackground(headerColor);
        columnHeader.setBorder(new EmptyBorder(15, 15, 15, 15));
        columnHeader.setPreferredSize(new Dimension(250, 60));
        
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
        
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(250, 400));
        
        setupDropTarget(cardsPanel, status);
        
        headerPanel.add(columnHeader);
        contentPanel.add(scrollPane);
        
        statusColumns.put(status, cardsPanel);
        columnScrollPanes.put(status, scrollPane);
    }
    
    private void setupDropTarget(JPanel panel, String targetStatus) {
        new DropTarget(panel, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                panel.setBackground(new Color(232, 245, 233));
            }
            
            @Override
            public void dragOver(DropTargetDragEvent dtde) {
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

            if (orderData == null || orderData.currentStatus == null) {
                showErrorDialog("Dữ liệu đơn hàng không hợp lệ!");
                dtde.dropComplete(false);
                return;
            }

            String normalizedCurrentStatus = orderData.currentStatus.trim();
            String normalizedTargetStatus = targetStatus.trim();

            if (normalizedCurrentStatus.equals(normalizedTargetStatus)) {
                dtde.dropComplete(true);
                return;
            }

            List<String> validNextStatuses = orderController.getValidNextStatuses(normalizedCurrentStatus);

            if (validNextStatuses != null && validNextStatuses.contains(normalizedTargetStatus)) {
                OrderController.OrderTransitionResult result = 
                    orderController.changeOrderStatus(orderData.orderId, normalizedTargetStatus);

                if (result.isSuccess()) {
                    if ("cancelled".equals(normalizedTargetStatus)) {
                        boolean quantityRestored = orderController.restoreProductQuantity(orderData.orderId);
                        if (quantityRestored) {
                            showSuccessDialog("Cập nhật trạng thái thành công! Số lượng sản phẩm đã được khôi phục.");
                        } else {
                            showWarningDialog("Cập nhật trạng thái thành công! Tuy nhiên có lỗi khi khôi phục số lượng sản phẩm.");
                        }
                    } else {
                        showSuccessDialog("Cập nhật trạng thái thành công!");
                    }
                    
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
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
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        add(kanbanPanel, BorderLayout.CENTER);
    }
    
    public void loadData(Map<String, List<Order>> ordersData, Map<String, Integer> counts) {
        this.ordersData = ordersData;
        
        for (JPanel panel : statusColumns.values()) {
            panel.removeAll();
        }
        
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
                
                if (!orders.isEmpty()) {
                    column.add(Box.createVerticalGlue());
                }
            }
            
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
        card.setPreferredSize(new Dimension(220, 120));
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel idLabel = new JLabel("Đơn hàng #" + order.getOrderId());
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        idLabel.setForeground(new Color(51, 51, 51));
        
        JLabel dateLabel = new JLabel(order.getOrderDate().format(dateFormatter));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(127, 140, 141));
        
        JLabel customerLabel = new JLabel("KH: " + (order.getCustomerName() != null ? order.getCustomerName() : "N/A"));
        customerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customerLabel.setForeground(new Color(51, 51, 51));
        
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
        
        setupDragSource(card, order);
        
       
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
        Component[] headerComponents = headerPanel.getComponents();
        for (Component comp : headerComponents) {
            if (comp instanceof JPanel) {
                JPanel headerColumn = (JPanel) comp;
                Component[] headerComps = headerColumn.getComponents();
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