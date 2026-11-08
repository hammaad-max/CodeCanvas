package com.hammaad.codecanvas.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptServiceTests {

    /** Clock that tests can move forward. */
    static final class TestClock extends Clock {
        Instant now = Instant.parse("2026-01-01T10:00:00Z");

        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
        void advance(Duration d) { now = now.plus(d); }
    }

    private final TestClock clock = new TestClock();
    private final LoginAttemptService service =
            new LoginAttemptService(5, Duration.ofMinutes(15), Duration.ofMinutes(15), clock);

    @Test
    @DisplayName("Lock expires after the lock duration")
    void lockExpires() {
        for (int i = 0; i < 5; i++) service.recordFailure("amy");
        assertTrue(service.isLocked("amy"));
        clock.advance(Duration.ofMinutes(14));
        assertTrue(service.isLocked("AMY"));
        clock.advance(Duration.ofMinutes(2));
        assertFalse(service.isLocked("amy"));
        // a fresh window starts: one more failure does not lock again
        service.recordFailure("amy");
        assertFalse(service.isLocked("amy"));
    }

    @Test
    @DisplayName("Failures spread beyond the window do not add up")
    void windowResets() {
        for (int i = 0; i < 4; i++) service.recordFailure("ben");
        clock.advance(Duration.ofMinutes(16));
        service.recordFailure("ben");
        assertFalse(service.isLocked("ben"));
    }

    @Test
    @DisplayName("Blank usernames are ignored")
    void blankIgnored() {
        for (int i = 0; i < 10; i++) service.recordFailure("  ");
        assertFalse(service.isLocked(""));
        assertFalse(service.isLocked(null));
    }
}
