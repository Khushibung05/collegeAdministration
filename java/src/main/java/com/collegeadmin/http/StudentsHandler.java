package com.collegeadmin.http;

import com.collegeadmin.config.DatabaseConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.*;
import java.util.*;

public class StudentsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGet(exchange);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                handlePost(exchange);
            } else {
                HttpUtils.sendPlain(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            HttpUtils.sendPlain(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, roll_number, name, email FROM students ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first = false;
                sb.append("{")
                  .append("\"id\":").append(rs.getInt("id")).append(",")
                  .append("\"roll_number\":\"").append(escape(rs.getString("roll_number"))).append("\",")
                  .append("\"name\":\"").append(escape(rs.getString("name"))).append("\",")
                  .append("\"email\":\"").append(escape(rs.getString("email"))).append("\"")
                  .append("}");
            }
        }
        sb.append("]");
        HttpUtils.sendJson(exchange, 200, sb.toString());
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        int len = Integer.parseInt(Optional.ofNullable(exchange.getRequestHeaders().getFirst("Content-length")).orElse("0"));
        Map<String,String> form = HttpUtils.parseForm(exchange.getRequestBody(), len);
        String roll = form.getOrDefault("roll_number", "").trim();
        String name = form.getOrDefault("name", "").trim();
        String email = form.getOrDefault("email", "").trim();
        if (roll.isEmpty() || name.isEmpty()) {
            HttpUtils.sendPlain(exchange, 400, "roll_number and name required");
            return;
        }
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO students (roll_number, name, email) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, roll);
            ps.setString(2, name);
            ps.setString(3, email.isEmpty()?null:email);
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) {
                if (g.next()) HttpUtils.sendJson(exchange, 200, "{\"id\":" + g.getInt(1) + "}");
                else HttpUtils.sendJson(exchange, 200, "{\"status\":\"ok\"}");
            }
        }
    }

    private static String escape(String s) { return s==null?"":s.replace("\\","\\\\").replace("\"","\\\""); }
}