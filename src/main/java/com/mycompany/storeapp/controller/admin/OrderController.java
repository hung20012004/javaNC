/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.model.dao.OrderDAO;
import com.mycompany.storeapp.model.entity.Order;
import com.mycompany.storeapp.model.entity.OrderDetail;
import com.mycompany.storeapp.config.DatabaseConnection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Controller for Order management with Kanban board functionality
 * @author Hi
 */
public class OrderController {
    private final OrderDAO orderDAO;
    
    // Định nghĩa các trạng thái đơn hàng theo thứ tự workflow
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_SHIPPING = "shipping";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";
    
    // Danh sách các trạng thái có thể chỉnh sửa (trước khi confirm)
    private static final List<String> EDITABLE_STATUSES = Arrays.asList(STATUS_PENDING);
    
    // Quy tắc chuyển đổi trạng thái
    private static final Map<String, List<String>> STATUS_TRANSITIONS = new HashMap<>();
    
    static {
        STATUS_TRANSITIONS.put(STATUS_PENDING, Arrays.asList(STATUS_CONFIRMED, STATUS_CANCELLED));
        STATUS_TRANSITIONS.put(STATUS_CONFIRMED, Arrays.asList(STATUS_PROCESSING, STATUS_CANCELLED));
        STATUS_TRANSITIONS.put(STATUS_PROCESSING, Arrays.asList(STATUS_SHIPPING, STATUS_CANCELLED));
        STATUS_TRANSITIONS.put(STATUS_SHIPPING, Arrays.asList(STATUS_DELIVERED));
        STATUS_TRANSITIONS.put(STATUS_DELIVERED, Arrays.asList()); // Không thể chuyển sang trạng thái khác
        STATUS_TRANSITIONS.put(STATUS_CANCELLED, Arrays.asList()); // Không thể chuyển sang trạng thái khác
    }

    public OrderController(DatabaseConnection connection) {
        this.orderDAO = new OrderDAO(connection);
    }
    
    /**
     * Lấy tất cả đơn hàng được nhóm theo trạng thái (cho Kanban board)
     */
    public Map<String, List<Order>> getOrdersGroupedByStatus() {
        Map<String, List<Order>> groupedOrders = new HashMap<>();
        
        // Khởi tạo các nhóm trạng thái
        groupedOrders.put(STATUS_PENDING, orderDAO.getOrdersByStatus(STATUS_PENDING));
        groupedOrders.put(STATUS_CONFIRMED, orderDAO.getOrdersByStatus(STATUS_CONFIRMED));
        groupedOrders.put(STATUS_PROCESSING, orderDAO.getOrdersByStatus(STATUS_PROCESSING));
        groupedOrders.put(STATUS_SHIPPING, orderDAO.getOrdersByStatus(STATUS_SHIPPING));
        groupedOrders.put(STATUS_DELIVERED, orderDAO.getOrdersByStatus(STATUS_DELIVERED));
        groupedOrders.put(STATUS_CANCELLED, orderDAO.getOrdersByStatus(STATUS_CANCELLED));
        
        return groupedOrders;
    }
    
    /**
     * Lấy số lượng đơn hàng theo từng trạng thái (cho dashboard)
     */
    public Map<String, Integer> getOrderCountByStatus() {
        Map<String, Integer> counts = new HashMap<>();
        
        counts.put(STATUS_PENDING, orderDAO.getOrderCountByStatus(STATUS_PENDING));
        counts.put(STATUS_CONFIRMED, orderDAO.getOrderCountByStatus(STATUS_CONFIRMED));
        counts.put(STATUS_PROCESSING, orderDAO.getOrderCountByStatus(STATUS_PROCESSING));
        counts.put(STATUS_SHIPPING, orderDAO.getOrderCountByStatus(STATUS_SHIPPING));
        counts.put(STATUS_DELIVERED, orderDAO.getOrderCountByStatus(STATUS_DELIVERED));
        counts.put(STATUS_CANCELLED, orderDAO.getOrderCountByStatus(STATUS_CANCELLED));
        
        return counts;
    }
    
    /**
     * Chuyển đổi trạng thái đơn hàng
     */
    public OrderTransitionResult changeOrderStatus(int orderId, String newStatus) {
        try {
            // Lấy thông tin đơn hàng hiện tại
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                return new OrderTransitionResult(false, "Không tìm thấy đơn hàng");
            }
            
            String currentStatus = order.getOrderStatus().trim();
            
            // Kiểm tra quy tắc chuyển đổi trạng thái
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                return new OrderTransitionResult(false, 
                    String.format("Không thể chuyển từ trạng thái '%s' sang '%s'", 
                    currentStatus, newStatus));
            }
            
            // Thực hiện cập nhật trạng thái
            boolean success = orderDAO.updateOrderStatus(orderId, newStatus);
            
            if (success) {
                return new OrderTransitionResult(true, "Cập nhật trạng thái thành công");
            } else {
                return new OrderTransitionResult(false, "Lỗi khi cập nhật trạng thái");
            }
            
        } catch (Exception e) {
            return new OrderTransitionResult(false, "Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra xem đơn hàng có thể chỉnh sửa không
     */
    public boolean canEditOrder(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            return false;
        }
        
        return EDITABLE_STATUSES.contains(order.getOrderStatus());
    }
    
    /**
     * Kiểm tra xem đơn hàng có thể chỉnh sửa không (theo trạng thái)
     */
    public boolean canEditOrder(String orderStatus) {
        return EDITABLE_STATUSES.contains(orderStatus);
    }
    
    /**
     * Lấy danh sách trạng thái có thể chuyển đổi từ trạng thái hiện tại
     */
    public List<String> getValidNextStatuses(String currentStatus) {
        return STATUS_TRANSITIONS.getOrDefault(currentStatus, Arrays.asList());
    }
    
    /**
     * Kiểm tra tính hợp lệ của việc chuyển đổi trạng thái
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        
        // Cho phép giữ nguyên trạng thái
        if (currentStatus.equals(newStatus)) {
            return true;
        }
        
        List<String> validNextStatuses = STATUS_TRANSITIONS.get(currentStatus);
        return validNextStatuses != null && validNextStatuses.contains(newStatus);
    }
    
    /**
     * Lấy thông tin chi tiết đơn hàng
     */
    public Order getOrderDetails(int orderId) {
        return orderDAO.getOrderById(orderId);
    }
    
    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    /**
     * Lấy đơn hàng theo trạng thái
     */
    public List<Order> getOrdersByStatus(String status) {
        return orderDAO.getOrdersByStatus(status);
    }
    
    /**
     * Hủy đơn hàng (chỉ cho phép khi chưa confirm hoặc đang processing)
     */
    public OrderTransitionResult cancelOrder(int orderId, String reason) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            return new OrderTransitionResult(false, "Không tìm thấy đơn hàng");
        }
        
        String currentStatus = order.getOrderStatus();
        
        // Chỉ cho phép hủy khi đơn hàng ở trạng thái PENDING, CONFIRMED hoặc PROCESSING
        if (!Arrays.asList(STATUS_PENDING, STATUS_CONFIRMED, STATUS_PROCESSING).contains(currentStatus)) {
            return new OrderTransitionResult(false, 
                "Không thể hủy đơn hàng ở trạng thái " + currentStatus);
        }
        
        return changeOrderStatus(orderId, STATUS_CANCELLED);
    }
    
    /**
     * Xác nhận đơn hàng (chuyển từ PENDING sang CONFIRMED)
     */
    public OrderTransitionResult confirmOrder(int orderId) {
        return changeOrderStatus(orderId, STATUS_CONFIRMED);
    }
    
    /**
     * Bắt đầu xử lý đơn hàng (chuyển từ CONFIRMED sang PROCESSING)
     */
    public OrderTransitionResult startProcessing(int orderId) {
        return changeOrderStatus(orderId, STATUS_PROCESSING);
    }
    
    /**
     * Bắt đầu giao hàng (chuyển từ PROCESSING sang SHIPPING)
     */
    public OrderTransitionResult startShipping(int orderId) {
        return changeOrderStatus(orderId, STATUS_SHIPPING);
    }
    
    /**
     * Hoàn thành giao hàng (chuyển từ SHIPPING sang DELIVERED)
     */
    public OrderTransitionResult completeDelivery(int orderId) {
        return changeOrderStatus(orderId, STATUS_DELIVERED);
    }
    
    /**
     * Lấy danh sách tất cả trạng thái có thể có
     */
    public List<String> getAllStatuses() {
        return Arrays.asList(
            STATUS_PENDING,
            STATUS_CONFIRMED, 
            STATUS_PROCESSING,
            STATUS_SHIPPING,
            STATUS_DELIVERED,
            STATUS_CANCELLED
        );
    }
    
    /**
     * Class để trả về kết quả của việc chuyển đổi trạng thái
     */
    public static class OrderTransitionResult {
        private final boolean success;
        private final String message;
        
        public OrderTransitionResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("OrderTransitionResult{success=%s, message='%s'}", success, message);
        }
    }
}