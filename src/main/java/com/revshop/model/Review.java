package com.revshop.model;

import java.sql.Timestamp;

public class Review {
    private int reviewId;
    private int productId;
    private int buyerId;
    private int orderId;
    private int rating;
    private String comment;
    private Timestamp reviewDate;
    private User buyer;

    // Constructors
    public Review() {}

    public Review(int productId, int buyerId, int orderId, int rating, String comment) {
        this.productId = productId;
        this.buyerId = buyerId;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getReviewDate() { return reviewDate; }
    public void setReviewDate(Timestamp reviewDate) { this.reviewDate = reviewDate; }

    public User getBuyer() { return buyer; }
    public void setBuyer(User buyer) { this.buyer = buyer; }

    @Override
    public String toString() {
        String buyerName = (buyer != null) ?
                buyer.getFirstName() + " " + buyer.getLastName() : "Anonymous";
        return String.format("Rating: %d/5 | By: %s | %s",
                rating, buyerName, comment);
    }
}