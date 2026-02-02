package com.revshop.dao;

import com.revshop.model.Seller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class SellerDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(SellerDAO.class);

    // Create seller
    public boolean createSeller(Seller seller) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        // First create user
        UserDAO userDAO = new UserDAO();
        if (!userDAO.createUser(seller)) {
            return false;
        }

        // Then create seller record
        String sql = "INSERT INTO sellers (seller_id, business_name, business_address, tax_id, business_phone) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, seller.getUserId());
            pstmt.setString(2, seller.getBusinessName());
            pstmt.setString(3, seller.getBusinessAddress());
            pstmt.setString(4, seller.getTaxId());
            pstmt.setString(5, seller.getBusinessPhone());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error creating seller record: {}", seller.getUserId(), e);
            // Rollback user creation using UserDAO's deleteUser method
            userDAO.deleteUser(seller.getUserId());
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Get seller by ID
    public Seller getSellerById(int sellerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT u.*, s.* FROM users u " +
                "JOIN sellers s ON u.user_id = s.seller_id " +
                "WHERE u.user_id = ? AND u.is_active = TRUE";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sellerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractSellerFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting seller by ID: {}", sellerId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Get seller by user ID (returns only seller-specific info)
    public Seller getSellerInfoByUserId(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT s.* FROM sellers s WHERE s.seller_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Seller seller = new Seller();
                seller.setBusinessName(rs.getString("business_name"));
                seller.setBusinessAddress(rs.getString("business_address"));
                seller.setTaxId(rs.getString("tax_id"));
                seller.setBusinessPhone(rs.getString("business_phone"));
                return seller;
            }
        } catch (SQLException e) {
            logger.error("Error getting seller info by user ID: {}", userId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Update seller
    public boolean updateSeller(Seller seller) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE sellers SET business_name = ?, business_address = ?, " +
                "tax_id = ?, business_phone = ? WHERE seller_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, seller.getBusinessName());
            pstmt.setString(2, seller.getBusinessAddress());
            pstmt.setString(3, seller.getTaxId());
            pstmt.setString(4, seller.getBusinessPhone());
            pstmt.setInt(5, seller.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating seller: {}", seller.getUserId(), e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Delete seller (soft delete)
    public boolean deleteSeller(int sellerId) {
        UserDAO userDAO = new UserDAO();
        return userDAO.deleteUser(sellerId);
        // Optionally also delete from sellers table:
        // String sql = "DELETE FROM sellers WHERE seller_id = ?";
    }

    // Helper method to extract seller from ResultSet
    private Seller extractSellerFromResultSet(ResultSet rs) throws SQLException {
        Seller seller = new Seller();
        seller.setUserId(rs.getInt("user_id"));
        seller.setEmail(rs.getString("email"));
        seller.setPassword(rs.getString("password"));
        seller.setFirstName(rs.getString("first_name"));
        seller.setLastName(rs.getString("last_name"));
        seller.setPhone(rs.getString("phone"));
        seller.setAddress(rs.getString("address"));
        seller.setUserType(com.revshop.model.User.UserType.valueOf(rs.getString("user_type")));
        seller.setCreatedAt(rs.getTimestamp("created_at"));
        seller.setActive(rs.getBoolean("is_active"));
        seller.setSecurityQuestion(rs.getString("security_question"));
        seller.setSecurityAnswer(rs.getString("security_answer"));
        seller.setPasswordHint(rs.getString("password_hint"));
        seller.setBusinessName(rs.getString("business_name"));
        seller.setBusinessAddress(rs.getString("business_address"));
        seller.setTaxId(rs.getString("tax_id"));
        seller.setBusinessPhone(rs.getString("business_phone"));
        return seller;
    }
}