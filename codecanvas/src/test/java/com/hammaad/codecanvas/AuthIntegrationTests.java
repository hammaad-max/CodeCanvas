package com.hammaad.codecanvas;

import com.hammaad.codecanvas.model.Student;
import com.hammaad.codecanvas.repository.StudentRepository;
import com.hammaad.codecanvas.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Protected pages redirect anonymous visitors to the login page")
    void pagesRequireLogin() throws Exception {
        for (String page : new String[]{"/dashboard", "/apcs", "/apcs/quiz", "/apcs/exam", "/progress", "/icse"}) {
            mockMvc.perform(get(page))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"));
        }
    }

    @Test
    @DisplayName("APIs return 401 (not a redirect) for anonymous callers")
    void apisRequireLogin() throws Exception {
        mockMvc.perform(get("/api/questions")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/progress")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/exam/apcs")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Login and register pages are public")
    void publicPages() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/register")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Registration stores a BCrypt hash, never the plain password")
    void registerHashesPassword() throws Exception {
        String username = TestStudents.uniqueUsername();
        mockMvc.perform(post("/register").with(csrf())
                        .param("displayName", "Ada Lovelace")
                        .param("username", username.toUpperCase())
                        .param("password", "s3cure-passw0rd")
                        .param("confirmPassword", "s3cure-passw0rd"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?registered"));

        Student saved = studentRepository.findByUsername(username).orElseThrow(); // stored lower-case
        assertEquals("Ada Lovelace", saved.getDisplayName());
        assertNotEquals("s3cure-passw0rd", saved.getPasswordHash());
        assertTrue(saved.getPasswordHash().startsWith("{bcrypt}$2"), "password must be BCrypt-hashed");
        assertTrue(passwordEncoder.matches("s3cure-passw0rd", saved.getPasswordHash()));
    }

    @Test
    @DisplayName("Registration rejects invalid input, mismatched passwords and duplicate usernames")
    void registerValidation() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                        .param("displayName", "")
                        .param("username", "a b")
                        .param("password", "short")
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "displayName", "username", "password"));

        mockMvc.perform(post("/register").with(csrf())
                        .param("displayName", "Sam")
                        .param("username", TestStudents.uniqueUsername())
                        .param("password", "long-enough-1")
                        .param("confirmPassword", "different-2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "confirmPassword"));

        String taken = TestStudents.uniqueUsername();
        TestStudents.register(studentService, taken);
        mockMvc.perform(post("/register").with(csrf())
                        .param("displayName", "Copycat")
                        .param("username", taken.toUpperCase())
                        .param("password", "long-enough-1")
                        .param("confirmPassword", "long-enough-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "username"));
        assertEquals(1, studentRepository.findAll().stream().filter(s -> s.getUsername().equals(taken)).count());
    }

    @Test
    @DisplayName("Registration without a CSRF token is rejected")
    void registerRequiresCsrf() throws Exception {
        mockMvc.perform(post("/register")
                        .param("displayName", "No Token")
                        .param("username", TestStudents.uniqueUsername())
                        .param("password", "long-enough-1")
                        .param("confirmPassword", "long-enough-1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Login succeeds with the right password (any username case) and fails with a wrong one")
    void loginFlow() throws Exception {
        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);

        mockMvc.perform(formLogin("/login").user(username.toUpperCase()).password(TestStudents.PASSWORD))
                .andExpect(authenticated().withUsername(username))
                .andExpect(redirectedUrl("/dashboard"));

        mockMvc.perform(formLogin("/login").user(username).password("wrong-password"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/?error"));

        mockMvc.perform(formLogin("/login").user("nobody-" + username).password("whatever"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/?error"));
    }

    @Test
    @DisplayName("Logout ends the session")
    void logoutFlow() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/?logout"));
    }

    @Test
    @DisplayName("Security headers are set")
    void securityHeaders() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(header().exists("Content-Security-Policy"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("X-Frame-Options", "DENY"));
    }
}
