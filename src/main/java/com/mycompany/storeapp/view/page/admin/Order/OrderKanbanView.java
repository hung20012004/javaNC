package com.mycompany.storeapp.view.page.admin.Order;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.controller.admin.OrderController;
import com.mycompany.storeapp.model.entity.Order;
import com.mycompany.storeapp.view.component.admin.KanbanComponent.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class OrderKanbanView extends JPanel {
    private final OrderController orderController;
    private KanbanHeader header;
    private KanbanContent content;
    
    private Map<String, List<Order>> ordersData;
    private Map<String, Integer> orderCounts;
    
    public OrderKanbanView() {
        var dbConnection = new DatabaseConnection();
        this.orderController = new OrderController(dbConnection);
        
        initComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
    }
    
    private void initComponents() {
        header = new KanbanHeader();
        content = new KanbanContent(orderController);
        content.setRefreshCallback(this::refreshData);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        add(header, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        header.addRefreshListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
    }
    
    private void loadData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    ordersData = orderController.getOrdersGroupedByStatus();
                    orderCounts = orderController.getOrderCountByStatus();
                    
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        showErrorDialog("Lỗi khi tải dữ liệu: " + e.getMessage());
                    });
                    throw e;
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    
                    content.loadData(ordersData, orderCounts);
                    
                } catch (Exception e) {
                    showErrorDialog("Lỗi khi cập nhật giao diện: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    private double calculateTotalRevenue() {
        double totalRevenue = 0.0;
        
        if (ordersData != null) {
            for (Map.Entry<String, List<Order>> entry : ordersData.entrySet()) {
                String status = entry.getKey();
                List<Order> orders = entry.getValue();
                
                if ("delivered".equals(status)) {
                    for (Order order : orders) {
                        totalRevenue += order.getTotalAmount();
                    }
                }
            }
        }
        
        return totalRevenue;
    }
    
    private void refreshData() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        header.getRefreshButton().setText("Đang tải...");
        header.getRefreshButton().setEnabled(false);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(500);
                
                ordersData = orderController.getOrdersGroupedByStatus();
                orderCounts = orderController.getOrderCountByStatus();
                
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    
                    content.loadData(ordersData, orderCounts);
                    
                } catch (Exception e) {
                    showErrorDialog("Lỗi khi làm mới dữ liệu: " + e.getMessage());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    header.getRefreshButton().setText("Làm mới");
                    header.getRefreshButton().setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    
    public void refreshView() {
        refreshData();
    }
    
    public KanbanHeader getHeader() {
        return header;
    }
    
    public KanbanContent getContent() {
        return content;
    }
    
    public OrderController getOrderController() {
        return orderController;
    }
    
    public Map<String, List<Order>> getOrdersData() {
        return ordersData;
    }
    
    public Map<String, Integer> getOrderCounts() {
        return orderCounts;
    }
    
    public void addCustomEventHandlers() {
    }
    
    public void customizeAppearance() {
    }
}