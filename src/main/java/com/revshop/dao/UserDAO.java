package com.revshop.dao;

import com.revshop.model.User;
import com.revshop.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(UserDAO.class);

    // Create user
    public boolean createUser(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "INSERT INTO users (email, password, first_name, last_name, phone, address, " +
                "user_type, security_question, security_answer, password_hint) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getAddress());
            pstmt.setString(7, user.getUserType().toString());
            pstmt.setString(8, user.getSecurityQuestion());
            pstmt.setString(9, user.getSecurityAnswer());
            pstmt.setString(10, user.getPasswordHint());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                    logger.info("User created with ID: {}", user.getUserId());
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating user: {}", user.getEmail(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return false;
    }

    // Get user by email
    public User getUserByEmail(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM users WHERE email = ? AND is_active = TRUE";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting user by email: {}", email, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Get user by ID
    public User getUserById(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM users WHERE user_id = ? AND is_active = TRUE";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting user by ID: {}", userId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Authenticate user
    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            updateLastLogin(user.getUserId());
            return user;
        }
        return null;
    }

    // Update user
    public boolean updateUser(User user) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE users SET first_name = ?, last_name = ?, phone = ?, address = ? " +
                "WHERE user_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getAddress());
            pstmt.setInt(5, user.getUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating user: {}", user.getUserId(), e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Change password
    public boolean changePassword(int userId, String newPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE users SET password = ? WHERE user_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, PasswordUtil.hashPassword(newPassword));
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error changing password for user: {}", userId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Get password hint
    public String getPasswordHint(String email) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT password_hint FROM users WHERE email = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("password_hint");
            }
        } catch (SQLException e) {
            logger.error("Error getting password hint for: {}", email, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Reset password using security question
    public boolean resetPassword(String email, String securityAnswer, String newPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT user_id FROM users WHERE email = ? AND security_answer = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, securityAnswer);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                return changePassword(userId, newPassword);
            }
        } catch (SQLException e) {
            logger.error("Error resetting password for: {}", email, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return false;
    }

    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM users WHERE is_active = TRUE";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all users", e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return users;
    }

    // DELETE USER METHOD
    public boolean deleteUser(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting user: {}", userId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Update last login time
    public boolean updateLastLogin(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error updating last login for user ID: {}", userId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Helper method to extract user from ResultSet
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setUserType(User.UserType.valueOf(rs.getString("user_type")));
        user.setCreatedAt(rs.getTimestamp("created_at"));

        // Safely get last_login (might be null)
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (!rs.wasNull()) {
            user.setLastLogin(lastLogin);
        }

        user.setActive(rs.getBoolean("is_active"));
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswer(rs.getString("security_answer"));
        user.setPasswordHint(rs.getString("password_hint"));
        return user;
    }
}