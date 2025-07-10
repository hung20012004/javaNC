/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;
/**
 *
 * @author Hi
 */
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private int shippingAddressId;
    private Integer promotionId; // Có thể null
    private LocalDateTime orderDate;
    private double subtotal;
    private double shippingFee;
    private double discountAmount;
    private double totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String note;

    // Quan hệ
    private UserProfile user;
    private ShippingAddress shippingAddress;
    private Promotion promotion;
    private List<OrderDetail> details = new ArrayList<>();
    private List<OrderHistory> history = new ArrayList<>();
    private Payment payment;
    private Integer processedByUserId;
    private Integer shippedByUserId;

    public Integer getProcessedByUserId() { return processedByUserId; }
    public void setProcessedByUserId(Integer processedByUserId) { this.processedByUserId = processedByUserId; }
    public Integer getShippedByUserId() { return shippedByUserId; }
    public void setShippedByUserId(Integer shippedByUserId) { this.shippedByUserId = shippedByUserId; }
    public Order() {}

    // ========== GETTERS & SETTERS GỐC (GIỮ NGUYÊN) ==========

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(int shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public Integer getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public List<OrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetail> details) {
        this.details = details;
    }

    public List<OrderHistory> getHistory() {
        return history;
    }

    public void setHistory(List<OrderHistory> history) {
        this.history = history;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    // ========== HELPER METHODS GỐC (GIỮ NGUYÊN) ==========
    
    public boolean isCod() {
        return "cod".equalsIgnoreCase(paymentMethod);
    }

    public boolean isVnpay() {
        return "vnpay".equalsIgnoreCase(paymentMethod);
    }

    // ========== HELPER METHODS MỚI CHO KANBANORDERVIEW ==========

    /**
     * Lấy tên khách hàng cho KanbanOrderView
     * Ưu tiên từ ShippingAddress.recipientName, fallback to User info
     */
    public String getCustomerName() {
        // Ưu tiên tên người nhận từ shipping address
        if (shippingAddress != null && shippingAddress.getRecipientName() != null && 
            !shippingAddress.getRecipientName().trim().isEmpty()) {
            return shippingAddress.getRecipientName();
        }
        
        // Nếu User có UserProfile với fullName
        if (user != null  && 
            user.getFullName() != null && 
            !user.getFullName().trim().isEmpty()) {
            return user.getFullName();
        }
        
        // Fallback to email
        if (user != null && user.getUser().getEmail() != null && 
            !user.getUser().getEmail().trim().isEmpty()) {
            return user.getUser().getEmail();
        }
        
        return "N/A";
    }

    /**
     * Setter cho customer name - cập nhật vào ShippingAddress
     */
    public void setCustomerName(String customerName) {
        if (shippingAddress != null && customerName != null) {
            shippingAddress.setRecipientName(customerName);
        }
    }

    /**
     * Lấy số điện thoại khách hàng cho KanbanOrderView
     * Ưu tiên từ ShippingAddress.phone, fallback to UserProfile.phone
     */
    public String getCustomerPhone() {
        // Ưu tiên phone từ shipping address
        if (shippingAddress != null && shippingAddress.getPhone() != null && 
            !shippingAddress.getPhone().trim().isEmpty()) {
            return shippingAddress.getPhone();
        }
        
        // Fallback to user profile phone
        if (user != null && 
            user.getPhone() != null && 
            !user.getPhone().trim().isEmpty()) {
            return user.getPhone();
        }
        
        return "N/A";
    }

    /**
     * Setter cho customer phone - cập nhật vào ShippingAddress
     */
    public void setCustomerPhone(String customerPhone) {
        if (shippingAddress != null && customerPhone != null) {
            shippingAddress.setPhone(customerPhone);
        }
    }

    /**
     * Lấy địa chỉ giao hàng đầy đủ cho KanbanOrderView
     */
    public String getDeliveryAddress() {
        if (shippingAddress == null) {
            return "N/A";
        }
        
        StringBuilder address = new StringBuilder();
        
        if (shippingAddress.getStreetAddress() != null && 
            !shippingAddress.getStreetAddress().trim().isEmpty()) {
            address.append(shippingAddress.getStreetAddress());
        }
        
        if (shippingAddress.getWard() != null && 
            !shippingAddress.getWard().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(shippingAddress.getWard());
        }
        
        if (shippingAddress.getDistrict() != null && 
            !shippingAddress.getDistrict().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(shippingAddress.getDistrict());
        }
        
        if (shippingAddress.getProvince() != null && 
            !shippingAddress.getProvince().trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(shippingAddress.getProvince());
        }
        
        return address.length() > 0 ? address.toString() : "N/A";
    }

    /**
     * Setter cho delivery address - cập nhật streetAddress
     */
    public void setDeliveryAddress(String deliveryAddress) {
        if (shippingAddress != null && deliveryAddress != null) {
            shippingAddress.setStreetAddress(deliveryAddress);
        }
    }

    /**
     * Alias cho getNote() để tương thích với KanbanOrderView
     */
    public String getNotes() {
        return note;
    }

    /**
     * Alias cho setNote() để tương thích với KanbanOrderView
     */
    public void setNotes(String notes) {
        this.note = notes;
    }

    /**
     * Alias cho getOrderStatus() để tương thích với KanbanOrderView
     */
    public String getStatus() {
        return orderStatus;
    }

    /**
     * Alias cho setOrderStatus() để tương thích với KanbanOrderView
     */
    public void setStatus(String status) {
        this.orderStatus = status;
    }

    /**
     * Lấy tên hiển thị của trạng thái tiếng Việt
     */
    public String getStatusDisplayName() {
        if (orderStatus == null) return "Không xác định";
        
        switch (orderStatus.toUpperCase()) {
            case "pending":
                return "Chờ xử lý";
            case "confirmed":
                return "Đã xác nhận";
            case "processing":
                return "Đang xử lý";
            case "shipping":
                return "Đang giao";
            case "delivered":
                return "Đã giao";
            case "cancelled":
                return "Đã hủy";
            case "returned":
                return "Đã trả";
            default:
                return orderStatus;
        }
    }

    /**
     * Lấy màu sắc cho trạng thái (dùng cho UI)
     */
    public String getStatusColor() {
        if (orderStatus == null) return "#666666";
        
        switch (orderStatus) {
            case "pending":
                return "#FF9800"; // Orange
            case "confirmed":
                return "#2196F3"; // Blue
            case "processing":
                return "#FF5722"; // Deep Orange
            case "shipping":
                return "#9C27B0"; // Purple
            case "delivered":
                return "#4CAF50"; // Green
            case "cancelled":
                return "#F44336"; // Red
            case "returned":
                return "#795548"; // Brown
            default:
                return "#666666"; // Gray
        }
    }

    /**
     * Tính tổng số lượng sản phẩm trong đơn hàng
     */
    public int getTotalQuantity() {
        if (details == null || details.isEmpty()) {
            return 0;
        }
        
        return details.stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }

    /**
     * Kiểm tra đơn hàng đã thanh toán chưa
     */
    public boolean isPaid() {
        return "paid".equalsIgnoreCase(paymentStatus) || 
               "completed".equalsIgnoreCase(paymentStatus);
    }

    /**
     * Lấy tên hiển thị trạng thái thanh toán
     */
    public String getPaymentStatusDisplayName() {
        if (paymentStatus == null) return "Không xác định";
        
        switch (paymentStatus.toUpperCase()) {
            case "pending":
                return "Chờ thanh toán";
            case "paid":
            case "completed":
                return "Đã thanh toán";
            case "failed":
                return "Thanh toán thất bại";
            case "cancelled":
                return "Đã hủy";
            case "refunded":
                return "Đã hoàn tiền";
            default:
                return paymentStatus;
        }
    }

    /**
     * Lấy tên hiển thị phương thức thanh toán
     */
    public String getPaymentMethodDisplayName() {
        if (paymentMethod == null) return "Không xác định";
        
        switch (paymentMethod.toLowerCase()) {
            case "cod":
                return "Thanh toán khi nhận hàng (COD)";
            case "vnpay":
                return "VNPay";
            case "momo":
                return "MoMo";
            case "zalopay":
                return "ZaloPay";
            case "bank_transfer":
                return "Chuyển khoản ngân hàng";
            case "credit_card":
                return "Thẻ tín dụng";
            default:
                return paymentMethod;
        }
    }
}