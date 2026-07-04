package com.collegeadmin.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database configuration and connection pooling
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "exam_seating_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Smart_interview1";

    /**
     * Initialize database connection pool
     */
    public static void initialize() throws SQLException {
        if (dataSource != null) return;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                DB_HOST, DB_PORT, DB_NAME));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        try {
            dataSource = new HikariDataSource(config);
            logger.info("Database connection pool initialized successfully");
            initializeSchema();
        } catch (Exception e) {
                logger.error("Failed to initialize database connection pool", e);
                throw new SQLException("Failed to initialize database", e);
            }
    }

    /**
     * Get a connection from the pool
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Database not initialized. Call DatabaseConfig.initialize() first.");
        }
        return dataSource.getConnection();
    }

    /**
     * Close all connections in the pool
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }

    /**
     * Initialize database schema and tables
     */
    private static void initializeSchema() {
        try (Connection conn = getConnection()) {
            executeSql(conn, SchemaInitializer.getCreateTablesSQL());
            logger.info("Database schema initialized");
        } catch (SQLException e) {
            logger.warn("Schema already initialized or error occurred", e);
        }
    }

    private static void executeSql(Connection conn, String sql) throws SQLException {
        try (var stmt = conn.createStatement()) {
            for (String statement : sql.split(";")) {
                statement = statement.trim();
                if (!statement.isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
    }
}
