package com.collegeadmin.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Invigilator entity representing exam supervision staff
 */
public class Invigilator implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String email;
    private String phone;
    private String department;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Invigilator() {}

    public Invigilator(String name, String email) {
        this.name = name;
        this.email = email.toLowerCase().trim();
        this.isActive = true;
    }

    public Invigilator(String name, String email, String phone, String department) {
        this.name = name;
        this.email = email.toLowerCase().trim();
        this.phone = phone;
        this.department = department;
        this.isActive = true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase().trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
        return String.format("Invigilator{id=%d, name='%s', email='%s', department='%s'}",
                id, name, email, department);
    }
}
