package com.hammaad.codecanvas.controller;

import com.hammaad.codecanvas.model.PasswordForms;
import com.hammaad.codecanvas.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Public pages: "Forgot password?" request form and the reset form opened from the emailed link. */
@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final String baseUrl;

    public PasswordResetController(PasswordResetService passwordResetService,
                                   @Value("${tracetables.base-url:}") String baseUrl) {
        this.passwordResetService = passwordResetService;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    @GetMapping("/forgot-password")
    public String forgotForm(Model model) {
        model.addAttribute("form", new PasswordForms.Forgot());
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgot(@Valid @ModelAttribute("form") PasswordForms.Forgot form, BindingResult result) {
        if (result.hasErrors()) {
            return "forgot-password";
        }
        passwordResetService.requestReset(form.getIdentifier().trim(), resetPageUrl());
        // Same answer whether or not the account exists
        return "redirect:/forgot-password?sent";
    }

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam(required = false) String token, Model model) {
        PasswordForms.Reset form = new PasswordForms.Reset();
        form.setToken(token);
        model.addAttribute("form", form);
        model.addAttribute("validLink", passwordResetService.isValid(token));
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String reset(@Valid @ModelAttribute("form") PasswordForms.Reset form, BindingResult result, Model model) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }
        if (result.hasErrors()) {
            model.addAttribute("validLink", passwordResetService.isValid(form.getToken()));
            return "reset-password";
        }
        if (!passwordResetService.resetPassword(form.getToken(), form.getPassword())) {
            model.addAttribute("validLink", false);
            return "reset-password";
        }
        return "redirect:/?reset";
    }

    /** Uses APP_BASE_URL when set (recommended in production) instead of trusting the request's Host header. */
    private String resetPageUrl() {
        String base = baseUrl.isBlank()
                ? ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
                : baseUrl;
        return base + "/reset-password";
    }
}
