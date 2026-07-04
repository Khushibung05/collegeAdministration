package com.collegeadmin.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Invigilator Duty entity representing staff assignments to halls
 */
public class InvigilatorDuty implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int sessionId;
    private int invigilatorId;
    private int hallId;
    private LocalDateTime assignedAt;

    // Transient fields for convenience
    private transient Invigilator invigilator;
    private transient Hall hall;
    private transient ExamSession session;

    // Constructors
    public InvigilatorDuty() {}

    public InvigilatorDuty(int sessionId, int invigilatorId, int hallId) {
        this.sessionId = sessionId;
        this.invigilatorId = invigilatorId;
        this.hallId = hallId;
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

    public int getInvigilatorId() {
        return invigilatorId;
    }

    public void setInvigilatorId(int invigilatorId) {
        this.invigilatorId = invigilatorId;
    }

    public int getHallId() {
        return hallId;
    }

    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Invigilator getInvigilator() {
        return invigilator;
    }

    public void setInvigilator(Invigilator invigilator) {
        this.invigilator = invigilator;
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

    @Override
    public String toString() {
        return String.format("InvigilatorDuty{id=%d, invigilator=%s, hall=%s, session=%s}",
                id, invigilator != null ? invigilator.getName() : "N/A",
                hall != null ? hall.getName() : "N/A",
                session != null ? session.getSessionName() : "N/A");
    }
}
