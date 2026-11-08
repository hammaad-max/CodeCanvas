package com.hammaad.codecanvas.model;

import java.util.Locale;

/** Shared email validation and normalisation. */
public final class EmailRules {

    /** Simple, strict-enough shape check: one @, a dot in the domain, no spaces or angle brackets. */
    public static final String PATTERN = "^[^@\\s<>]+@[^@\\s<>]+\\.[^@\\s<>]+$";

    private EmailRules() {
    }

    public static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
