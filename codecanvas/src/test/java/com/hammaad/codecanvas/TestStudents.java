package com.hammaad.codecanvas;

import com.hammaad.codecanvas.model.RegistrationForm;
import com.hammaad.codecanvas.model.Student;
import com.hammaad.codecanvas.security.StudentPrincipal;
import com.hammaad.codecanvas.service.StudentService;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

/** Creates real registered students for integration tests. */
final class TestStudents {

    static final String PASSWORD = "correct-horse-42";

    private TestStudents() {
    }

    static String uniqueUsername() {
        return "s" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    static Student register(StudentService service, String username) {
        RegistrationForm form = new RegistrationForm();
        form.setDisplayName("Test Student");
        form.setUsername(username);
        form.setEmail(username + "@example.test");
        form.setPassword(PASSWORD);
        form.setConfirmPassword(PASSWORD);
        return service.register(form);
    }

    /** Request post-processor that authenticates as a freshly registered student. */
    static RequestPostProcessor newStudent(StudentService service) {
        return user(new StudentPrincipal(register(service, uniqueUsername())));
    }

    static RequestPostProcessor as(Student student) {
        return user(new StudentPrincipal(student));
    }
}
