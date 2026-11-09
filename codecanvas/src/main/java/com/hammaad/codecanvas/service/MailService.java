package com.hammaad.codecanvas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends account emails over SMTP (configured with SPRING_MAIL_HOST / _PORT / _USERNAME / _PASSWORD and MAIL_FROM).
 * <p>
 * Without SMTP configured, nothing is sent. Outside the prod profile the reset link is written to the log so
 * password reset can be tried locally; in prod the link is never logged.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String from;
    private final boolean prod;

    public MailService(ObjectProvider<JavaMailSender> mailSender,
                       @Value("${tracetables.mail.from:}") String from,
                       Environment environment) {
        this.mailSender = mailSender;
        this.from = from;
        this.prod = environment.acceptsProfiles(Profiles.of("prod"));
    }

    /** Returns true if the email was handed to the SMTP server. Never throws. */
    public boolean sendPasswordReset(String to, String displayName, String link, long validMinutes) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || from.isBlank()) {
            if (prod) {
                log.warn("mail.not_configured: password reset email NOT sent (set SPRING_MAIL_HOST and MAIL_FROM)");
            } else {
                log.warn("mail.not_configured (dev): password reset link for {} is {}", to, link);
            }
            return false;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Reset your TraceTables password");
        message.setText("""
                Hi %s,

                Someone (hopefully you) asked to reset the password for your TraceTables account.
                Open this link to choose a new password. It works once and expires in %d minutes:

                %s

                If you did not ask for this, you can ignore this email; your password will not change.

                - TraceTables
                """.formatted(displayName, validMinutes, link));
        try {
            sender.send(message);
            log.info("mail.sent type=password_reset");
            return true;
        } catch (MailException e) {
            log.error("mail.failed type=password_reset error={}", e.getClass().getSimpleName());
            return false;
        }
    }
}
