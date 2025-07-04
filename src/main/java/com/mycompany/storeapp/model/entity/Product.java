package com.mycompany.storeapp.model.entity;

import java.sql.Timestamp;

public class Product {
    private long productId;
    private Integer categoryId;
    private long materialId;
    private String brand;
    private String name;
    private String gender;
    private String careInstruction;
    private String slug;
    private String description;
    private double price;
    private double salePrice;
    private int stockQuantity;
    private String sku;
    private int minPurchaseQuantity;
    private int maxPurchaseQuantity;
    private boolean isActive;

    public Product() {}

    public Product(long productId, Integer categoryId, long materialId, String brand, String name, String gender, String careInstruction, String slug, String description, double price, double salePrice, int stockQuantity, String sku, int minPurchaseQuantity, int maxPurchaseQuantity, boolean isActive) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.materialId = materialId;
        this.brand = brand;
        this.name = name;
        this.gender = gender;
        this.careInstruction = careInstruction;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.salePrice = salePrice;
        this.stockQuantity = stockQuantity;
        this.sku = sku;
        this.minPurchaseQuantity = minPurchaseQuantity;
        this.maxPurchaseQuantity = maxPurchaseQuantity;
        this.isActive = isActive;
    }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
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
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }
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
}