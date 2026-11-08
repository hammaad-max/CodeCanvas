package com.hammaad.codecanvas;

import com.hammaad.codecanvas.model.Student;
import com.hammaad.codecanvas.repository.PasswordResetTokenRepository;
import com.hammaad.codecanvas.repository.StudentRepository;
import com.hammaad.codecanvas.service.MailService;
import com.hammaad.codecanvas.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PasswordResetTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private StudentService studentService;
    @Autowired private StudentRepository studentRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @MockitoBean private MailService mailService;

    @BeforeEach
    void mailSucceeds() {
        when(mailService.sendPasswordReset(anyString(), anyString(), anyString(), anyLong())).thenReturn(true);
    }

    /** Requests a reset and returns the emailed link's token. */
    private String requestToken(String identifier, String expectedEmail) throws Exception {
        mockMvc.perform(post("/forgot-password").with(csrf()).param("identifier", identifier))
                .andExpect(redirectedUrl("/forgot-password?sent"));
        ArgumentCaptor<String> link = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendPasswordReset(eq(expectedEmail), anyString(), link.capture(), eq(30L));
        assertTrue(link.getValue().contains("/reset-password?token="), link.getValue());
        return link.getValue().substring(link.getValue().indexOf("token=") + 6);
    }

    @Test
    @DisplayName("Full reset: emailed link sets a new password once; old password and reused link stop working")
    void fullResetFlow() throws Exception {
        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);
        String token = requestToken(username.toUpperCase(), username + "@example.test");

        // Only the hash is stored
        assertTrue(tokenRepository.findAll().stream().noneMatch(t -> t.getTokenHash().equals(token)));

        mockMvc.perform(get("/reset-password").param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Save new password")));

        mockMvc.perform(post("/reset-password").with(csrf())
                        .param("token", token).param("password", "brand-new-pass-9").param("confirmPassword", "brand-new-pass-9"))
                .andExpect(redirectedUrl("/?reset"));

        mockMvc.perform(formLogin("/login").user(username).password(TestStudents.PASSWORD)).andExpect(unauthenticated());
        mockMvc.perform(formLogin("/login").user(username).password("brand-new-pass-9")).andExpect(authenticated());

        // Link is single-use
        mockMvc.perform(get("/reset-password").param("token", token))
                .andExpect(content().string(containsString("invalid, already used or has expired")));
        mockMvc.perform(post("/reset-password").with(csrf())
                        .param("token", token).param("password", "another-pass-77").param("confirmPassword", "another-pass-77"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("invalid, already used or has expired")));
        Student s = studentRepository.findByUsername(username).orElseThrow();
        assertTrue(passwordEncoder.matches("brand-new-pass-9", s.getPasswordHash()));
    }

    @Test
    @DisplayName("Reset can be requested by email address (any case)")
    void requestByEmail() throws Exception {
        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);
        String token = requestToken((username + "@EXAMPLE.test"), username + "@example.test");
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Unknown accounts get the same answer and no email is sent")
    void unknownAccount() throws Exception {
        mockMvc.perform(post("/forgot-password").with(csrf()).param("identifier", "nobody-here-" + TestStudents.uniqueUsername()))
                .andExpect(redirectedUrl("/forgot-password?sent"));
        mockMvc.perform(get("/forgot-password").param("sent", ""))
                .andExpect(content().string(containsString("If an account with that username or email exists")));
        verify(mailService, never()).sendPasswordReset(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("At most 3 reset emails per account per hour")
    void throttled() throws Exception {
        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/forgot-password").with(csrf()).param("identifier", username))
                    .andExpect(redirectedUrl("/forgot-password?sent"));
        }
        verify(mailService, times(3)).sendPasswordReset(anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    @DisplayName("Garbage tokens, mismatched passwords and missing CSRF are rejected")
    void badInput() throws Exception {
        mockMvc.perform(get("/reset-password").param("token", "not-a-real-token-at-all-123456"))
                .andExpect(content().string(containsString("invalid, already used or has expired")))
                .andExpect(content().string(not(containsString("Save new password"))));
        mockMvc.perform(get("/reset-password"))
                .andExpect(content().string(containsString("invalid, already used or has expired")));

        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);
        String token = requestToken(username, username + "@example.test");
        mockMvc.perform(post("/reset-password").with(csrf())
                        .param("token", token).param("password", "brand-new-pass-9").param("confirmPassword", "different-pass-9"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "confirmPassword"));
        mockMvc.perform(post("/reset-password")
                        .param("token", token).param("password", "brand-new-pass-9").param("confirmPassword", "brand-new-pass-9"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/forgot-password").param("identifier", username)).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Account page: change email and password; wrong current password is refused")
    void accountSettings() throws Exception {
        String username = TestStudents.uniqueUsername();
        Student student = TestStudents.register(studentService, username);

        mockMvc.perform(get("/account").with(TestStudents.as(student)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(username)));

        mockMvc.perform(post("/account/email").with(csrf()).with(TestStudents.as(student)).param("email", "New." + username + "@Mail.test"))
                .andExpect(redirectedUrl("/account?emailSaved"));
        assertEquals("new." + username + "@mail.test", studentRepository.findById(student.getId()).orElseThrow().getEmail());

        mockMvc.perform(post("/account/email").with(csrf()).with(TestStudents.as(student)).param("email", "nope"))
                .andExpect(model().attributeHasFieldErrors("emailForm", "email"));

        mockMvc.perform(post("/account/password").with(csrf()).with(TestStudents.as(student))
                        .param("currentPassword", "wrong-one").param("password", "changed-pass-1").param("confirmPassword", "changed-pass-1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("passwordForm", "currentPassword"));

        mockMvc.perform(post("/account/password").with(csrf()).with(TestStudents.as(student))
                        .param("currentPassword", TestStudents.PASSWORD).param("password", "changed-pass-1").param("confirmPassword", "changed-pass-1"))
                .andExpect(redirectedUrl("/account?passwordSaved"));
        mockMvc.perform(formLogin("/login").user(username).password("changed-pass-1")).andExpect(authenticated());
    }

    @Test
    @DisplayName("Older accounts without email see an 'Add email' reminder on the dashboard and get no reset email")
    void accountWithoutEmail() throws Exception {
        String username = TestStudents.uniqueUsername();
        Student student = studentRepository.saveAndFlush(
                new Student(username, "Old Account", passwordEncoder.encode(TestStudents.PASSWORD)));

        mockMvc.perform(get("/dashboard").with(TestStudents.as(student)))
                .andExpect(content().string(containsString("Add your email address")));
        mockMvc.perform(post("/forgot-password").with(csrf()).param("identifier", username))
                .andExpect(redirectedUrl("/forgot-password?sent"));
        verify(mailService, never()).sendPasswordReset(anyString(), anyString(), anyString(), anyLong());

        Student withEmail = TestStudents.register(studentService, TestStudents.uniqueUsername());
        mockMvc.perform(get("/dashboard").with(TestStudents.as(withEmail)))
                .andExpect(content().string(not(containsString("Add your email address"))));
    }
}
