package com.revshop.dao;

import com.revshop.config.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class BaseDAO {
    protected static final Logger logger = LogManager.getLogger(BaseDAO.class);

    // Get connection from connection pool
    protected Connection getConnection() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    // Close resources with Connection
    protected void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            logger.warn("Error closing ResultSet", e);
        }

        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            logger.warn("Error closing PreparedStatement", e);
        }

        try {
            if (conn != null) conn.close(); // Returns connection to pool
        } catch (SQLException e) {
            logger.warn("Error closing Connection", e);
        }
    }

    // Close resources without ResultSet
    protected void closeResources(PreparedStatement pstmt, Connection conn) {
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            logger.warn("Error closing PreparedStatement", e);
        }

        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            logger.warn("Error closing Connection", e);
        }
    }

    // Rollback transaction
    protected void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.warn("Error rolling back transaction", e);
            }
        }
    }

    // Set auto-commit
    protected void setAutoCommit(Connection conn, boolean autoCommit) {
        if (conn != null) {
            try {
                conn.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                logger.warn("Error setting auto-commit", e);
            }
        }
    }
}