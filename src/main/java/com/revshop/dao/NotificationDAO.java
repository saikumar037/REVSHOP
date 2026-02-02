package com.revshop.dao;

import com.revshop.model.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO extends BaseDAO {
    private static final Logger logger = LogManager.getLogger(NotificationDAO.class);

    // Create notification
    public boolean createNotification(Notification notification) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "INSERT INTO notifications (user_id, type, message, is_read) " +
                "VALUES (?, ?, ?, ?)";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, notification.getUserId());
            pstmt.setString(2, notification.getType());
            pstmt.setString(3, notification.getMessage());
            pstmt.setBoolean(4, notification.isRead());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    notification.setNotificationId(rs.getInt(1));
                    logger.info("Notification created with ID: {}", notification.getNotificationId());
                    return true;
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating notification for user: {}", notification.getUserId(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return false;
    }

    // Get notification by ID
    public Notification getNotificationById(int notificationId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM notifications WHERE notification_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, notificationId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractNotificationFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting notification by ID: {}", notificationId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return null;
    }

    // Get notifications by user ID
    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting notifications for user: {}", userId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return notifications;
    }

    // Get unread notifications by user ID
    public List<Notification> getUnreadNotificationsByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting unread notifications for user: {}", userId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return notifications;
    }

    // Mark notification as read
    public boolean markAsRead(int notificationId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, notificationId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error marking notification as read: {}", notificationId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Mark all notifications as read for user
    public boolean markAllAsRead(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error marking all notifications as read for user: {}", userId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Delete notification
    public boolean deleteNotification(int notificationId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "DELETE FROM notifications WHERE notification_id = ?";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, notificationId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting notification: {}", notificationId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Get notification count for user
    public int getUnreadNotificationCount(int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = FALSE";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            logger.error("Error getting notification count for user: {}", userId, e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return 0;
    }

    // Create order notification
    public boolean createOrderNotification(int userId, int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = "INSERT INTO notifications (user_id, type, message, is_read) " +
                "VALUES (?, 'ORDER', ?, FALSE)";

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, userId);
            pstmt.setString(2, "Order #" + orderId + " placed successfully!");

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error creating order notification for user: {}, order: {}", userId, orderId, e);
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    // Helper method to extract notification from ResultSet
    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setType(rs.getString("type"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }
}