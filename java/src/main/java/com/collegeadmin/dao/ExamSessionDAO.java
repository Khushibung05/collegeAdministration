package com.collegeadmin.dao;

import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.model.ExamSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for ExamSession entity
 */
public class ExamSessionDAO {
    private static final Logger logger = LoggerFactory.getLogger(ExamSessionDAO.class);

    /**
     * Create a new exam session
     */
    public ExamSession create(ExamSession session) throws SQLException {
        String sql = "INSERT INTO exam_sessions (session_name, exam_date, exam_time, duration_minutes, " +
                "description, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, session.getSessionName());
            pstmt.setDate(2, Date.valueOf(session.getExamDate()));
            if (session.getExamTime() != null) {
                pstmt.setTime(3, Time.valueOf(session.getExamTime()));
            } else {
                pstmt.setNull(3, Types.TIME);
            }
            pstmt.setInt(4, session.getDurationMinutes());
            pstmt.setString(5, session.getDescription());
            pstmt.setString(6, session.getStatus().toString());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating exam session failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    session.setId(generatedKeys.getInt(1));
                    session.setCreatedAt(LocalDateTime.now());
                    logger.info("Exam session created: {}", session);
                    return session;
                }
            }
        }
        throw new SQLException("Failed to create exam session.");
    }

    /**
     * Find exam session by ID
     */
    public Optional<ExamSession> findById(int id) throws SQLException {
        String sql = "SELECT id, session_name, exam_date, exam_time, duration_minutes, " +
                "description, status, created_at, updated_at FROM exam_sessions WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToExamSession(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find all active exam sessions
     */
    public List<ExamSession> findAll() throws SQLException {
        String sql = "SELECT id, session_name, exam_date, exam_time, duration_minutes, " +
                "description, status, created_at, updated_at FROM exam_sessions " +
                "ORDER BY exam_date DESC";

        List<ExamSession> sessions = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sessions.add(mapResultSetToExamSession(rs));
            }
        }
        return sessions;
    }

    /**
     * Find sessions by status
     */
    public List<ExamSession> findByStatus(ExamSession.Status status) throws SQLException {
        String sql = "SELECT id, session_name, exam_date, exam_time, duration_minutes, " +
                "description, status, created_at, updated_at FROM exam_sessions " +
                "WHERE status = ? ORDER BY exam_date DESC";

        List<ExamSession> sessions = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToExamSession(rs));
                }
            }
        }
        return sessions;
    }

    /**
     * Update exam session
     */
    public void update(ExamSession session) throws SQLException {
        String sql = "UPDATE exam_sessions SET session_name = ?, exam_date = ?, exam_time = ?, " +
                "duration_minutes = ?, description = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, session.getSessionName());
            pstmt.setDate(2, Date.valueOf(session.getExamDate()));
            if (session.getExamTime() != null) {
                pstmt.setTime(3, Time.valueOf(session.getExamTime()));
            } else {
                pstmt.setNull(3, Types.TIME);
            }
            pstmt.setInt(4, session.getDurationMinutes());
            pstmt.setString(5, session.getDescription());
            pstmt.setString(6, session.getStatus().toString());
            pstmt.setInt(7, session.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Exam session updated: {}", session);
            }
        }
    }

    /**
     * Map ResultSet to ExamSession object
     */
    private ExamSession mapResultSetToExamSession(ResultSet rs) throws SQLException {
        ExamSession session = new ExamSession();
        session.setId(rs.getInt("id"));
        session.setSessionName(rs.getString("session_name"));
        
        Date examDate = rs.getDate("exam_date");
        if (examDate != null) {
            session.setExamDate(examDate.toLocalDate());
        }
        
        Time examTime = rs.getTime("exam_time");
        if (examTime != null) {
            session.setExamTime(examTime.toLocalTime());
        }
        
        session.setDurationMinutes(rs.getInt("duration_minutes"));
        session.setDescription(rs.getString("description"));
        session.setStatus(ExamSession.Status.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            session.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            session.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return session;
    }
}
