package com.revshop.service;

import com.revshop.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.revshop.util.ConsoleColors.*;

public class NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    // Send notification
    public void sendNotification(int userId, String message, String type) {
        String sql = "INSERT INTO notifications (user_id, message, type) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, message);
            pstmt.setString(3, type);

            pstmt.executeUpdate();
            logger.info("Notification sent to user {}: {}", userId, message);

        } catch (SQLException e) {
            logger.error("Error sending notification", e);
        }
    }

    // Get notifications for user
    public void getNotifications(int userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<String> notifications = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                boolean hasNotifications = false;
                while (rs.next()) {
                    hasNotifications = true;
                    int id = rs.getInt("notification_id");
                    String message = rs.getString("message");
                    String type = rs.getString("type");
                    boolean isRead = rs.getBoolean("is_read");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    String status = isRead ? notification("[READ]") : success("[NEW]");
                    System.out.printf("%s ID: %d | Type: %s | %s%n", status, id, type, message);
                    System.out.println(info("   Time: " + createdAt));
                }

                if (!hasNotifications) {
                    System.out.println(info("No notifications."));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting notifications for user: {}", userId, e);
        }
    }

    // Mark notification as read
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, notificationId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Error marking notification as read: {}", notificationId, e);
            return false;
        }
    }

    // Get unread notification count
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = FALSE";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting unread count for user: {}", userId, e);
        }
        return 0;
    }
}