package com.revshop.dao;

import com.revshop.model.CartItem;
import com.revshop.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(CartDAO.class);

    // Add item to cart
    public boolean addToCart(CartItem cartItem) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // Check if item already exists
        CartItem existing = getCartItem(cartItem.getBuyerId(), cartItem.getProductId());

        if (existing != null) {
            // Update quantity
            return updateCartItemQuantity(cartItem.getBuyerId(), cartItem.getProductId(),
                    existing.getQuantity() + cartItem.getQuantity());
        } else {
            // Insert new item
            String sql = "INSERT INTO cart (buyer_id, product_id, quantity) VALUES (?, ?, ?)";

            try {
                conn = getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cartItem.getBuyerId());
                pstmt.setInt(2, cartItem.getProductId());
                pstmt.setInt(3, cartItem.getQuantity());

                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            } catch (SQLException e) {
                logger.error("Error adding to cart: buyer={}, product={}",
                        cartItem.getBuyerId(), cartItem.getProductId(), e);
            } finally {
                closeResources(pstmt, conn);
            }
        }
        return false;
    }

    // Get cart item
    public CartItem getCartItem(int buyerId, int productId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM cart WHERE buyer_id = ? AND product_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, buyerId);
            pstmt.setInt(2, productId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractCartItemFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting cart item: buyer={}, product={}", buyerId, productId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Get cart items for buyer
    public List<CartItem> getCartItems(int buyerId) {
        List<CartItem> cartItems = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT c.*, p.* FROM cart c " +
                "JOIN products p ON c.product_id = p.product_id " +
                "WHERE c.buyer_id = ? AND p.is_active = TRUE " +
                "ORDER BY c.added_at DESC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, buyerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                CartItem cartItem = extractCartItemFromResultSet(rs);
                Product product = extractProductFromResultSet(rs);
                cartItem.setProduct(product);
                cartItems.add(cartItem);
            }
        } catch (SQLException e) {
            logger.error("Error getting cart items for buyer: {}", buyerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return cartItems;
    }

    // Update cart item quantity
    public boolean updateCartItemQuantity(int buyerId, int productId, int quantity) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (quantity <= 0) {
            return removeFromCart(buyerId, productId);
        }

        String sql = "UPDATE cart SET quantity = ? WHERE buyer_id = ? AND product_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, buyerId);
            pstmt.setInt(3, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating cart item quantity: buyer={}, product={}",
                    buyerId, productId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Remove item from cart
    public boolean removeFromCart(int buyerId, int productId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "DELETE FROM cart WHERE buyer_id = ? AND product_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, buyerId);
            pstmt.setInt(2, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error removing from cart: buyer={}, product={}", buyerId, productId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Clear cart
    public boolean clearCart(int buyerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "DELETE FROM cart WHERE buyer_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, buyerId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error clearing cart for buyer: {}", buyerId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Get cart total
    public double getCartTotal(int buyerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT SUM(p.price * c.quantity) as total FROM cart c " +
                "JOIN products p ON c.product_id = p.product_id " +
                "WHERE c.buyer_id = ? AND p.is_active = TRUE";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, buyerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            logger.error("Error getting cart total for buyer: {}", buyerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return 0.0;
    }

    // Helper method to extract cart item from ResultSet
    private CartItem extractCartItemFromResultSet(ResultSet rs) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setCartId(rs.getInt("cart_id"));
        cartItem.setBuyerId(rs.getInt("buyer_id"));
        cartItem.setProductId(rs.getInt("product_id"));
        cartItem.setQuantity(rs.getInt("quantity"));
        cartItem.setAddedAt(rs.getTimestamp("added_at"));
        return cartItem;
    }

    // Helper method to extract product from ResultSet
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setSellerId(rs.getInt("seller_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategory(rs.getString("category"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setMrp(rs.getBigDecimal("mrp"));
        product.setDiscountPrice(rs.getBigDecimal("discount_price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setThresholdQuantity(rs.getInt("threshold_quantity"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setActive(rs.getBoolean("is_active"));
        return product;
    }
}