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
public class CartItem {
    private long cartItemId;
    private long cartId;
    private long variantId;
    private int quantity;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public CartItem() {}

    public CartItem(long cartItemId, long cartId, long variantId, int quantity, Timestamp createdAt, Timestamp updatedAt) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getCartItemId() { return cartItemId; }
    public void setCartItemId(long cartItemId) { this.cartItemId = cartItemId; }
    public long getCartId() { return cartId; }
    public void setCartId(long cartId) { this.cartId = cartId; }
    public long getVariantId() { return variantId; }
    public void setVariantId(long variantId) { this.variantId = variantId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
