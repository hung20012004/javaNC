/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
public class VariantImage {
    private Integer imageId;
    private Integer variantId;
    private String imageUrl;
    private Integer displayOrder;

    // Quan hệ với ProductVariant
    private ProductVariant variant;

    public VariantImage() {}

    public Integer getImageId() {
        return imageId;
    }
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getVariantId() {
        return variantId;
    }
    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public ProductVariant getVariant() {
        return variant;
    }
    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }
}

