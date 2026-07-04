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
                                <button class="btn btn-success" onclick="alert('Coming Soon!')">Manage Students</button>
                            </div>
                            <div class="card">
                                <h2>👨‍🏫 Invigilators</h2>
                                <p>Add and manage invigilator assignments</p>
                                <button class="btn btn-success" onclick="alert('Coming Soon!')">Manage Invigilators</button>
                            </div>
                            <div class="card">
                                <h2>🏛️ Halls</h2>
                                <p>Configure examination halls and seating capacity</p>
                                <button class="btn btn-success" onclick="alert('Coming Soon!')">Manage Halls</button>
                            </div>
                            <div class="card">
                                <h2>📅 Exam Sessions</h2>
                                <p>Create and manage exam sessions</p>
                                <button class="btn btn-success" onclick="alert('Coming Soon!')">Create Session</button>
                            </div>
                            <div class="card">
                                <h2>🎲 Generate Allocations</h2>
                                <p>Auto-generate seat allocations and invigilator duties</p>
                                <button class="btn btn-warning" onclick="alert('Coming Soon!')">Generate Now</button>
                            </div>
                            <div class="card">
                                <h2>📊 Reports</h2>
                                <p>View allocation reports and statistics</p>
                                <button class="btn" onclick="alert('Coming Soon!')">View Reports</button>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """;
        sendHtmlResponse(exchange, 200, html);
    }
}