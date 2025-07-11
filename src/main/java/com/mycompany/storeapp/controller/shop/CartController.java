package com.mycompany.storeapp.controller.shop;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.controller.admin.ProductVariantController;
import com.mycompany.storeapp.model.dao.CartDAO;
import com.mycompany.storeapp.model.entity.CartItem;
import com.mycompany.storeapp.model.entity.ProductVariant;

import java.util.List;

public class CartController {
    private final CartDAO cartDAO;
    private final ProductVariantController variantController;

    public CartController() {
        this.cartDAO = new CartDAO(new DatabaseConnection());
        this.variantController = new ProductVariantController();
    }

    public CartController(DatabaseConnection dbConnection) {
        this.cartDAO = new CartDAO(dbConnection);
        this.variantController = new ProductVariantController();
    }

    public boolean addItem(CartItem cartItem) {
        // Check stock availability
        ProductVariant variant = variantController.getVariantById(cartItem.getVariantId());
        if (variant == null || variant.getStockQuantity() < cartItem.getQuantity()) {
            return false; // Insufficient stock or invalid variant
        }

        // Reduce stock
        boolean stockUpdated = variantController.updateStock(cartItem.getVariantId(), -cartItem.getQuantity());
        if (!stockUpdated) {
            return false; // Stock update failed
        }

        // Add item to cart
        cartDAO.addItem(cartItem);
        return true;
    }

    public void removeItem(int cartItemId) {
        // Get the cart item to know the variant and quantity
        CartItem item = cartDAO.getItemById(cartItemId); // Assume CartDAO has this method
        if (item != null) {
            // Restore stock
            variantController.updateStock(item.getVariantId(), item.getQuantity());
        }
        cartDAO.removeItem(cartItemId);
    }

    public boolean updateQuantity(int cartItemId, int newQuantity) {
        CartItem item = cartDAO.getItemById(cartItemId); // Assume CartDAO has this method
        if (item != null) {
            int quantityChange = newQuantity - item.getQuantity(); // Positive if increasing, negative if decreasing
            if (quantityChange > 0) {
                // Check if enough stock for increase
                ProductVariant variant = variantController.getVariantById(item.getVariantId());
                if (variant == null || variant.getStockQuantity() < quantityChange) {
                    return false; // Insufficient stock
                }
            }
            // Update stock
            boolean stockUpdated = variantController.updateStock(item.getVariantId(), -quantityChange);
            if (!stockUpdated) {
                return false; // Stock update failed
            }
            cartDAO.updateQuantity(cartItemId, newQuantity);
            return true;
        }
        return false;
    }

    public void clearCart(int cartId) {
        List<CartItem> items = cartDAO.getAllItems(cartId);
        for (CartItem item : items) {
            // Restore stock for each item
            variantController.updateStock(item.getVariantId(), item.getQuantity());
        }
        cartDAO.clearCart(cartId);
    }

    public List<CartItem> getAllItems(int cartId) {
        return cartDAO.getAllItems(cartId);
    }
}