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

public class PurchaseOrderDetail {
    private int poId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    private PurchaseOrder purchaseOrder;
    private Product product;

    public PurchaseOrderDetail() {}

    // Getters & Setters
    public int getPoId() {
        return poId;
    }
    public void setPoId(int poId) {
        this.poId = poId;
    }

    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateSubtotal();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
    private void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    // Tính lại subtotal tự động khi quantity hoặc unitPrice thay đổi
    private void recalculateSubtotal() {
        if (unitPrice != null) {
            BigDecimal qty = new BigDecimal(quantity);
            setSubtotal(unitPrice.multiply(qty));
        } else {
            setSubtotal(BigDecimal.ZERO);
        }
    }

    // Gọi recalculate tổng tiền PO sau khi lưu thay đổi
    public void onSaved() {
        if (purchaseOrder != null) {
            purchaseOrder.recalculateTotalAmount();
        }
    }
}
