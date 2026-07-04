package com.collegeadmin.http;

import com.collegeadmin.config.DatabaseConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SessionsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) handleGet(exchange);
            else if ("POST".equals(exchange.getRequestMethod())) handlePost(exchange);
            else HttpUtils.sendPlain(exchange,405,"Method Not Allowed");
        } catch (Exception e) {
            HttpUtils.sendPlain(exchange,500,e.getMessage());
        } finally { exchange.close(); }
    }

    private void handleGet(HttpExchange exchange) throws IOException, SQLException {
        StringBuilder sb = new StringBuilder("[");
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, session_name, exam_date, exam_time, duration_minutes FROM exam_sessions ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            boolean first=true;
            while (rs.next()) {
                if (!first) sb.append(",");
                first=false;
                sb.append("{")
                  .append("\"id\":").append(rs.getInt("id")).append(",")
                  .append("\"session_name\":\"").append(escape(rs.getString("session_name"))).append("\",")
                  .append("\"exam_date\":\"").append(rs.getString("exam_date")).append("\",")
                  .append("\"exam_time\":\"").append(rs.getString("exam_time")).append("\",")
                  .append("\"duration_minutes\":").append(rs.getInt("duration_minutes"))
                  .append("}");
            }
            sb.append("]");
        }
        HttpUtils.sendJson(exchange,200,sb.toString());
    }

    private void handlePost(HttpExchange exchange) throws IOException, SQLException {
        int len = Integer.parseInt(Optional.ofNullable(exchange.getRequestHeaders().getFirst("Content-length")).orElse("0"));
        Map<String,String> form = HttpUtils.parseForm(exchange.getRequestBody(), len);
        String name = form.getOrDefault("session_name","").trim();
        String date = form.getOrDefault("exam_date","");
        String time = form.getOrDefault("exam_time","");
        int dur = Integer.parseInt(form.getOrDefault("duration_minutes","60"));
        if (name.isEmpty() || date.isEmpty()) { HttpUtils.sendPlain(exchange,400,"session_name and exam_date required"); return; }
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO exam_sessions (session_name, exam_date, exam_time, duration_minutes) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,name); ps.setString(2,date); ps.setString(3,time.isEmpty()?null:time); ps.setInt(4,dur);
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) {
                if (g.next()) HttpUtils.sendJson(exchange,200,"{\"id\":"+g.getInt(1)+"}");
                else HttpUtils.sendJson(exchange,200,"{\"status\":\"ok\"}");
            }
        }
    }

    private static String escape(String s) { return s==null?"":s.replace("\\","\\\\").replace("\"","\\\""); }
} 