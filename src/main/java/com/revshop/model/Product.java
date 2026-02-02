package com.revshop.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private int productId;
    private int sellerId;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private BigDecimal mrp;
    private BigDecimal discountPrice;
    private int stockQuantity;
    private int thresholdQuantity;
    private Timestamp createdAt;
    private boolean isActive;

    // Constructors
    public Product() {}

    public Product(int sellerId, String name, String description, String category,
                   BigDecimal price, BigDecimal mrp, int stockQuantity) {
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.mrp = mrp;
        this.stockQuantity = stockQuantity;
        this.thresholdQuantity = 5;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getMrp() { return mrp; }
    public void setMrp(BigDecimal mrp) { this.mrp = mrp; }

    public BigDecimal getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(BigDecimal discountPrice) { this.discountPrice = discountPrice; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public int getThresholdQuantity() { return thresholdQuantity; }
    public void setThresholdQuantity(int thresholdQuantity) { this.thresholdQuantity = thresholdQuantity; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public BigDecimal getFinalPrice() {
        return discountPrice != null ? discountPrice : price;
    }

    public boolean isLowStock() {
        return stockQuantity <= thresholdQuantity;
    }

    @Override
    public String toString() {
        return String.format("Product ID: %d | Name: %-20s | Category: %-15s | Price: $%.2f | Stock: %d",
                productId, name, category, getFinalPrice(), stockQuantity);
    }
}