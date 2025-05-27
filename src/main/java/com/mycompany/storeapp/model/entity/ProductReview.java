/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;
/**
 *
 * @author Hi
 */
public class ProductReview {
    private int reviewId;
    private int productId;
    private int userId;
    private Integer variantId; // có thể null nếu không review biến thể
    private int rating;
    private String comment;

    // Quan hệ đối tượng
    private Product product;
    private User user;
    private ProductVariant variant;

    public ProductReview() {}

    // Getters & Setters
    public int getReviewId() {
        return reviewId;
    }
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getVariantId() {
        return variantId;
    }
    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public ProductVariant getVariant() {
        return variant;
    }
    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }
}
