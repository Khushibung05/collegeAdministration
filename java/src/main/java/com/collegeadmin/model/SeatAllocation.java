package com.collegeadmin.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Seat Allocation entity representing student seating assignments
 */
public class SeatAllocation implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int sessionId;
    private int studentId;
    private int hallId;
    private int seatRow;
    private int seatColumn;
    private LocalDateTime allocatedAt;

    // Transient fields for convenience
    private transient Student student;
    private transient Hall hall;
    private transient ExamSession session;

    // Constructors
    public SeatAllocation() {}

    public SeatAllocation(int sessionId, int studentId, int hallId, int seatRow, int seatColumn) {
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.hallId = hallId;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getHallId() {
        return hallId;
    }

    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatColumn() {
        return seatColumn;
    }

    public void setSeatColumn(int seatColumn) {
        this.seatColumn = seatColumn;
    }

    public LocalDateTime getAllocatedAt() {
        return allocatedAt;
    }

    public void setAllocatedAt(LocalDateTime allocatedAt) {
        this.allocatedAt = allocatedAt;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public ExamSession getSession() {
        return session;
    }

    public void setSession(ExamSession session) {
        this.session = session;
    }

    public String getSeatPositionString() {
        return String.format("Row %d, Col %d", seatRow, seatColumn);
    }

    @Override
    public String toString() {
        return String.format("SeatAllocation{id=%d, student=%s, hall=%s, position=Row%d/Col%d}",
                id, student != null ? student.getName() : "N/A",
                hall != null ? hall.getName() : "N/A", seatRow, seatColumn);
    }
}
