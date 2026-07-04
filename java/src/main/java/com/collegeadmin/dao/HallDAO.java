package com.collegeadmin.dao;

import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.model.Hall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Hall entity
 */
public class HallDAO {
    private static final Logger logger = LoggerFactory.getLogger(HallDAO.class);

    /**
     * Create a new hall
     */
    public Hall create(Hall hall) throws SQLException {
        String sql = "INSERT INTO halls (name, rows, columns, is_active) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, hall.getName());
            pstmt.setInt(2, hall.getRows());
            pstmt.setInt(3, hall.getColumns());
            pstmt.setBoolean(4, hall.isActive());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating hall failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    hall.setId(generatedKeys.getInt(1));
                    hall.setCreatedAt(LocalDateTime.now());
                    logger.info("Hall created: {}", hall);
                    return hall;
                }
            }
        }
        throw new SQLException("Failed to create hall.");
    }

    /**
     * Find hall by ID
     */
    public Optional<Hall> findById(int id) throws SQLException {
        String sql = "SELECT id, name, rows, columns, is_active, created_at, updated_at " +
                "FROM halls WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHall(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find all active halls
     */
    public List<Hall> findAll() throws SQLException {
        String sql = "SELECT id, name, rows, columns, is_active, created_at, updated_at " +
                "FROM halls WHERE is_active = true ORDER BY name";

        List<Hall> halls = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                halls.add(mapResultSetToHall(rs));
            }
        }
        return halls;
    }

    /**
     * Update hall
     */
    public void update(Hall hall) throws SQLException {
        String sql = "UPDATE halls SET name = ?, rows = ?, columns = ?, is_active = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hall.getName());
            pstmt.setInt(2, hall.getRows());
            pstmt.setInt(3, hall.getColumns());
            pstmt.setBoolean(4, hall.isActive());
            pstmt.setInt(5, hall.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Hall updated: {}", hall);
            }
        }
    }

    /**
     * Delete hall (soft delete)
     */
    public void delete(int id) throws SQLException {
        String sql = "UPDATE halls SET is_active = false WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            logger.info("Hall deleted (soft): {}", id);
        }
    }

    /**
     * Get total capacity of all active halls
     */
    public int getTotalCapacity() throws SQLException {
        String sql = "SELECT SUM(rows * columns) as capacity FROM halls WHERE is_active = true";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                Object capacity = rs.getObject("capacity");
                return capacity != null ? rs.getInt("capacity") : 0;
            }
        }
        return 0;
    }

    /**
     * Map ResultSet to Hall object
     */
    private Hall mapResultSetToHall(ResultSet rs) throws SQLException {
        Hall hall = new Hall();
        hall.setId(rs.getInt("id"));
        hall.setName(rs.getString("name"));
        hall.setRows(rs.getInt("rows"));
        hall.setColumns(rs.getInt("columns"));
        hall.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            hall.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            hall.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return hall;
    }
}
