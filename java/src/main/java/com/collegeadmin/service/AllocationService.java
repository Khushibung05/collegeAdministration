package com.collegeadmin.service;

import com.collegeadmin.dao.*;
import com.collegeadmin.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * Service for managing seat allocations and invigilator duties
 */
public class AllocationService {
    private static final Logger logger = LoggerFactory.getLogger(AllocationService.class);

    private final StudentDAO studentDAO = new StudentDAO();
    private final InvigilatorDAO invigilatorDAO = new InvigilatorDAO();
    private final HallDAO hallDAO = new HallDAO();
    private final SeatAllocationDAO seatAllocationDAO = new SeatAllocationDAO();
    private final InvigilatorDutyDAO invigilatorDutyDAO = new InvigilatorDutyDAO();

    /**
     * Generate seat allocations and invigilator duties for a session
     */
    public void generateAllocation(int sessionId) throws SQLException, IllegalArgumentException {
        logger.info("Starting allocation generation for session: {}", sessionId);

        // Get data
        List<Student> students = studentDAO.findAll();
        List<Hall> halls = hallDAO.findAll();
        List<Invigilator> invigilators = invigilatorDAO.findAll();

        // Validation
        if (students.isEmpty()) {
            throw new IllegalArgumentException("No students available for allocation");
        }
        if (halls.isEmpty()) {
            throw new IllegalArgumentException("No halls available for allocation");
        }
        if (invigilators.isEmpty()) {
            throw new IllegalArgumentException("No invigilators available for allocation");
        }

        int totalCapacity = halls.stream().mapToInt(Hall::getTotalCapacity).sum();
        if (totalCapacity < students.size()) {
            throw new IllegalArgumentException(
                    String.format("Not enough seats. Students: %d, Available: %d",
                            students.size(), totalCapacity));
        }

        if (invigilators.size() < halls.size()) {
            throw new IllegalArgumentException(
                    String.format("Not enough invigilators. Halls: %d, Invigilators: %d",
                            halls.size(), invigilators.size()));
        }

        // Clear existing allocations
        seatAllocationDAO.deleteBySession(sessionId);
        invigilatorDutyDAO.deleteBySession(sessionId);

        // Allocate seats
        allocateSeats(sessionId, students, halls);

        // Assign invigilators
        assignInvigilators(sessionId, halls, invigilators);

        logger.info("Allocation generation completed successfully for session: {}", sessionId);
    }

    /**
     * Allocate students to seats
     */
    private void allocateSeats(int sessionId, List<Student> students, List<Hall> halls) throws SQLException {
        int studentIndex = 0;

        for (Hall hall : halls) {
            for (int row = 1; row <= hall.getRows(); row++) {
                for (int col = 1; col <= hall.getColumns(); col++) {
                    if (studentIndex < students.size()) {
                        Student student = students.get(studentIndex);
                        SeatAllocation allocation = new SeatAllocation(
                                sessionId, student.getId(), hall.getId(), row, col
                        );
                        seatAllocationDAO.create(allocation);
                        studentIndex++;
                    }
                }
            }
        }

        logger.info("Allocated {} students to {} halls", studentIndex, halls.size());
    }

    /**
     * Assign invigilators to halls
     */
    private void assignInvigilators(int sessionId, List<Hall> halls, List<Invigilator> invigilators) throws SQLException {
        for (int i = 0; i < halls.size(); i++) {
            if (i < invigilators.size()) {
                Hall hall = halls.get(i);
                Invigilator invigilator = invigilators.get(i);
                InvigilatorDuty duty = new InvigilatorDuty(sessionId, invigilator.getId(), hall.getId());
                invigilatorDutyDAO.create(duty);
            }
        }

        logger.info("Assigned {} invigilators to halls", Math.min(halls.size(), invigilators.size()));
    }

    /**
     * Get student's seat allocation by roll number
     */
    public Optional<Map<String, String>> getStudentSeatAllocation(String rollNumber, int sessionId) throws SQLException {
        Optional<SeatAllocation> allocation = seatAllocationDAO.findByStudentRollInSession(rollNumber, sessionId);

        if (allocation.isPresent()) {
            SeatAllocation alloc = allocation.get();
            Optional<Hall> hall = hallDAO.findById(alloc.getHallId());

            if (hall.isPresent()) {
                Map<String, String> result = new HashMap<>();
                result.put("hall", hall.get().getName());
                result.put("row", String.valueOf(alloc.getSeatRow()));
                result.put("column", String.valueOf(alloc.getSeatColumn()));
                result.put("position", alloc.getSeatPositionString());
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }

    /**
     * Get invigilator's assigned hall by email
     */
    public Optional<Map<String, String>> getInvigilatorDuty(String email, int sessionId) throws SQLException {
        Optional<InvigilatorDuty> duty = invigilatorDutyDAO.findByInvigilatorEmailInSession(email, sessionId);

        if (duty.isPresent()) {
            InvigilatorDuty d = duty.get();
            Optional<Hall> hall = hallDAO.findById(d.getHallId());
            Optional<Invigilator> invigilator = invigilatorDAO.findById(d.getInvigilatorId());

            if (hall.isPresent() && invigilator.isPresent()) {
                Map<String, String> result = new HashMap<>();
                result.put("name", invigilator.get().getName());
                result.put("hall", hall.get().getName());
                result.put("capacity", String.valueOf(hall.get().getTotalCapacity()));
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }
}
