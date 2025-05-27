/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrder {
    private int poId;
    private int supplierId;
    private int createByUserId;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDate;
    private BigDecimal totalAmount;
    private String status; // pending, processing, completed, cancelled
    private String note;

    private Supplier supplier;
    private User createdByUser;
    private List<PurchaseOrderDetail> details;

    public PurchaseOrder() {}

    // Getters & Setters
    public int getPoId() {
        return poId;
    }
    public void setPoId(int poId) {
        this.poId = poId;
    }

    public int getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getCreateByUserId() {
        return createByUserId;
    }
    public void setCreateByUserId(int createByUserId) {
        this.createByUserId = createByUserId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getExpectedDate() {
        return expectedDate;
    }
    public void setExpectedDate(LocalDateTime expectedDate) {
        this.expectedDate = expectedDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public Supplier getSupplier() {
        return supplier;
    }
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }
    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public List<PurchaseOrderDetail> getDetails() {
        return details;
    }
    public void setDetails(List<PurchaseOrderDetail> details) {
        this.details = details;
    }

    // Helper method to calculate total quantity
    public int getTotalQuantity() {
        if (details == null) return 0;
        return details.stream().mapToInt(PurchaseOrderDetail::getQuantity).sum();
    }

    // Helper method to recalculate total amount
    public BigDecimal recalculateTotalAmount() {
        if (details == null) return BigDecimal.ZERO;

        BigDecimal total = details.stream()
            .map(PurchaseOrderDetail::getSubtotal)
            .filter(sb -> sb != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        setTotalAmount(total);
        return total;
    }

    // Status helpers
    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }

    public boolean isProcessing() {
        return "processing".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(status);
    }

    // Status update methods
    public void markAsProcessing() {
        this.status = "processing";
        // gọi save/update bên ngoài nếu dùng ORM
    }

    public void markAsCompleted() {
        this.status = "completed";
        // gọi save/update bên ngoài nếu dùng ORM
    }

    public void markAsCancelled() {
        this.status = "cancelled";
        // gọi save/update bên ngoài nếu dùng ORM
    }
}

