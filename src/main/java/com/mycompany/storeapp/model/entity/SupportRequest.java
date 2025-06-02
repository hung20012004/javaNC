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

public class SupportRequest {
    private Integer id;  // giả sử có id tự sinh
    private Integer userId;
    private Integer orderId;
    private String issueType;
    private String description;
    private String status;
    private LocalDateTime resolvedAt;
    private Integer assignedTo;
    private String adminNotes;

    // Các hằng số trạng thái
    public static final String STATUS_NEW = "new";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_RESOLVED = "resolved";
    public static final String STATUS_CLOSED = "closed";

    // Các hằng số loại vấn đề
    public static final String ISSUE_SHIPPING = "shipping";
    public static final String ISSUE_PRODUCT = "product";
    public static final String ISSUE_PAYMENT = "payment";
    public static final String ISSUE_OTHER = "other";

    public SupportRequest() {
        // Mặc định trạng thái mới
        this.status = STATUS_NEW;
    }

    // Getters và Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public Integer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    // Các phương thức tiện ích tương đương scope và accessor Laravel

    public boolean hasStatus(String status) {
        return this.status != null && this.status.equals(status);
    }

    public boolean hasIssueType(String issueType) {
        return this.issueType != null && this.issueType.equals(issueType);
    }

    // Label cho status
    public String getStatusLabel() {
        switch (this.status) {
            case STATUS_NEW: return "Mới";
            case STATUS_IN_PROGRESS: return "Đang xử lý";
            case STATUS_RESOLVED: return "Đã giải quyết";
            case STATUS_CLOSED: return "Đã đóng";
            default: return "Không xác định";
        }
    }

    // Label cho issue type
    public String getIssueTypeLabel() {
        switch (this.issueType) {
            case ISSUE_SHIPPING: return "Vấn đề vận chuyển";
            case ISSUE_PRODUCT: return "Vấn đề sản phẩm";
            case ISSUE_PAYMENT: return "Vấn đề thanh toán";
            case ISSUE_OTHER: return "Khác";
            default: return "Không xác định";
        }
    }
}

