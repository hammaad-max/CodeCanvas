package com.hammaad.codecanvas.service;

import com.hammaad.codecanvas.model.EmailRules;
import com.hammaad.codecanvas.model.PasswordResetToken;
import com.hammaad.codecanvas.model.Student;
import com.hammaad.codecanvas.repository.PasswordResetTokenRepository;
import com.hammaad.codecanvas.repository.StudentRepository;
import com.hammaad.codecanvas.security.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

/**
 * Forgot-password flow: one-time links that expire after 30 minutes. Only a SHA-256 hash of each token is stored,
 * so a database leak does not reveal usable links. At most 3 links per account per hour.
 */
@Service
public class PasswordResetService {

    public static final Duration VALIDITY = Duration.ofMinutes(30);
    static final int MAX_LINKS_PER_HOUR = 3;

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StudentRepository students;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final LoginAttemptService loginAttempts;
    private final Clock clock = Clock.systemUTC();

    public PasswordResetService(StudentRepository students, PasswordResetTokenRepository tokens,
                                PasswordEncoder passwordEncoder, MailService mailService,
                                LoginAttemptService loginAttempts) {
        this.students = students;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.loginAttempts = loginAttempts;
    }

    /**
     * Emails a reset link if the username or email matches an account that has an email address.
     * The caller always shows the same message, so this never reveals whether an account exists.
     *
     * @param resetPageUrl absolute URL of the reset page, e.g. https://looplab.example/reset-password
     */
    @Transactional
    public void requestReset(String identifier, String resetPageUrl) {
        Optional<Student> match = identifier != null && identifier.contains("@")
                ? students.findByEmail(EmailRules.normalize(identifier))
                : students.findByUsername(StudentService.normalizeUsername(identifier));
        if (match.isEmpty() || match.get().getEmail() == null || !match.get().isEnabled()) {
            log.info("password_reset.requested matched=false");
            return;
        }
        Student student = match.get();
        Instant now = clock.instant();
        if (tokens.countByStudentIdAndCreatedAtAfter(student.getId(), now.minus(Duration.ofHours(1))) >= MAX_LINKS_PER_HOUR) {
            log.warn("password_reset.throttled studentId={}", student.getId());
            return;
        }
        String token = newToken();
        tokens.save(new PasswordResetToken(student.getId(), hash(token), now, now.plus(VALIDITY)));
        log.info("password_reset.requested matched=true studentId={}", student.getId());
        mailService.sendPasswordReset(student.getEmail(), student.getDisplayName(),
                resetPageUrl + "?token=" + token, VALIDITY.toMinutes());
    }

    /** True if the token is a live, unused link. */
    @Transactional(readOnly = true)
    public boolean isValid(String token) {
        return find(token).isPresent();
    }

    /** Sets a new password if the token is valid; returns false otherwise. Every outstanding link is then void. */
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> found = find(token);
        if (found.isEmpty()) {
            return false;
        }
        Instant now = clock.instant();
        Student student = students.findById(found.get().getStudentId()).orElse(null);
        if (student == null) {
            return false;
        }
        student.setPasswordHash(passwordEncoder.encode(newPassword));
        tokens.invalidateAll(student.getId(), now);
        loginAttempts.recordSuccess(student.getUsername()); // a reset also lifts any sign-in lock
        log.info("password_reset.completed studentId={}", student.getId());
        return true;
    }

    private Optional<PasswordResetToken> find(String token) {
        if (token == null || token.length() < 20 || token.length() > 100) {
            return Optional.empty();
        }
        Instant now = clock.instant();
        return tokens.findByTokenHash(hash(token)).filter(t -> t.isUsable(now));
    }

    private static String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
