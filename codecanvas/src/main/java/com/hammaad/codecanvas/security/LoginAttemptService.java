package com.hammaad.codecanvas.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Brute-force protection for sign-in. After {@code maxFailures} wrong passwords for the same username within
 * {@code window}, that username is locked for {@code lockDuration}. A successful sign-in clears the counter.
 * <p>
 * Counters are kept in memory (one app instance). They reset on restart, which is acceptable for this purpose.
 */
@Service
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger("codecanvas.security");
    private static final int MAX_TRACKED = 10_000;

    private final int maxFailures;
    private final Duration window;
    private final Duration lockDuration;
    private final Clock clock;
    private final Map<String, Entry> entries = new ConcurrentHashMap<>();

    @Autowired
    public LoginAttemptService(@Value("${tracetables.login.max-failures:5}") int maxFailures,
                               @Value("${tracetables.login.window:15m}") Duration window,
                               @Value("${tracetables.login.lock-duration:15m}") Duration lockDuration) {
        this(maxFailures, window, lockDuration, Clock.systemUTC());
    }

    LoginAttemptService(int maxFailures, Duration window, Duration lockDuration, Clock clock) {
        this.maxFailures = maxFailures;
        this.window = window;
        this.lockDuration = lockDuration;
        this.clock = clock;
    }

    /** True if this username is currently locked out. */
    public boolean isLocked(String username) {
        Entry e = entries.get(key(username));
        return e != null && e.lockedUntil != null && clock.instant().isBefore(e.lockedUntil);
    }

    public void recordFailure(String username) {
        String key = key(username);
        if (key.isEmpty()) {
            return;
        }
        Instant now = clock.instant();
        if (entries.size() > MAX_TRACKED) {
            prune(now);
        }
        boolean[] lockedNow = {false};
        entries.compute(key, (k, old) -> {
            Entry n;
            if (old == null) {
                n = new Entry(now);
            } else if (old.locked(now)) {
                return old; // already locked: the lock is not extended
            } else if (old.lockedUntil != null || now.isAfter(old.windowStart.plus(window))) {
                n = new Entry(now); // previous lock or counting window has expired
            } else {
                n = old;
            }
            n.failures++;
            if (n.failures >= maxFailures) {
                n.lockedUntil = now.plus(lockDuration);
                lockedNow[0] = true;
            }
            return n;
        });
        if (lockedNow[0]) {
            log.warn("login.locked username={} minutes={}", SecurityEventLogger.safe(key), lockDuration.toMinutes());
        }
    }

    public void recordSuccess(String username) {
        entries.remove(key(username));
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        // Only wrong passwords / unknown usernames count (not e.g. disabled accounts)
        if (event.getException() instanceof BadCredentialsException) {
            recordFailure(event.getAuthentication().getName());
        }
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        recordSuccess(event.getAuthentication().getName());
    }

    private void prune(Instant now) {
        entries.entrySet().removeIf(en -> !en.getValue().locked(now)
                && now.isAfter(en.getValue().windowStart.plus(window)));
    }

    private static String key(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    private static final class Entry {
        final Instant windowStart;
        int failures;
        Instant lockedUntil;

        Entry(Instant windowStart) {
            this.windowStart = windowStart;
        }

        boolean locked(Instant now) {
            return lockedUntil != null && now.isBefore(lockedUntil);
        }
    }
}
