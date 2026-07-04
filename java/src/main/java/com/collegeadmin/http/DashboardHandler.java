package com.collegeadmin.http;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class DashboardHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            handleGetDashboard(exchange);
        } else {
            sendPlainResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGetDashboard(HttpExchange exchange) throws IOException {
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Dashboard - Exam Seating System</title>
                    <meta charset="utf-8"/>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f7fa; }
                        .navbar { background: #2c3e50; color: white; padding: 15px 30px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
                        .navbar h1 { font-size: 24px; }
                        .container { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
                        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
                        .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); transition: transform 0.3s; }
                        .card:hover { transform: translateY(-5px); box-shadow: 0 4px 12px rgba(0,0,0,0.15); }
                        .card h2 { color: #2c3e50; margin-bottom: 10px; }
                        .card p { color: #7f8c8d; margin-bottom: 15px; }
                        .btn { display: inline-block; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; cursor: pointer; border: none; font-size: 14px; }
                        .btn:hover { background: #2980b9; }
                        .btn-success { background: #27ae60; }
                        .btn-success:hover { background: #229954; }
                        .btn-warning { background: #f39c12; }
                        .btn-warning:hover { background: #e67e22; }
                    </style>
                </head>
                <body>
                    <div class="navbar">
                        <h1>📊 Admin Dashboard</h1>
                    </div>
                    <div class="container">
                        <h2 style="color: #2c3e50; margin-bottom: 20px;">Welcome to Exam Seating System</h2>
                        <div class="grid">
                            <div class="card">
                                <h2>👥 Students</h2>
                                <p>Manage student records and view allocations</p>
                                <a class="btn btn-success" href="/admin#students">Manage Students</a>
                            </div>
                            <div class="card">
                                <h2>👨‍🏫 Invigilators</h2>
                                <p>Add and manage invigilator assignments</p>
                                <a class="btn btn-success" href="/admin#invigilators">Manage Invigilators</a>
                            </div>
                            <div class="card">
                                <h2>🏛️ Halls</h2>
                                <p>Configure examination halls and seating capacity</p>
                                <a class="btn btn-success" href="/admin#halls">Manage Halls</a>
                            </div>
                            <div class="card">
                                <h2>📅 Exam Sessions</h2>
                                <p>Create and manage exam sessions</p>
                                <a class="btn btn-success" href="/admin#sessions">Create Session</a>
                            </div>
                            <div class="card">
                                <h2>🎲 Generate Allocations</h2>
                                <p>Auto-generate seat allocations and invigilator duties</p>
                                <a class="btn btn-warning" href="/admin#alloc">Generate Now</a>
                            </div>
                            <div class="card">
                                <h2>📊 Reports</h2>
                                <p>View allocation reports and statistics</p>
                                <a class="btn" href="/admin#reports">View Reports</a>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;
        sendHtmlResponse(exchange, 200, html);
    }
}