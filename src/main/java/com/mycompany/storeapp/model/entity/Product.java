/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;
import java.sql.Timestamp;
/**
 *
 * @author Hi
 */
public class Product {
    private long productId;
    private long categoryId;
    private long materialId;
    private String brand;
    private String name;
    private String gender;
    private String careInstruction;
    private String slug;
    private String description;
    private float price;
    private float salePrice;
    private int stockQuantity;
    private String sku;
    private int minPurchaseQuantity;
    private int maxPurchaseQuantity;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Product() {}

    public Product(long productId, String name, String description, long categoryId, long materialId, boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.materialId = materialId;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public long getMaterialId() { return materialId; }
    public void setMaterialId(long materialId) { this.materialId = materialId; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCareInstruction() { return careInstruction; }
    public void setCareInstruction(String careInstruction) { this.careInstruction = careInstruction; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    public float getSalePrice() { return salePrice; }
    public void setSalePrice(float salePrice) { this.salePrice = salePrice; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getMinPurchaseQuantity() { return minPurchaseQuantity; }
    public void setMinPurchaseQuantity(int minPurchaseQuantity) { this.minPurchaseQuantity = minPurchaseQuantity; }
    public int getMaxPurchaseQuantity() { return maxPurchaseQuantity; }
    public void setMaxPurchaseQuantity(int maxPurchaseQuantity) { this.maxPurchaseQuantity = maxPurchaseQuantity; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
