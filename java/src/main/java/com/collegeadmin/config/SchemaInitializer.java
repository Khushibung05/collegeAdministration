package com.collegeadmin.config;

/**
 * SQL schema definitions for the application
 */
public class SchemaInitializer {
    public static String getCreateTablesSQL() {
        return """
            -- Admin/Session table
            CREATE TABLE IF NOT EXISTS admin_sessions (
                id INT PRIMARY KEY AUTO_INCREMENT,
                admin_username VARCHAR(50) NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP NULL,
                UNIQUE(admin_username)
            );

            -- Students table
            CREATE TABLE IF NOT EXISTS students (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL,
                roll_number VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(100) UNIQUE,
                phone VARCHAR(15),
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_roll (roll_number),
                INDEX idx_email (email)
            );

            -- Invigilators (staff/teachers) table
            CREATE TABLE IF NOT EXISTS invigilators (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                phone VARCHAR(15),
                department VARCHAR(100),
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_email (email)
            );

            -- Examination halls/rooms
            CREATE TABLE IF NOT EXISTS halls (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL UNIQUE,
                rows INT NOT NULL,
                columns INT NOT NULL,
                total_capacity INT GENERATED ALWAYS AS (rows * columns) STORED,
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            );

            -- Exam sessions
            CREATE TABLE IF NOT EXISTS exam_sessions (
                id INT PRIMARY KEY AUTO_INCREMENT,
                session_name VARCHAR(100) NOT NULL,
                exam_date DATE NOT NULL,
                exam_time TIME,
                duration_minutes INT DEFAULT 60,
                description TEXT,
                status ENUM('SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            );

            -- Seat allocations
            CREATE TABLE IF NOT EXISTS seat_allocations (
                id INT PRIMARY KEY AUTO_INCREMENT,
                session_id INT NOT NULL,
                student_id INT NOT NULL,
                hall_id INT NOT NULL,
                seat_row INT NOT NULL,
                seat_column INT NOT NULL,
                allocated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (session_id) REFERENCES exam_sessions(id) ON DELETE CASCADE,
                FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE CASCADE,
                UNIQUE KEY unique_allocation (session_id, student_id),
                UNIQUE KEY unique_seat (session_id, hall_id, seat_row, seat_column),
                INDEX idx_session (session_id),
                INDEX idx_student (student_id)
            );

            -- Invigilator duty assignments
            CREATE TABLE IF NOT EXISTS invigilator_duties (
                id INT PRIMARY KEY AUTO_INCREMENT,
                session_id INT NOT NULL,
                invigilator_id INT NOT NULL,
                hall_id INT NOT NULL,
                assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (session_id) REFERENCES exam_sessions(id) ON DELETE CASCADE,
                FOREIGN KEY (invigilator_id) REFERENCES invigilators(id) ON DELETE CASCADE,
                FOREIGN KEY (hall_id) REFERENCES halls(id) ON DELETE CASCADE,
                UNIQUE KEY unique_duty (session_id, invigilator_id, hall_id),
                INDEX idx_session (session_id),
                INDEX idx_invigilator (invigilator_id)
            );

            -- Audit log
            CREATE TABLE IF NOT EXISTS audit_logs (
                id INT PRIMARY KEY AUTO_INCREMENT,
                admin_id INT,
                action VARCHAR(50) NOT NULL,
                entity_type VARCHAR(50) NOT NULL,
                entity_id INT,
                details JSON,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_admin (admin_id),
                INDEX idx_action (action),
                INDEX idx_created (created_at)
            );
            """;
    }
}
