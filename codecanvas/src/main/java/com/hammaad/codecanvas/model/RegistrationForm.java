package com.hammaad.codecanvas.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Registration form, validated on the server.
 */
public class RegistrationForm {

    @NotBlank(message = "Please enter your name.")
    @Size(max = 50, message = "Name must be at most 50 characters.")
    @Pattern(regexp = "^[^\\p{Cntrl}<>]*$", message = "Name contains characters that are not allowed.")
    private String displayName;

    @NotBlank(message = "Please choose a username.")
    @Pattern(regexp = "^[A-Za-z0-9_.-]{3,30}$",
            message = "Username must be 3-30 characters: letters, numbers, dot, dash or underscore.")
    private String username;

    @NotBlank(message = "Please enter your email address.")
    @Size(max = 254, message = "Email must be at most 254 characters.")
    @Email(regexp = EmailRules.PATTERN, message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Please choose a password.")
    @Size(min = 8, max = 72, message = "Password must be 8-72 characters.")
    private String password;

    @NotBlank(message = "Please confirm your password.")
    private String confirmPassword;

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim(); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
