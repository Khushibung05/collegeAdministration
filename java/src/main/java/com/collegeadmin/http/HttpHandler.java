package com.collegeadmin.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Base HTTP handler for common operations
 */
public abstract class HttpHandler implements HttpHandler {
    protected static final Logger logger = LoggerFactory.getLogger(HttpHandler.class);

    /**
     * Send JSON response
     */
    protected void sendJsonResponse(HttpExchange exchange, int statusCode, Map<String, Object> response) throws IOException {
        String jsonResponse = new com.google.gson.Gson().toJson(response);
        sendResponse(exchange, statusCode, "application/json", jsonResponse);
    }

    /**
     * Send HTML response
     */
    protected void sendHtmlResponse(HttpExchange exchange, int statusCode, String html) throws IOException {
        sendResponse(exchange, statusCode, "text/html; charset=utf-8", html);
    }

    /**
     * Send plain text response
     */
    protected void sendPlainResponse(HttpExchange exchange, int statusCode, String text) throws IOException {
        sendResponse(exchange, statusCode, "text/plain; charset=utf-8", text);
    }

    /**
     * Send response with specified content type
     */
    protected void sendResponse(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] responseBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    /**
     * Redirect to another path
     */
    protected void sendRedirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(303, 0);
        exchange.close();
    }

    /**
     * Parse query parameters
     */
    protected Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            for (String param : query.split("&")) {
                String[] kv = param.split("=", 2);
                params.put(kv[0], kv.length > 1 ? kv[1] : "");
            }
        }
        return params;
    }

    /**
     * Parse request body as key-value pairs
     */
    protected Map<String, String> parseRequestBody(HttpExchange exchange) throws IOException {
        Map<String, String> body = new HashMap<>();
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            // Parse as form data
            for (String param : content.toString().split("&")) {
                String[] kv = param.split("=", 2);
                body.put(kv[0], kv.length > 1 ? java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
            }
        }
        return body;
    }

    /**
     * Create error response
     */
    protected Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }

    /**
     * Create success response
     */
    protected Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    /**
     * Create data response
     */
    protected Map<String, Object> createDataResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        return response;
    }
}
