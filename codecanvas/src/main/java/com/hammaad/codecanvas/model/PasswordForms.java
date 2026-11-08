package com.hammaad.codecanvas.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Form objects for forgot / reset / change password and changing email. All validated on the server. */
public final class PasswordForms {

    private PasswordForms() {
    }

    public static class Forgot {
        @NotBlank(message = "Please enter your username or email.")
        @Size(max = 254, message = "That is too long.")
        private String identifier;

        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }
    }

    public static class Reset {
        private String token;

        @NotBlank(message = "Please choose a password.")
        @Size(min = 8, max = 72, message = "Password must be 8-72 characters.")
        private String password;

        @NotBlank(message = "Please confirm your password.")
        private String confirmPassword;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    public static class Change {
        @NotBlank(message = "Please enter your current password.")
        private String currentPassword;

        @NotBlank(message = "Please choose a new password.")
        @Size(min = 8, max = 72, message = "Password must be 8-72 characters.")
        private String password;

        @NotBlank(message = "Please confirm your new password.")
        private String confirmPassword;

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    public static class EmailChange {
        @NotBlank(message = "Please enter your email address.")
        @Size(max = 254, message = "Email must be at most 254 characters.")
        @Email(regexp = EmailRules.PATTERN, message = "Please enter a valid email address.")
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email == null ? null : email.trim(); }
    }
}
