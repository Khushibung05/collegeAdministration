package com.collegeadmin.http;

import com.collegeadmin.config.DatabaseConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.sql.*;
import java.util.*;

public class AllocationsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equals(exchange.getRequestMethod())) {
                HttpUtils.sendPlain(exchange,405,"Method Not Allowed");
                return;
            }
            String query = Optional.ofNullable(exchange.getRequestURI().getQuery()).orElse("");
            Map<String,String> qmap = HttpUtils.parseUrlEncoded(query);
            String sidS = qmap.get("sessionId");
            if (sidS==null) { HttpUtils.sendPlain(exchange,400,"sessionId required"); return; }
            int sessionId = Integer.parseInt(sidS);
            String result = generateAllocations(sessionId);
            HttpUtils.sendPlain(exchange,200,result);
        } catch (Exception e) {
            HttpUtils.sendPlain(exchange,500,e.getMessage());
        } finally { exchange.close(); }
    }

    private String generateAllocations(int sessionId) throws SQLException {
        try (Connection c = DatabaseConfig.getConnection()) {
            c.setAutoCommit(false);
            // load students
            List<Integer> students = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT id FROM students ORDER BY id");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) students.add(rs.getInt(1));
            }
            // load halls with rows & cols
            class H { int id; int rows; int cols; H(int id,int r,int c){this.id=id;rows=r;cols=c;} }
            List<H> halls = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT id,row_count,column_count FROM halls ORDER BY id");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) halls.add(new H(rs.getInt(1), rs.getInt(2), rs.getInt(3)));
            }
            if (students.isEmpty() || halls.isEmpty()) {
                c.rollback();
                return "{\"status\":\"no_students_or_halls\"}";
            }

            // remove previous allocations for the session to avoid duplicates
            try (PreparedStatement del = c.prepareStatement("DELETE FROM seat_allocations WHERE session_id = ?")) {
                del.setInt(1, sessionId);
                del.executeUpdate();
            }

            try (PreparedStatement ins = c.prepareStatement("INSERT INTO seat_allocations (session_id, student_id, hall_id, seat_row, seat_column) VALUES (?,?,?,?,?)")) {
                int si = 0;
                outer:
                for (H h : halls) {
                    for (int r = 1; r <= h.rows; r++) {
                        for (int col = 1; col <= h.cols; col++) {
                            if (si >= students.size()) break outer;
                            ins.setInt(1, sessionId);
                            ins.setInt(2, students.get(si));
                            ins.setInt(3, h.id);
                            ins.setInt(4, r);
                            ins.setInt(5, col);
                            ins.addBatch();
                            si++;
                        }
                    }
                }
                ins.executeBatch();
            }
            c.commit();
            return "{\"status\":\"ok\",\"allocated\":" + (students.size()<=totalCapacity(halls)?students.size():totalCapacity(halls)) + "}";
        }
    }

    private int totalCapacity(List<?> halls) {
        int sum=0;
        for (Object o: halls) {
            try { java.lang.reflect.Field f1 = o.getClass().getDeclaredField("rows"); java.lang.reflect.Field f2 = o.getClass().getDeclaredField("cols"); f1.setAccessible(true); f2.setAccessible(true); sum += ((int)f1.get(o))*((int)f2.get(o)); } catch (Exception ignore){}
        }
        return sum;
    }
}