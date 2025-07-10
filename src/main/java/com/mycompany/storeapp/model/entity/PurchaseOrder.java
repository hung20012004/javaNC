/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
import java.util.Date;
import java.util.List;

public class PurchaseOrder {
 private int poId;
    private int supplierId;
    private int createdByUserId;
    private Date orderDate;
    private Date expectedDate;
    private double totalAmount;
    private String status;
    private String note;
    private Date createdAt;
    private Date updatedAt;
    private List<PurchaseOrderDetail> details;
    
    public PurchaseOrder (){};

    public PurchaseOrder(int poId, int supplierId, int createdByUserId, Date orderDate, Date expectedDate, 
                         double totalAmount, String status, String note, Date createdAt, Date updatedAt) {
        this.poId = poId;
        this.supplierId = supplierId;
        this.createdByUserId = createdByUserId;
        this.orderDate = orderDate;
        this.expectedDate = expectedDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getPoId() { return poId; }
    public void setPoId(int poId) { this.poId = poId; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public int getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(int createdByUserId) { this.createdByUserId = createdByUserId; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public Date getExpectedDate() { return expectedDate; }
    public void setExpectedDate(Date expectedDate) { this.expectedDate = expectedDate; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    public List<PurchaseOrderDetail> getDetails() { return details; }
    public void setDetails(List<PurchaseOrderDetail> details) { this.details = details; }
}
