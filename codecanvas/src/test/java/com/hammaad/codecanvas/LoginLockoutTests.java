package com.hammaad.codecanvas;

import com.hammaad.codecanvas.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LoginLockoutTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentService studentService;

    @Test
    @DisplayName("5 wrong passwords lock the username: even the right password is refused")
    void lockAfterFiveFailures() throws Exception {
        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(formLogin("/login").user(username).password("wrong-password"))
                    .andExpect(redirectedUrl("/?error"))
                    .andExpect(unauthenticated());
        }
        // Username is matched case-insensitively
        mockMvc.perform(formLogin("/login").user(username.toUpperCase()).password(TestStudents.PASSWORD))
                .andExpect(redirectedUrl("/?locked"))
                .andExpect(unauthenticated());

        mockMvc.perform(get("/").param("locked", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Too many failed sign-in attempts")));
    }

    @Test
    @DisplayName("A successful sign-in resets the failure count")
    void successResetsCounter() throws Exception {
        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);

        for (int i = 0; i < 4; i++) {
            mockMvc.perform(formLogin("/login").user(username).password("wrong-password"));
        }
        mockMvc.perform(formLogin("/login").user(username).password(TestStudents.PASSWORD))
                .andExpect(authenticated());
        for (int i = 0; i < 4; i++) {
            mockMvc.perform(formLogin("/login").user(username).password("wrong-password"));
        }
        mockMvc.perform(formLogin("/login").user(username).password(TestStudents.PASSWORD))
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Locking one username does not affect other students")
    void lockIsPerUsername() throws Exception {
        String locked = TestStudents.uniqueUsername();
        String other = TestStudents.uniqueUsername();
        TestStudents.register(studentService, locked);
        TestStudents.register(studentService, other);
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(formLogin("/login").user(locked).password("wrong-password"));
        }
        mockMvc.perform(formLogin("/login").user(other).password(TestStudents.PASSWORD))
                .andExpect(authenticated());
    }
}
