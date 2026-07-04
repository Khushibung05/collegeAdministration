package com.collegeadmin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for admin authentication and management
 */
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private static final String ADMIN_PASSWORD_HASH = "$2a$10$slYQmyNdGzin7olVN3p9POFST9/PgBkqquzi.Ss7KIUgO2t0jKMUe"; // bcrypt hash of "admin123"

    /**
     * Authenticate admin user
     */
    public boolean authenticateAdmin(String password) {
        try {
            // Simple password verification
            // In production, use Spring Security or similar
            return verifyPassword(password, ADMIN_PASSWORD_HASH);
        } catch (Exception e) {
            logger.error("Authentication error", e);
            return false;
        }
    }

    /**
     * Verify password against hash
     */
    private boolean verifyPassword(String password, String hash) throws Exception {
        // For demo purposes, using simple comparison
        // In production, use BCryptPasswordEncoder from Spring Security
        String inputHash = hashPassword(password);
        return inputHash.equals(hash) || password.equals("admin123"); // fallback for development
    }

    /**
     * Hash password
     */
    private String hashPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}
