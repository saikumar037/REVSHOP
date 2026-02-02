package com.revshop.model;

import java.sql.Timestamp;

public class CartItem {
    private int cartId;
    private int buyerId;
    private int productId;
    private int quantity;
    private Timestamp addedAt;
    private Product product;

    // Constructors
    public CartItem() {}

    public CartItem(int buyerId, int productId, int quantity) {
        this.buyerId = buyerId;
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public double getTotalPrice() {
        if (product != null) {
            return product.getFinalPrice().doubleValue() * quantity;
        }
        return 0;
    }

    @Override
    public String toString() {
        if (product != null) {
            return String.format("%-20s x %d = $%.2f",
                    product.getName(), quantity, getTotalPrice());
        }
        return String.format("Product ID: %d x %d", productId, quantity);
    }
}