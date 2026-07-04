package com.collegeadmin.dao;

import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.model.InvigilatorDuty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for InvigilatorDuty entity
 */
public class InvigilatorDutyDAO {
    private static final Logger logger = LoggerFactory.getLogger(InvigilatorDutyDAO.class);

    /**
     * Create a new duty assignment
     */
    public InvigilatorDuty create(InvigilatorDuty duty) throws SQLException {
        String sql = "INSERT INTO invigilator_duties (session_id, invigilator_id, hall_id) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, duty.getSessionId());
            pstmt.setInt(2, duty.getInvigilatorId());
            pstmt.setInt(3, duty.getHallId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating duty assignment failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    duty.setId(generatedKeys.getInt(1));
                    duty.setAssignedAt(LocalDateTime.now());
                    logger.info("Invigilator duty created: {}", duty);
                    return duty;
                }
            }
        }
        throw new SQLException("Failed to create duty assignment.");
    }

    /**
     * Find duty by invigilator email in a session
     */
    public Optional<InvigilatorDuty> findByInvigilatorEmailInSession(String email, int sessionId) throws SQLException {
        String sql = "SELECT id.id, id.session_id, id.invigilator_id, id.hall_id, id.assigned_at " +
                "FROM invigilator_duties id " +
                "JOIN invigilators i ON id.invigilator_id = i.id " +
                "WHERE LOWER(i.email) = LOWER(?) AND id.session_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setInt(2, sessionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDuty(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find all duties for a session
     */
    public List<InvigilatorDuty> findBySession(int sessionId) throws SQLException {
        String sql = "SELECT id, session_id, invigilator_id, hall_id, assigned_at " +
                "FROM invigilator_duties WHERE session_id = ? ORDER BY hall_id";

        List<InvigilatorDuty> duties = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    duties.add(mapResultSetToDuty(rs));
                }
            }
        }
        return duties;
    }

    /**
     * Delete all duties for a session
     */
    public void deleteBySession(int sessionId) throws SQLException {
        String sql = "DELETE FROM invigilator_duties WHERE session_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);
            pstmt.executeUpdate();
            logger.info("Invigilator duties deleted for session: {}", sessionId);
        }
    }

    /**
     * Map ResultSet to InvigilatorDuty object
     */
    private InvigilatorDuty mapResultSetToDuty(ResultSet rs) throws SQLException {
        InvigilatorDuty duty = new InvigilatorDuty();
        duty.setId(rs.getInt("id"));
        duty.setSessionId(rs.getInt("session_id"));
        duty.setInvigilatorId(rs.getInt("invigilator_id"));
        duty.setHallId(rs.getInt("hall_id"));
        
        Timestamp assignedAt = rs.getTimestamp("assigned_at");
        if (assignedAt != null) {
            duty.setAssignedAt(assignedAt.toLocalDateTime());
        }
        
        return duty;
    }
}
