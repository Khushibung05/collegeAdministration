import com.collegeadmin.config.DatabaseConfig;
import com.collegeadmin.dao.*;
import com.collegeadmin.model.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class SampleDataLoader {
    public static void main(String[] args) throws SQLException {
        DatabaseConfig.initialize();
        
        StudentDAO studentDAO = new StudentDAO();
        InvigilatorDAO invigilatorDAO = new InvigilatorDAO();
        HallDAO hallDAO = new HallDAO();
        ExamSessionDAO sessionDAO = new ExamSessionDAO();
        
        try {
            // Create students
            System.out.println("Adding students...");
            for (int i = 1; i <= 50; i++) {
                Student s = new Student("Student " + i, "STU" + String.format("%03d", i), 
                    "student" + i + "@example.com", "900000" + String.format("%04d", i));
                studentDAO.create(s);
            }
            
            // Create invigilators
            System.out.println("Adding invigilators...");
            for (int i = 1; i <= 5; i++) {
                Invigilator inv = new Invigilator("Invigilator " + i, 
                    "invig" + i + "@example.com", "980000" + String.format("%04d", i), "CSE");
                invigilatorDAO.create(inv);
            }
            
            // Create halls
            System.out.println("Adding halls...");
            hallDAO.create(new Hall("Hall A", 5, 10)); // 50 seats
            hallDAO.create(new Hall("Hall B", 5, 10)); // 50 seats
            
            // Create exam session
            System.out.println("Adding exam session...");
            ExamSession session = new ExamSession("Mid Semester - CSE", LocalDate.now().plusDays(7));
            sessionDAO.create(session);
            
            System.out.println("✅ Sample data loaded successfully!");
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}