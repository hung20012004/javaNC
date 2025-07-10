
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.controller.admin;

import com.mycompany.storeapp.model.dao.OrderDAO;
import com.mycompany.storeapp.model.entity.Order;
import com.mycompany.storeapp.model.entity.OrderDetail;
import com.mycompany.storeapp.model.entity.ShippingAddress;
import com.mycompany.storeapp.model.entity.CartItem;
import com.mycompany.storeapp.model.entity.ProductVariant;
import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.UserProfile;
import com.mycompany.storeapp.model.dao.ProductVariantDAO;
import com.mycompany.storeapp.model.entity.Product;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Controller for Order management with Kanban board functionality
 * @author Hi
 */
public class OrderController {
    private final OrderDAO orderDAO;
    private final ProductVariantDAO variantDAO;
    
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_SHIPPING = "shipping";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";
    
    private static final List<String> EDITABLE_STATUSES = Arrays.asList(STATUS_PENDING);
    
    private static final Map<String, List<String>> STATUS_TRANSITIONS = new HashMap<>();
    
    static {
        STATUS_TRANSITIONS.put(STATUS_PENDING, Arrays.asList(STATUS_CONFIRMED, STATUS_CANCELLED));
        STATUS_TRANSITIONS.put(STATUS_CONFIRMED, Arrays.asList(STATUS_PROCESSING, STATUS_CANCELLED));
        STATUS_TRANSITIONS.put(STATUS_PROCESSING, Arrays.asList(STATUS_SHIPPING, STATUS_CANCELLED));
        STATUS_TRANSITIONS.put(STATUS_SHIPPING, Arrays.asList(STATUS_DELIVERED));
        STATUS_TRANSITIONS.put(STATUS_DELIVERED, Arrays.asList());
        STATUS_TRANSITIONS.put(STATUS_CANCELLED, Arrays.asList());
    }

    public OrderController(DatabaseConnection connection) {
        this.orderDAO = new OrderDAO(connection);
        this.variantDAO = new ProductVariantDAO(connection);
    }
    
    public Map<String, List<Order>> getOrdersGroupedByStatus() {
        Map<String, List<Order>> groupedOrders = new HashMap<>();
        
        groupedOrders.put(STATUS_PENDING, orderDAO.getOrdersByStatus(STATUS_PENDING));
        groupedOrders.put(STATUS_CONFIRMED, orderDAO.getOrdersByStatus(STATUS_CONFIRMED));
        groupedOrders.put(STATUS_PROCESSING, orderDAO.getOrdersByStatus(STATUS_PROCESSING));
        groupedOrders.put(STATUS_SHIPPING, orderDAO.getOrdersByStatus(STATUS_SHIPPING));
        groupedOrders.put(STATUS_DELIVERED, orderDAO.getOrdersByStatus(STATUS_DELIVERED));
        groupedOrders.put(STATUS_CANCELLED, orderDAO.getOrdersByStatus(STATUS_CANCELLED));
        
        return groupedOrders;
    }
    
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
    
    public OrderTransitionResult changeOrderStatus(int orderId, String newStatus) {
        try {
            if (orderId <= 0 || newStatus == null || newStatus.trim().isEmpty()) {
                return new OrderTransitionResult(false, "ID đơn hàng hoặc trạng thái không hợp lệ");
            }
            
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                return new OrderTransitionResult(false, "Không tìm thấy đơn hàng");
            }
            
            String currentStatus = order.getOrderStatus().trim();
            
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                return new OrderTransitionResult(false, 
                    String.format("Không thể chuyển từ trạng thái '%s' sang '%s'", 
                    currentStatus, newStatus));
            }
            
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
    
    public boolean canEditOrder(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        return order != null && EDITABLE_STATUSES.contains(order.getOrderStatus());
    }
    
    public boolean canEditOrder(String orderStatus) {
        return orderStatus != null && EDITABLE_STATUSES.contains(orderStatus);
    }
    
    public List<String> getValidNextStatuses(String currentStatus) {
        return STATUS_TRANSITIONS.getOrDefault(currentStatus, Arrays.asList());
    }
    
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        if (currentStatus.equals(newStatus)) {
            return true;
        }
        List<String> validNextStatuses = STATUS_TRANSITIONS.get(currentStatus);
        return validNextStatuses != null && validNextStatuses.contains(newStatus);
    }
    
    public Order getOrderDetails(int orderId) {
        return orderDAO.getOrderById(orderId);
    }
    
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    public List<Order> getOrdersByStatus(String status) {
        return orderDAO.getOrdersByStatus(status);
    }
    
    public OrderTransitionResult cancelOrder(int orderId, String reason) {
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            return new OrderTransitionResult(false, "Không tìm thấy đơn hàng");
        }
        
        String currentStatus = order.getOrderStatus();
        if (!Arrays.asList(STATUS_PENDING, STATUS_CONFIRMED, STATUS_PROCESSING).contains(currentStatus)) {
            return new OrderTransitionResult(false, 
                "Không thể hủy đơn hàng ở trạng thái " + currentStatus);
        }
        
        return changeOrderStatus(orderId, STATUS_CANCELLED);
    }
    
    public OrderTransitionResult confirmOrder(int orderId) {
        return changeOrderStatus(orderId, STATUS_CONFIRMED);
    }
    
    public OrderTransitionResult startProcessing(int orderId) {
        return changeOrderStatus(orderId, STATUS_PROCESSING);
    }
    
    public OrderTransitionResult startShipping(int orderId) {
        return changeOrderStatus(orderId, STATUS_SHIPPING);
    }
    
    public OrderTransitionResult completeDelivery(int orderId) {
        return changeOrderStatus(orderId, STATUS_DELIVERED);
    }
    
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
     * Create a new order with details
     */
    public OrderTransitionResult createOrder(int userId, 
                                          ShippingAddress shippingAddress,
                                          List<CartItem> cartItems,
                                          double discountPercent,
                                          String paymentMethod,
                                          String deliveryMethod,
                                          String note,
                                          Integer processedByUserId) {
        try {
            if (userId <= 0) {
                return new OrderTransitionResult(false, "ID người dùng không hợp lệ");
            }
            if (cartItems == null || cartItems.isEmpty()) {
                return new OrderTransitionResult(false, "Giỏ hàng trống");
            }
            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                return new OrderTransitionResult(false, "Phương thức thanh toán không hợp lệ");
            }
            if (deliveryMethod == null || deliveryMethod.trim().isEmpty()) {
                return new OrderTransitionResult(false, "Phương thức giao hàng không hợp lệ");
            }
            if (!deliveryMethod.equals("Tại cửa hàng") && shippingAddress == null) {
                return new OrderTransitionResult(false, "Địa chỉ giao hàng là bắt buộc cho phương thức giao hàng đã chọn");
            }

            // Create Order object
            Order order = new Order();
            order.setUserId(userId);
            order.setShippingAddressId(shippingAddress != null ? shippingAddress.getAddressId() : 0);
            order.setOrderDate(LocalDateTime.now());
            
            // Calculate totals
            double subtotal = cartItems.stream()
                .mapToDouble(item -> {
                    ProductVariant variant = variantDAO.getVariantById(item.getVariantId());
                    return variant != null ? variant.getPrice().doubleValue() * item.getQuantity() : 0.0;
                }).sum();
            
            double shippingFee = getShippingFeeByMethod(deliveryMethod);
            double discountAmount = subtotal * (discountPercent / 100.0);
            double totalAmount = subtotal - discountAmount + shippingFee;
            
            order.setSubtotal(subtotal);
            order.setShippingFee(shippingFee);
            order.setDiscountAmount(discountAmount);
            order.setTotalAmount(totalAmount);
            order.setPaymentMethod(mapPaymentMethod(paymentMethod));
            order.setPaymentStatus("pending");
            order.setOrderStatus(STATUS_PENDING);
            order.setNote(note);
            order.setProcessedByUserId(processedByUserId);
            
            // Create OrderDetail objects
            List<OrderDetail> details = new ArrayList<>();
            for (CartItem item : cartItems) {
                ProductVariant variant = variantDAO.getVariantById(item.getVariantId());
                if (variant == null) {
                    return new OrderTransitionResult(false, "Sản phẩm không tồn tại: ID " + item.getVariantId());
                }
                OrderDetail detail = new OrderDetail();
                detail.setVariantId(item.getVariantId());
                detail.setQuantity(item.getQuantity());
                detail.setUnitPrice(variant.getPrice().doubleValue());
                detail.setSubtotal(variant.getPrice().doubleValue() * item.getQuantity());
                details.add(detail);
            }
            
            // Save order and details in a transaction
            boolean success = orderDAO.saveOrderWithDetails(order, details);
            if (success) {
                return new OrderTransitionResult(true, "Tạo đơn hàng thành công");
            } else {
                return new OrderTransitionResult(false, "Tạo đơn hàng thất bại");
            }
            
        } catch (Exception e) {
            return new OrderTransitionResult(false, "Lỗi hệ thống khi tạo đơn hàng: " + e.getMessage());
        }
    }
    
    private double getShippingFeeByMethod(String method) {
        switch (method) {
            case "Tại cửa hàng": return 0;
            case "Giao hàng tiêu chuẩn": return 25000;
            case "Giao hàng nhanh": return 40000;
            case "Giao hàng hỏa tốc": return 60000;
            default: return 0;
        }
    }
    
    private String mapPaymentMethod(String uiMethod) {
        switch (uiMethod) {
            case "Tiền mặt": return "cod";
            case "Thẻ tín dụng": return "credit_card";
            case "Chuyển khoản": return "bank_transfer";
            case "Ví điện tử": return "momo";
            default: return uiMethod.toLowerCase();
        }
    }
    
    public List<Order> getOrdersByMonth(int year, int month) {
        try {
            return orderDAO.getOrdersByMonth(year, month);
        } catch (Exception e) {
            showErrorMessage("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Map<Product, Integer> getTopSellingProducts(int limit, int year, int month) {
        Map<Product, Integer> productSales = new HashMap<>();
        List<Order> orders = getOrdersByMonth(year, month);

        for (Order order : orders) {
            for (OrderDetail detail : order.getDetails()) {
                if (detail.getVariant() != null && detail.getVariant().getProduct() != null) {
                    Product product = detail.getVariant().getProduct();
                    productSales.put(product, productSales.getOrDefault(product, 0) + detail.getQuantity());
                }
            }
        }

        return productSales.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
    }

    public Map<String, Integer> getMonthlySales(int year, int month) {
        Map<String, Integer> monthlySales = new HashMap<>();
        List<Order> orders = getOrdersByMonth(year, month);

        for (Order order : orders) {
            if (order.getOrderDate() != null) {
                String monthKey = "Tháng " + order.getOrderDate().getMonthValue();
                monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0) + order.getTotalQuantity());
            }
        }

        return monthlySales;
    }

    public int getTotalQuantitySold(int year, int month) {
        return getOrdersByMonth(year, month).stream()
                .mapToInt(Order::getTotalQuantity)
                .sum();
    }

    public double getTotalRevenue(int year, int month) {
        return getOrdersByMonth(year, month).stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Thông tin", JOptionPane.INFORMATION_MESSAGE);
    }

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
