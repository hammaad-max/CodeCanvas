package com.hammaad.codecanvas;

import com.hammaad.codecanvas.repository.StudentRepository;
import com.hammaad.codecanvas.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

/** The dashboard greets a student's first-ever sign-in differently from later ones. */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WelcomeGreetingTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private StudentService studentService;
    @Autowired private StudentRepository studentRepository;

    private MockHttpSession signIn(String username) throws Exception {
        return (MockHttpSession) mockMvc.perform(formLogin("/login").user(username).password(TestStudents.PASSWORD))
                .andExpect(redirectedUrl("/dashboard"))
                .andReturn().getRequest().getSession();
    }

    @Test
    @DisplayName("First sign-in is marked as first; the next sign-in is a returning one")
    void firstThenReturning() throws Exception {
        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);
        assertNull(studentRepository.findByUsername(username).orElseThrow().getLastLoginAt());

        MockHttpSession first = signIn(username);
        mockMvc.perform(get("/dashboard").session(first))
                .andExpect(content().string(containsString("data-first-login=\"true\"")));
        assertNotNull(studentRepository.findByUsername(username).orElseThrow().getLastLoginAt());

        // Still the same first session: the first-time greeting stays until they sign out
        mockMvc.perform(get("/dashboard").session(first))
                .andExpect(content().string(containsString("data-first-login=\"true\"")));

        MockHttpSession second = signIn(username);
        mockMvc.perform(get("/dashboard").session(second))
                .andExpect(content().string(containsString("data-first-login=\"false\"")));
    }
}
