package com.collegeadmin.dao;

import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Student entity
 */
public class StudentDAO {
    private static final Logger logger = LoggerFactory.getLogger(StudentDAO.class);

    /**
     * Create a new student
     */
    public Student create(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, roll_number, email, phone, is_active) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getRollNumber());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhone());
            pstmt.setBoolean(5, student.isActive());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating student failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setId(generatedKeys.getInt(1));
                    student.setCreatedAt(LocalDateTime.now());
                    logger.info("Student created: {}", student);
                    return student;
                }
            }
        }
        throw new SQLException("Failed to create student.");
    }

    /**
     * Find student by ID
     */
    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT id, name, roll_number, email, phone, is_active, created_at, updated_at " +
                "FROM students WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find student by roll number
     */
    public Optional<Student> findByRollNumber(String rollNumber) throws SQLException {
        String sql = "SELECT id, name, roll_number, email, phone, is_active, created_at, updated_at " +
                "FROM students WHERE roll_number = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rollNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Find all active students
     */
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT id, name, roll_number, email, phone, is_active, created_at, updated_at " +
                "FROM students WHERE is_active = true ORDER BY name";

        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }

    /**
     * Update student
     */
    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, phone = ?, is_active = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setBoolean(4, student.isActive());
            pstmt.setInt(5, student.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Student updated: {}", student);
            }
        }
    }

    /**
     * Delete student (soft delete)
     */
    public void delete(int id) throws SQLException {
        String sql = "UPDATE students SET is_active = false WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            logger.info("Student deleted (soft): {}", id);
        }
    }

    /**
     * Get total count of active students
     */
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM students WHERE is_active = true";

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
     * Map ResultSet to Student object
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setRollNumber(rs.getString("roll_number"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            student.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            student.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return student;
    }
}
