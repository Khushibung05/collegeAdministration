package com.collegeadmin;

import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.http.*;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

public class ExamSeatingApp {
    private static final Logger logger = LoggerFactory.getLogger(ExamSeatingApp.class);
    private static final int PORT = 8083;
    private static HttpServer server;

    public static void main(String[] args) {
        try {
            // Initialize database (this invokes SchemaInitializer internally)
            logger.info("Initializing database...");
            DatabaseConfig.initialize();

            // Start HTTP server and register handlers
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

        // Root -> redirect to /dashboard
        server.createContext("/", exchange -> {
            try {
                exchange.getResponseHeaders().set("Location", "/dashboard");
                exchange.sendResponseHeaders(302, -1);
            } catch (IOException e) {
                logger.error("Error handling root path", e);
            } finally {
                exchange.close();
            }
        });

        // UI pages
        server.createContext("/dashboard", new DashboardHandler());
        server.createContext("/admin", new AdminUIHandler()); // consolidated admin UI
        // API endpoints
        server.createContext("/api/students", new StudentsHandler());
        server.createContext("/api/invigilators", new InvigilatorsHandler());
        server.createContext("/api/halls", new HallsHandler());
        server.createContext("/api/sessions", new SessionsHandler());
        server.createContext("/api/allocations", new AllocationsHandler());
        server.createContext("/api/reports", new ReportsHandler());

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