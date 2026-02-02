package com.revshop.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);
    private static DatabaseConfig instance;
    private static HikariDataSource dataSource;
    private String url;
    private String username;
    private String password;

    private DatabaseConfig() {
        loadProperties();
        initializeConnectionPool();
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                logger.error("Unable to find database.properties");
                throw new RuntimeException("database.properties not found");
            }

            props.load(input);
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");

            logger.info("Database configuration loaded");

        } catch (IOException e) {
            logger.error("Error loading database properties", e);
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    private void initializeConnectionPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);

            // Connection pool settings from properties
            config.setMinimumIdle(Integer.parseInt(getProperty("pool.minimumIdle", "5")));
            config.setMaximumPoolSize(Integer.parseInt(getProperty("pool.maximumPoolSize", "20")));
            config.setConnectionTimeout(Long.parseLong(getProperty("pool.connectionTimeout", "30000")));
            config.setIdleTimeout(Long.parseLong(getProperty("pool.idleTimeout", "600000")));
            config.setMaxLifetime(Long.parseLong(getProperty("pool.maxLifetime", "1800000")));

            // MySQL specific optimizations
            config.addDataSourceProperty("cachePrepStmts", getProperty("pool.dataSource.cachePrepStmts", "true"));
            config.addDataSourceProperty("prepStmtCacheSize", getProperty("pool.dataSource.prepStmtCacheSize", "250"));
            config.addDataSourceProperty("prepStmtCacheSqlLimit", getProperty("pool.dataSource.prepStmtCacheSqlLimit", "2048"));
            config.addDataSourceProperty("useServerPrepStmts", getProperty("pool.dataSource.useServerPrepStmts", "true"));
            config.addDataSourceProperty("useLocalSessionState", getProperty("pool.dataSource.useLocalSessionState", "true"));
            config.addDataSourceProperty("rewriteBatchedStatements", getProperty("pool.dataSource.rewriteBatchedStatements", "true"));
            config.addDataSourceProperty("cacheResultSetMetadata", getProperty("pool.dataSource.cacheResultSetMetadata", "true"));
            config.addDataSourceProperty("cacheServerConfiguration", getProperty("pool.dataSource.cacheServerConfiguration", "true"));
            config.addDataSourceProperty("elideSetAutoCommits", getProperty("pool.dataSource.elideSetAutoCommits", "true"));
            config.addDataSourceProperty("maintainTimeStats", getProperty("pool.dataSource.maintainTimeStats", "false"));

            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully. Pool size: {}, Active connections: {}",
                    config.getMaximumPoolSize(), dataSource.getHikariPoolMXBean().getActiveConnections());

        } catch (Exception e) {
            logger.error("Error initializing database connection pool", e);
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    private String getProperty(String key, String defaultValue) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                return props.getProperty(key, defaultValue);
            }
        } catch (IOException e) {
            logger.warn("Error reading property {}, using default: {}", key, defaultValue, e);
        }
        return defaultValue;
    }

    public Connection getConnection() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                logger.warn("Connection pool is closed, reinitializing...");
                initializeConnectionPool();
            }
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Error getting database connection from pool", e);
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    public void closeConnectionPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }

    // ADDED: For backward compatibility with existing code
    public void closeConnection() {
        closeConnectionPool();
    }

    public static void shutdown() {
        if (instance != null) {
            instance.closeConnectionPool();
            instance = null;
        }
    }

    // For backward compatibility - returns a single connection from the pool
    @Deprecated
    public Connection getSingleConnection() {
        return getConnection();
    }

    // ADDED: Check if data source is active
    public static boolean isDataSourceActive() {
        return dataSource != null && !dataSource.isClosed();
    }
}