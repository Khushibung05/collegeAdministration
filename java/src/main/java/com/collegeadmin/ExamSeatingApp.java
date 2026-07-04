package com.collegeadmin;

import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.http.AdminLoginHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

/**
 * Main application entry point
 */
public class ExamSeatingApp {
    private static final Logger logger = LoggerFactory.getLogger(ExamSeatingApp.class);
    private static final int PORT = 8083;
    private static HttpServer server;

    public static void main(String[] args) {
        try {
            // Initialize database
            logger.info("Initializing database...");
            DatabaseConfig.initialize();

            // Start HTTP server
            logger.info("Starting HTTP server on port {}", PORT);
            startServer();

            logger.info("\n========================================");
            logger.info("Exam Seating System - Java Edition");
            logger.info("Server running on http://localhost:{}", PORT);
            logger.info("Default Admin Password: admin123");
            logger.info("========================================\n");

        } catch (SQLException e) {
            logger.error("Database initialization failed", e);
            System.exit(1);
        } catch (IOException e) {
            logger.error("HTTP server failed to start", e);
            System.exit(1);
        }
    }

    private static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Routes
        server.createContext("/", exchange -> {
            try {
                exchange.sendResponseHeaders(302, 0);
                exchange.getResponseHeaders().set("Location", "/login");
                exchange.close();
            } catch (IOException e) {
                logger.error("Error handling root path", e);
            }
        });

        server.createContext("/login", new AdminLoginHandler());

        // Add more contexts as needed
        server.setExecutor(null);
        server.start();
    }

    public static void stop() {
        if (server != null) {
            server.stop(0);
            DatabaseConfig.shutdown();
            logger.info("Server stopped");
        }
    }
}
