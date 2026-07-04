package com.collegeadmin.dao;

import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.model.Invigilator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Invigilator entity
 */
public class InvigilatorDAO {
    private static final Logger logger = LoggerFactory.getLogger(InvigilatorDAO.class);

    /**
     * Create a new invigilator
     */
    public Invigilator create(Invigilator invigilator) throws SQLException {
        String sql = "INSERT INTO invigilators (name, email, phone, department, is_active) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, invigilator.getName());
            pstmt.setString(2, invigilator.getEmail());
            pstmt.setString(3, invigilator.getPhone());
            pstmt.setString(4, invigilator.getDepartment());
            pstmt.setBoolean(5, invigilator.isActive());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating invigilator failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invigilator.setId(generatedKeys.getInt(1));
                    invigilator.setCreatedAt(LocalDateTime.now());
                    logger.info("Invigilator created: {}", invigilator);
                    return invigilator;
                }
            }
        }
        throw new SQLException("Failed to create invigilator.");
    }

    /**
     * Find invigilator by ID
     */
    public Optional<Invigilator> findById(int id) throws SQLException {
        String sql = "SELECT id, name, email, phone, department, is_active, created_at, updated_at " +
                "FROM invigilators WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvigilator(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find invigilator by email
     */
    public Optional<Invigilator> findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, phone, department, is_active, created_at, updated_at " +
                "FROM invigilators WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToInvigilator(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find all active invigilators
     */
    public List<Invigilator> findAll() throws SQLException {
        String sql = "SELECT id, name, email, phone, department, is_active, created_at, updated_at " +
                "FROM invigilators WHERE is_active = true ORDER BY name";

        List<Invigilator> invigilators = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                invigilators.add(mapResultSetToInvigilator(rs));
            }
        }
        return invigilators;
    }

    /**
     * Update invigilator
     */
    public void update(Invigilator invigilator) throws SQLException {
        String sql = "UPDATE invigilators SET name = ?, email = ?, phone = ?, department = ?, is_active = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, invigilator.getName());
            pstmt.setString(2, invigilator.getEmail());
            pstmt.setString(3, invigilator.getPhone());
            pstmt.setString(4, invigilator.getDepartment());
            pstmt.setBoolean(5, invigilator.isActive());
            pstmt.setInt(6, invigilator.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Invigilator updated: {}", invigilator);
            }
        }
    }

    /**
     * Delete invigilator (soft delete)
     */
    public void delete(int id) throws SQLException {
        String sql = "UPDATE invigilators SET is_active = false WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            logger.info("Invigilator deleted (soft): {}", id);
        }
    }

    /**
     * Get total count of active invigilators
     */
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM invigilators WHERE is_active = true";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Map ResultSet to Invigilator object
     */
    private Invigilator mapResultSetToInvigilator(ResultSet rs) throws SQLException {
        Invigilator invigilator = new Invigilator();
        invigilator.setId(rs.getInt("id"));
        invigilator.setName(rs.getString("name"));
        invigilator.setEmail(rs.getString("email"));
        invigilator.setPhone(rs.getString("phone"));
        invigilator.setDepartment(rs.getString("department"));
        invigilator.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            invigilator.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            invigilator.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return invigilator;
    }
}
