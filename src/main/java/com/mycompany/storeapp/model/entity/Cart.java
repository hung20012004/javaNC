package com.mycompany.storeapp.model.entity;

import com.mycompany.storeapp.model.entity.CartItem;
import java.util.List;

public class Cart {
    private int cartId;
    private List<CartItem> items;

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}