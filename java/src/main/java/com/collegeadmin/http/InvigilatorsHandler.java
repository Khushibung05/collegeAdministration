package com.collegeadmin.http;

import com.collegeadmin.config.DatabaseConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.*;
import java.util.*;

public class InvigilatorsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) handleGet(exchange);
            else if ("POST".equals(exchange.getRequestMethod())) handlePost(exchange);
            else HttpUtils.sendPlain(exchange, 405, "Method Not Allowed");
        } catch (Exception e) {
            HttpUtils.sendPlain(exchange, 500, e.getMessage());
        } finally { exchange.close(); }
    }

    private void handleGet(HttpExchange exchange) throws IOException, SQLException {
        StringBuilder sb = new StringBuilder("[]");
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, email, department FROM invigilators ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            sb = new StringBuilder("[");
            boolean first = true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first=false;
                sb.append("{")
                  .append("\"id\":").append(rs.getInt("id")).append(",")
                  .append("\"name\":\"").append(escape(rs.getString("name"))).append("\",")
                  .append("\"email\":\"").append(escape(rs.getString("email"))).append("\",")
                  .append("\"department\":\"").append(escape(rs.getString("department"))).append("\"")
                  .append("}");
            }
            sb.append("]");
        }
        HttpUtils.sendJson(exchange, 200, sb.toString());
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        int len = Integer.parseInt(Optional.ofNullable(exchange.getRequestHeaders().getFirst("Content-length")).orElse("0"));
        Map<String,String> form = HttpUtils.parseForm(exchange.getRequestBody(), len);
        String name = form.getOrDefault("name","").trim();
        String email = form.getOrDefault("email","").trim();
        String dept = form.getOrDefault("department","").trim();
        if (name.isEmpty() || email.isEmpty()) { HttpUtils.sendPlain(exchange,400,"name and email required"); return; }
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO invigilators (name,email,department) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, dept.isEmpty()?null:dept);
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) {
                if (g.next()) HttpUtils.sendJson(exchange,200,"{\"id\":"+g.getInt(1)+"}");
                else HttpUtils.sendJson(exchange,200,"{\"status\":\"ok\"}");
            }
        }
    }

    private static String escape(String s) { return s==null?"":s.replace("\\","\\\\").replace("\"","\\\""); }
}