package com.collegeadmin.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class HttpUtils {
    private HttpUtils() {}

    public static Map<String,String> parseForm(InputStream in, int contentLength) throws IOException {
        String body = readAll(in, contentLength);
        return parseUrlEncoded(body);
    }

    public static Map<String,String> parseUrlEncoded(String s) {
        Map<String,String> map = new HashMap<>();
        if (s == null || s.isEmpty()) return map;
        String[] pairs = s.split("&");
        for (String p : pairs) {
            String[] kv = p.split("=",2);
            String k = urlDecode(kv[0]);
            String v = kv.length>1 ? urlDecode(kv[1]) : "";
            map.put(k, v);
        }
        return map;
    }

    public static String readAll(InputStream in, int contentLength) throws IOException {
        if (contentLength > 0) {
            byte[] buf = new byte[contentLength];
            int read = 0;
            while (read < contentLength) {
                int r = in.read(buf, read, contentLength - read);
                if (r < 0) break;
                read += r;
            }
            return new String(buf, 0, read, StandardCharsets.UTF_8);
        } else {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        }
    }

    public static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        byte[] b = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, b.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(b);
        }
    }

    public static void sendHtml(HttpExchange exchange, int status, String html) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        byte[] b = html.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, b.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(b);
        }
    }

    public static void sendPlain(HttpExchange exchange, int status, String text) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        byte[] b = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, b.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(b);
        }
    }
}