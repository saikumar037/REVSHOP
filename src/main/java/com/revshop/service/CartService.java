package com.revshop.service;

import com.revshop.dao.CartDAO;
import com.revshop.model.CartItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class CartService {
    private static final Logger logger = LogManager.getLogger(CartService.class);
    private CartDAO cartDAO;

    public CartService() {
        this.cartDAO = new CartDAO();
    }

    public boolean addToCart(CartItem cartItem) {
        return cartDAO.addToCart(cartItem);
    }

    public List<CartItem> getCartItems(int buyerId) {
        return cartDAO.getCartItems(buyerId);
    }

    public boolean updateCartItemQuantity(int buyerId, int productId, int quantity) {
        return cartDAO.updateCartItemQuantity(buyerId, productId, quantity);
    }

    public boolean removeFromCart(int buyerId, int productId) {
        return cartDAO.removeFromCart(buyerId, productId);
    }

    public boolean clearCart(int buyerId) {
        return cartDAO.clearCart(buyerId);
    }

    public double getCartTotal(int buyerId) {
        return cartDAO.getCartTotal(buyerId);
    }
}