package com.revshop.dao;

import com.revshop.model.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(ReviewDAO.class);

    // Create review
    public boolean createReview(Review review) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "INSERT INTO reviews (product_id, buyer_id, order_id, rating, comment) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, review.getProductId());
            pstmt.setInt(2, review.getBuyerId());
            pstmt.setInt(3, review.getOrderId());
            pstmt.setInt(4, review.getRating());
            pstmt.setString(5, review.getComment());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    review.setReviewId(rs.getInt(1));
                    logger.info("Review created with ID: {}", review.getReviewId());
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating review", e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return false;
    }

    // Get reviews by product
    public List<Review> getReviewsByProduct(int productId) {
        List<Review> reviews = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT r.*, u.first_name, u.last_name FROM reviews r " +
                "JOIN users u ON r.buyer_id = u.user_id " +
                "WHERE r.product_id = ? ORDER BY r.review_date DESC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = extractReviewFromResultSet(rs);

                // Set buyer info
                com.revshop.model.User buyer = new com.revshop.model.User();
                buyer.setFirstName(rs.getString("first_name"));
                buyer.setLastName(rs.getString("last_name"));
                review.setBuyer(buyer);

                reviews.add(review);
            }
        } catch (SQLException e) {
            logger.error("Error getting reviews for product: {}", productId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return reviews;
    }

    // Get reviews by buyer
    public List<Review> getReviewsByBuyer(int buyerId) {
        List<Review> reviews = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT r.*, p.name as product_name FROM reviews r " +
                "JOIN products p ON r.product_id = p.product_id " +
                "WHERE r.buyer_id = ? ORDER BY r.review_date DESC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, buyerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                reviews.add(extractReviewFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting reviews for buyer: {}", buyerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return reviews;
    }

    // Get average rating for product
    public double getAverageRating(int productId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT AVG(rating) as avg_rating FROM reviews WHERE product_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, productId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            logger.error("Error getting average rating for product: {}", productId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return 0.0;
    }

    // Check if buyer has reviewed product
    public boolean hasReviewed(int buyerId, int productId, int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT COUNT(*) as count FROM reviews WHERE buyer_id = ? " +
                "AND product_id = ? AND order_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, buyerId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            logger.error("Error checking review existence", e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return false;
    }

    // Update review
    public boolean updateReview(Review review) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, review.getRating());
            pstmt.setString(2, review.getComment());
            pstmt.setInt(3, review.getReviewId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating review: {}", review.getReviewId(), e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Delete review
    public boolean deleteReview(int reviewId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "DELETE FROM reviews WHERE review_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reviewId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting review: {}", reviewId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Helper method to extract review from ResultSet
    private Review extractReviewFromResultSet(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getInt("review_id"));
        review.setProductId(rs.getInt("product_id"));
        review.setBuyerId(rs.getInt("buyer_id"));
        review.setOrderId(rs.getInt("order_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setReviewDate(rs.getTimestamp("review_date"));
        return review;
    }
}