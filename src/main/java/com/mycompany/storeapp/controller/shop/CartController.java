package com.mycompany.storeapp.controller.shop;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.dao.CartDAO;
import com.mycompany.storeapp.model.entity.CartItem;
import java.util.List;

public class CartController {
    private final CartDAO cartDAO;

    public CartController() {
        this.cartDAO = new CartDAO(new DatabaseConnection());
    }

    public CartController(DatabaseConnection dbConnection) {
        this.cartDAO = new CartDAO(dbConnection);
    }

    public void addItem(CartItem cartItem) {
        cartDAO.addItem(cartItem);
    }

    public void removeItem(int cartItemId) {
        cartDAO.removeItem(cartItemId);
    }

    public void updateQuantity(int cartItemId, int quantity) {
        cartDAO.updateQuantity(cartItemId, quantity);
    }

    public void clearCart(int cartId) {
        cartDAO.clearCart(cartId);
    }

    public List<CartItem> getAllItems(int cartId) {
        return cartDAO.getAllItems();
    }
}