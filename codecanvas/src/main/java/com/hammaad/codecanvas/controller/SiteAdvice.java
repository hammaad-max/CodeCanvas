package com.hammaad.codecanvas.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Site-wide values for every page: the public contact address (only when CONTACT_EMAIL is set, so a personal
 * address is never published by accident) and the public base address used in link previews.
 */
@ControllerAdvice(annotations = Controller.class)
public class SiteAdvice {

    private final String contactEmail;
    private final String baseUrl;

    public SiteAdvice(@Value("${tracetables.contact-email:}") String contactEmail,
                      @Value("${tracetables.base-url:}") String baseUrl) {
        this.contactEmail = contactEmail == null ? "" : contactEmail.trim();
        this.baseUrl = baseUrl == null ? "" : baseUrl.trim().replaceAll("/+$", "");
    }

    /** Null when no contact address is configured, so templates can simply test for it. */
    @ModelAttribute("contactEmail")
    public String contactEmail() {
        return contactEmail.isEmpty() ? null : contactEmail;
    }

    @ModelAttribute("siteUrl")
    public String siteUrl() {
        return baseUrl.isEmpty() ? ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() : baseUrl;
    }
}
