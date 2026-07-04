package collegeadmin;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CollegeAdminServiceTest {

    @Test
    void testRegisterStudent() {
        CollegeAdminService service = new CollegeAdminService();
        Student s = new Student(1, "Asha");

        boolean registered = service.registerStudent(s);

        assertTrue(registered);
        assertEquals("Asha", service.getStudentById(1).getName());
    }

    @Test
    void testDuplicateStudent() {
        CollegeAdminService service = new CollegeAdminService();
        Student s1 = new Student(1, "Asha");
        Student s2 = new Student(1, "Nina");

        service.registerStudent(s1);
        boolean second = service.registerStudent(s2);

        assertFalse(second);  // duplicate ID
    }
}
