/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
public class InventoryCheckDetail {
    private int checkId;
    private int productId;
    private int systemQuantity;
    private int actualQuantity;
    private int difference;
    private String note;

    // Quan hệ với InventoryCheck (tùy ý nếu cần)
    private InventoryCheck inventoryCheck;

    // Quan hệ với Product (tùy ý nếu cần)
    private Product product;

    public InventoryCheckDetail() {}

    // Getters và Setters

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSystemQuantity() {
        return systemQuantity;
    }

    public void setSystemQuantity(int systemQuantity) {
        this.systemQuantity = systemQuantity;
        // Cập nhật lại chênh lệch nếu đã có actualQuantity
        updateDifference();
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity = actualQuantity;
        updateDifference();
    }

    public int getDifference() {
        return difference;
    }

    private void updateDifference() {
        this.difference = this.actualQuantity - this.systemQuantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public InventoryCheck getInventoryCheck() {
        return inventoryCheck;
    }

    public void setInventoryCheck(InventoryCheck inventoryCheck) {
        this.inventoryCheck = inventoryCheck;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // Helper method: kiểm tra có chênh lệch không
    public boolean hasDiscrepancy() {
        return this.difference != 0;
    }

    // Helper method: phần trăm chênh lệch
    public double getDiscrepancyPercentage() {
        if (systemQuantity == 0) {
            return actualQuantity > 0 ? 100.0 : 0.0;
        }
        return Math.round(((double) difference / systemQuantity) * 10000.0) / 100.0;
    }
}

