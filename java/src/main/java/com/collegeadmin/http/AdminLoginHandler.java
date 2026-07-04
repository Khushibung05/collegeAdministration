package com.collegeadmin.http;

import com.collegeadmin.service.AdminService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP handler for admin login
 */
public class AdminLoginHandler extends HttpHandler {
    private final AdminService adminService = new AdminService();
    private static final String LOGIN_SESSION_KEY = "admin_logged_in";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            handleGetLogin(exchange);
        } else if ("POST".equals(method)) {
            handlePostLogin(exchange);
        } else {
            sendPlainResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGetLogin(HttpExchange exchange) throws IOException {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Admin Login - Exam Seating System</title>
                    <style>
                        body { font-family: Arial, sans-serif; background: #f5f5f5; }
                        .container { max-width: 400px; margin: 50px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                        h1 { color: #333; text-align: center; }
                        .form-group { margin: 20px 0; }
                        label { display: block; margin-bottom: 5px; color: #666; }
                        input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
                        button { width: 100%; padding: 10px; background: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
                        button:hover { background: #45a049; }
                        .error { color: #d32f2f; text-align: center; margin: 10px 0; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🔐 Admin Login</h1>
                        <form method="POST">
                            <div class="form-group">
                                <label for="password">Password:</label>
                                <input type="password" id="password" name="password" required>
                            </div>
                            <button type="submit">Login</button>
                        </form>
                    </div>
                </body>
                </html>
                """;
        sendHtmlResponse(exchange, 200, html);
    }

    private void handlePostLogin(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseRequestBody(exchange);
        String password = body.getOrDefault("password", "");

        if (adminService.authenticateAdmin(password)) {
            // In a real application, set a session/cookie
            logger.info("Admin login successful");
            sendRedirect(exchange, "/dashboard");
        } else {
            logger.warn("Admin login failed - invalid password");
            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Admin Login - Exam Seating System</title>
                        <style>
                            body { font-family: Arial, sans-serif; background: #f5f5f5; }
                            .container { max-width: 400px; margin: 50px auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                            h1 { color: #333; text-align: center; }
                            .form-group { margin: 20px 0; }
                            label { display: block; margin-bottom: 5px; color: #666; }
                            input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }
                            button { width: 100%; padding: 10px; background: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
                            button:hover { background: #45a049; }
                            .error { color: #d32f2f; text-align: center; margin: 10px 0; padding: 10px; background: #ffebee; border-radius: 4px; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>🔐 Admin Login</h1>
                            <div class="error">❌ Invalid password. Please try again.</div>
                            <form method="POST">
                                <div class="form-group">
                                    <label for="password">Password:</label>
                                    <input type="password" id="password" name="password" required autofocus>
                                </div>
                                <button type="submit">Login</button>
                            </form>
                        </div>
                    </body>
                    </html>
                    """;
            sendHtmlResponse(exchange, 401, html);
        }
    }
}
