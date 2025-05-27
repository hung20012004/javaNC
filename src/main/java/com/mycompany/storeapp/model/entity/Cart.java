/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.model.entity;

/**
 *
 * @author Hi
 */
public class Cart {
    private long cartId;
    private long userId;

    public Cart() {}

    public Cart(long cartId, long userId) {
        this.cartId = cartId;
        this.userId = userId;
    }

    public long getCartId() { return cartId; }
    public void setCartId(long cartId) { this.cartId = cartId; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
}
