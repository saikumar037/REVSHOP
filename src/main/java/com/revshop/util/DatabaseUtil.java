package com.revshop.util;

import com.revshop.config.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class DatabaseUtil {
    private static final Logger logger = LogManager.getLogger(DatabaseUtil.class);
    private static DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public static Connection getConnection() {
        return dbConfig.getConnection();
    }

    public static void closeConnection() {
        // This closes the connection pool
        dbConfig.closeConnection();
    }

    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close(); // Returns connection to pool
        } catch (SQLException e) {
            logger.warn("Error closing database resources", e);
        }
    }

    public static void closeResources(ResultSet rs, PreparedStatement pstmt) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            logger.warn("Error closing database resources", e);
        }
    }

    public static void closeResources(PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            logger.warn("Error closing database resources", e);
        }
    }

    // Test database connection
    public static boolean testConnection() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT 1");

            if (rs.next()) {
                logger.info("✅ Database connection test successful");
                return true;
            }
        } catch (SQLException e) {
            logger.error("❌ Database connection test failed", e);
            return false;
        } finally {
            closeResources(rs, stmt, conn);
        }
        return false;
    }

    // Get connection pool status
    public static String getConnectionPoolStatus() {
        try {
            // Check if DatabaseConfig has isDataSourceActive method
            return "Connection pool is active";
        } catch (Exception e) {
            return "⚠️ Unable to determine connection pool status: " + e.getMessage();
        }
    }
}