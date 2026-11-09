package com.hammaad.codecanvas.service;

import com.hammaad.codecanvas.model.EmailRules;
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

    /** Thrown when an email address already belongs to another account. */
    public static class EmailTakenException extends RuntimeException {
        public EmailTakenException() {
            super("Email already in use");
        }
    }

    /** Thrown when the current password given for a password change is wrong. */
    public static class WrongPasswordException extends RuntimeException {
        public WrongPasswordException() {
            super("Current password is wrong");
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
        String email = EmailRules.normalize(form.getEmail());
        if (studentRepository.existsByUsername(username)) {
            throw new UsernameTakenException();
        }
        if (studentRepository.existsByEmail(email)) {
            throw new EmailTakenException();
        }
        Student student = new Student(username, form.getDisplayName().trim(), email,
                passwordEncoder.encode(form.getPassword()));
        try {
            Student saved = studentRepository.saveAndFlush(student);
            log.info("student.registered id={} username={}", saved.getId(), username);
            return saved;
        } catch (DataIntegrityViolationException e) {
            // Two registrations for the same username/email at the same time: the unique constraint wins.
            if (studentRepository.existsByEmail(email)) {
                throw new EmailTakenException();
            }
            throw new UsernameTakenException();
        }
    }

    @Transactional(readOnly = true)
    public Student get(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow();
    }

    /**
     * Records a successful sign-in. Returns true if this was the student's first sign-in ever,
     * so the dashboard can say "Welcome" instead of "Welcome back".
     */
    @Transactional
    public boolean recordLogin(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        boolean firstLogin = student.getLastLoginAt() == null;
        student.setLastLoginAt(java.time.Instant.now());
        return firstLogin;
    }

    @Transactional
    public void updateEmail(Long studentId, String newEmail) {
        String email = EmailRules.normalize(newEmail);
        Student student = studentRepository.findById(studentId).orElseThrow();
        if (email.equals(student.getEmail())) {
            return;
        }
        if (studentRepository.existsByEmail(email)) {
            throw new EmailTakenException();
        }
        student.setEmail(email);
        try {
            studentRepository.saveAndFlush(student);
        } catch (DataIntegrityViolationException e) {
            throw new EmailTakenException();
        }
        log.info("student.email_changed id={}", studentId);
    }

    @Transactional
    public void changePassword(Long studentId, String currentPassword, String newPassword) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        if (!passwordEncoder.matches(currentPassword, student.getPasswordHash())) {
            throw new WrongPasswordException();
        }
        student.setPasswordHash(passwordEncoder.encode(newPassword));
        log.info("student.password_changed id={}", studentId);
    }
}
