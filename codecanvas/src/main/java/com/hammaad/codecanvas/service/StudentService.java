package com.hammaad.codecanvas.service;

import com.hammaad.codecanvas.model.RegistrationForm;
import com.hammaad.codecanvas.model.Student;
import com.hammaad.codecanvas.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class StudentService {

    /** Thrown when a username is already registered. */
    public static class UsernameTakenException extends RuntimeException {
        public UsernameTakenException() {
            super("Username already taken");
        }
    }

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public Student register(RegistrationForm form) {
        String username = normalizeUsername(form.getUsername());
        if (studentRepository.existsByUsername(username)) {
            throw new UsernameTakenException();
        }
        Student student = new Student(username, form.getDisplayName().trim(),
                passwordEncoder.encode(form.getPassword()));
        try {
            Student saved = studentRepository.saveAndFlush(student);
            log.info("student.registered id={} username={}", saved.getId(), username);
            return saved;
        } catch (DataIntegrityViolationException e) {
            // Two registrations for the same username at the same time: the unique constraint wins.
            throw new UsernameTakenException();
        }
    }
}
