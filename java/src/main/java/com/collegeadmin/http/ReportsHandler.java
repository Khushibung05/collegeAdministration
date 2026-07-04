package com.collegeadmin.http;

import com.collegeadmin.config.DatabaseConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.*;
import java.util.Map;
import java.util.Optional;

public class ReportsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equals(exchange.getRequestMethod())) {
                HttpUtils.sendPlain(exchange,405,"Method Not Allowed"); return;
            }
            String q = Optional.ofNullable(exchange.getRequestURI().getQuery()).orElse("");
            Map<String,String> m = HttpUtils.parseUrlEncoded(q);
            String sid = m.get("sessionId");
            if (sid==null) { HttpUtils.sendPlain(exchange,400,"sessionId required"); return; }
            int sessionId = Integer.parseInt(sid);

            StringBuilder sb = new StringBuilder("[");
            try (Connection c = DatabaseConfig.getConnection();
                 PreparedStatement ps = c.prepareStatement(
                         "SELECT sa.id, sa.seat_row, sa.seat_column, s.roll_number, s.name AS student_name, h.name AS hall_name " +
                                 "FROM seat_allocations sa " +
                                 "JOIN students s ON sa.student_id = s.id " +
                                 "JOIN halls h ON sa.hall_id = h.id " +
                                 "WHERE sa.session_id = ? ORDER BY h.id, sa.seat_row, sa.seat_column")) {
                ps.setInt(1, sessionId);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first=true;
                    while (rs.next()) {
                        if (!first) sb.append(",");
                        first=false;
                        sb.append("{")
                          .append("\"id\":").append(rs.getInt("id")).append(",")
                          .append("\"seat_row\":").append(rs.getInt("seat_row")).append(",")
                          .append("\"seat_column\":").append(rs.getInt("seat_column")).append(",")
                          .append("\"roll_number\":\"").append(escape(rs.getString("roll_number"))).append("\",")
                          .append("\"student_name\":\"").append(escape(rs.getString("student_name"))).append("\",")
                          .append("\"hall_name\":\"").append(escape(rs.getString("hall_name"))).append("\"")
                          .append("}");
                    }
                }
            }
            sb.append("]");
            HttpUtils.sendJson(exchange,200,sb.toString());
        } catch (Exception e) {
            HttpUtils.sendPlain(exchange,500,e.getMessage());
        } finally { exchange.close(); }
    }

    private static String escape(String s) { return s==null?"":s.replace("\\","\\\\").replace("\"","\\\""); }
}