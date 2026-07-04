package com.collegeadmin.dao;

import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.model.SeatAllocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for SeatAllocation entity
 */
public class SeatAllocationDAO {
    private static final Logger logger = LoggerFactory.getLogger(SeatAllocationDAO.class);

    /**
     * Create a new seat allocation
     */
    public SeatAllocation create(SeatAllocation allocation) throws SQLException {
        String sql = "INSERT INTO seat_allocations (session_id, student_id, hall_id, seat_row, seat_column) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, allocation.getSessionId());
            pstmt.setInt(2, allocation.getStudentId());
            pstmt.setInt(3, allocation.getHallId());
            pstmt.setInt(4, allocation.getSeatRow());
            pstmt.setInt(5, allocation.getSeatColumn());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating seat allocation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    allocation.setId(generatedKeys.getInt(1));
                    allocation.setAllocatedAt(LocalDateTime.now());
                    logger.info("Seat allocation created: {}", allocation);
                    return allocation;
                }
            }
        }
        throw new SQLException("Failed to create seat allocation.");
    }

    /**
     * Find allocation by student roll number in a session
     */
    public Optional<SeatAllocation> findByStudentRollInSession(String rollNumber, int sessionId) throws SQLException {
        String sql = "SELECT sa.id, sa.session_id, sa.student_id, sa.hall_id, sa.seat_row, sa.seat_column, " +
                "sa.allocated_at FROM seat_allocations sa " +
                "JOIN students s ON sa.student_id = s.id " +
                "WHERE s.roll_number = ? AND sa.session_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rollNumber);
            pstmt.setInt(2, sessionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToAllocation(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find all allocations for a session
     */
    public List<SeatAllocation> findBySession(int sessionId) throws SQLException {
        String sql = "SELECT id, session_id, student_id, hall_id, seat_row, seat_column, allocated_at " +
                "FROM seat_allocations WHERE session_id = ? ORDER BY hall_id, seat_row, seat_column";

        List<SeatAllocation> allocations = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapResultSetToAllocation(rs));
                }
            }
        }
        return allocations;
    }

    /**
     * Delete all allocations for a session
     */
    public void deleteBySession(int sessionId) throws SQLException {
        String sql = "DELETE FROM seat_allocations WHERE session_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);
            pstmt.executeUpdate();
            logger.info("Seat allocations deleted for session: {}", sessionId);
        }
    }

    /**
     * Check if a seat is already occupied
     */
    public boolean isSeatOccupied(int sessionId, int hallId, int row, int column) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM seat_allocations " +
                "WHERE session_id = ? AND hall_id = ? AND seat_row = ? AND seat_column = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, sessionId);
            pstmt.setInt(2, hallId);
            pstmt.setInt(3, row);
            pstmt.setInt(4, column);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }

    /**
     * Map ResultSet to SeatAllocation object
     */
    private SeatAllocation mapResultSetToAllocation(ResultSet rs) throws SQLException {
        SeatAllocation allocation = new SeatAllocation();
        allocation.setId(rs.getInt("id"));
        allocation.setSessionId(rs.getInt("session_id"));
        allocation.setStudentId(rs.getInt("student_id"));
        allocation.setHallId(rs.getInt("hall_id"));
        allocation.setSeatRow(rs.getInt("seat_row"));
        allocation.setSeatColumn(rs.getInt("seat_column"));
        
        Timestamp allocatedAt = rs.getTimestamp("allocated_at");
        if (allocatedAt != null) {
            allocation.setAllocatedAt(allocatedAt.toLocalDateTime());
        }
        
        return allocation;
    }
}
