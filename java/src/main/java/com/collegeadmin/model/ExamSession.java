package com.collegeadmin.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Exam Session entity representing examination events
 */
public class ExamSession implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        SCHEDULED, ACTIVE, COMPLETED, CANCELLED
    }

    private int id;
    private String sessionName;
    private LocalDate examDate;
    private LocalTime examTime;
    private int durationMinutes;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ExamSession() {}

    public ExamSession(String sessionName, LocalDate examDate) {
        this.sessionName = sessionName;
        this.examDate = examDate;
        this.durationMinutes = 60;
        this.status = Status.SCHEDULED;
    }

    public ExamSession(String sessionName, LocalDate examDate, LocalTime examTime, int durationMinutes) {
        this.sessionName = sessionName;
        this.examDate = examDate;
        this.examTime = examTime;
        this.durationMinutes = durationMinutes;
        this.status = Status.SCHEDULED;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public LocalTime getExamTime() {
        return examTime;
    }

    public void setExamTime(LocalTime examTime) {
        this.examTime = examTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return String.format("ExamSession{id=%d, name='%s', date=%s, time=%s, status=%s}",
                id, sessionName, examDate, examTime, status);
    }
}
