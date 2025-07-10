package com.mycompany.storeapp.model.dao;

import com.mycompany.storeapp.config.DatabaseConnection;
import com.mycompany.storeapp.model.entity.CartItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private final DatabaseConnection dbConnection;
    private final Connection conn;

    public CartDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConnection();
    }

    public void addItem(CartItem cartItem) {
        String sql = "INSERT INTO cart_items (cart_id, variant_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cartItem.getCartId());
            stmt.setInt(2, cartItem.getVariantId());
            stmt.setInt(3, cartItem.getQuantity());
            stmt.executeUpdate();

            // Get the generated cart_item_id
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cartItem.setCartItemId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
        }
    }

    public List<CartItem> getAllItems() {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT * FROM cart_items WHERE cart_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, 1); // Giả sử cart_id = 1, cần thay bằng logic lấy cart_id hiện tại
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem(rs.getInt("cart_id"), rs.getInt("variant_id"), rs.getInt("quantity"));
                    item.setCartItemId(rs.getInt("cart_item_id"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting cart items: " + e.getMessage());
        }
        return items;
    }

    public void removeItem(int cartItemId) {
        String sql = "DELETE FROM cart_items WHERE cart_item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing item from cart: " + e.getMessage());
        }
    }

    public void updateQuantity(int cartItemId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, cartItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating cart quantity: " + e.getMessage());
        }
    }

    public void clearCart(int cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
        }
    }
}