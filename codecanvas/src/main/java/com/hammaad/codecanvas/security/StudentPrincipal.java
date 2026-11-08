package com.hammaad.codecanvas.security;

import com.hammaad.codecanvas.model.Student;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * Authenticated student. Carries the database id so controllers never trust a client-supplied student id.
 */
public class StudentPrincipal extends User {

    private final Long studentId;
    private final String displayName;

    public StudentPrincipal(Student student) {
        super(student.getUsername(), student.getPasswordHash(), student.isEnabled(), true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_" + student.getRole())));
        this.studentId = student.getId();
        this.displayName = student.getDisplayName();
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
