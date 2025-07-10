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
import java.util.Date;

public class PurchaseOrderDetail {
   private int poId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double subTotal;
    private Date createdAt;
    private Date updatedAt;
    
    private String productName;
    private String sizeName;
    private String colorName;

    public PurchaseOrderDetail(){};

    public PurchaseOrderDetail(int poId, int productId, int quantity, double unitPrice, double subTotal, 
                              Date createdAt, Date updatedAt) {
        this.poId = poId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subTotal = subTotal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getPoId() { return poId; }
    public void setPoId(int poId) { this.poId = poId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getSubTotal() { return subTotal; }
    public void setSubTotal(double subTotal) { this.subTotal = subTotal; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductName() { return productName; }
    public void setSizeName(String sizeName) { this.sizeName = sizeName; }
    public String getSizeName() { return sizeName; }
    public void setColorName(String colorName) { this.colorName = colorName; }
    public String getColorName() { return colorName; }
}
