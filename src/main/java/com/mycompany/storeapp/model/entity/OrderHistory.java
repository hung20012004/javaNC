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

public class OrderHistory {
    private int historyId;
    private int orderId;
    private String status;
    private String note;
    private Integer processedByUserId;
    private Integer shippedByUserId;
    private Integer preparedByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Quan hệ đối tượng
    private Order order;
    private User processedBy;
    private User shippedBy;
    private User preparedBy;

    public OrderHistory() {}

    // Getters & Setters

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public Integer getProcessedByUserId() {
        return processedByUserId;
    }

    public void setProcessedByUserId(Integer processedByUserId) {
        this.processedByUserId = processedByUserId;
    }

    public Integer getShippedByUserId() {
        return shippedByUserId;
    }

    public void setShippedByUserId(Integer shippedByUserId) {
        this.shippedByUserId = shippedByUserId;
    }

    public Integer getPreparedByUserId() {
        return preparedByUserId;
    }

    public void setPreparedByUserId(Integer preparedByUserId) {
        this.preparedByUserId = preparedByUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    public User getShippedBy() {
        return shippedBy;
    }

    public void setShippedBy(User shippedBy) {
        this.shippedBy = shippedBy;
    }

    public User getPreparedBy() {
        return preparedBy;
    }

    public void setPreparedBy(User preparedBy) {
        this.preparedBy = preparedBy;
    }
}

