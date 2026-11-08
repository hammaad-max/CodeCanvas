package com.hammaad.codecanvas.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Audit log for sign-ins. Logs the username and outcome only — never passwords.
 */
@Component
public class SecurityEventLogger {

    private static final Logger log = LoggerFactory.getLogger("codecanvas.security");

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        log.info("login.success username={}", safe(event.getAuthentication().getName()));
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        log.warn("login.failure username={} reason={}",
                safe(event.getAuthentication().getName()),
                event.getException().getClass().getSimpleName());
    }

    /** Strip line breaks and limit length so user input cannot forge log lines. */
    static String safe(String value) {
        if (value == null) {
            return "";
        }
        String cleaned = value.replaceAll("[\\r\\n\\t]", "_");
        return cleaned.length() > 60 ? cleaned.substring(0, 60) + "…" : cleaned;
    }
}
