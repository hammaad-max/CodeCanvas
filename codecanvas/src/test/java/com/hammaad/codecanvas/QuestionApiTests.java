package com.hammaad.codecanvas;

import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.repository.QuestionRepository;
import com.hammaad.codecanvas.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class QuestionApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentService studentService;

    private RequestPostProcessor student;

    @BeforeEach
    void signIn() {
        student = TestStudents.newStudent(studentService);
    }

    @Test
    @DisplayName("All 16 pages render for a signed-in student")
    void pagesRender() throws Exception {
        String[] pages = {"/dashboard", "/apcs", "/apcs/variables", "/apcs/expressions", "/apcs/strings",
                "/apcs/selection", "/apcs/iteration", "/apcs/methods", "/apcs/classes", "/apcs/arrays",
                "/apcs/quiz", "/apcs/exam", "/progress", "/icse", "/icse/learn", "/icse/exam"};
        for (String page : pages) {
            mockMvc.perform(get(page).with(student)).andExpect(status().isOk());
        }
        // Login page (16th template) is public
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Question list does not expose answers or explanations")
    void questionsHideAnswers() throws Exception {
        mockMvc.perform(get("/api/questions").param("topic", "Arrays").param("curriculum", "AP CS").with(student))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].questionText").exists())
                .andExpect(jsonPath("$[0].optionA").exists())
                .andExpect(jsonPath("$[0].correctAnswer").doesNotExist())
                .andExpect(jsonPath("$[0].explanation").doesNotExist());
    }

    @Test
    @DisplayName("Mock exams return 40 answer-free questions")
    void examsHideAnswers() throws Exception {
        for (String path : new String[]{"/api/exam/apcs", "/api/exam/icse"}) {
            mockMvc.perform(get(path).with(student))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(40)))
                    .andExpect(jsonPath("$[*].correctAnswer").isEmpty());
        }
    }

    @Test
    @DisplayName("Single answer check grades correctly")
    void checkAnswer() throws Exception {
        Question q = questionRepository.findById(1L).orElseThrow();

        mockMvc.perform(post("/api/questions/1/check").with(student).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"" + q.getCorrectAnswer() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.correctAnswer").value(q.getCorrectAnswer()))
                .andExpect(jsonPath("$.explanation").value(q.getExplanation()));

        mockMvc.perform(post("/api/questions/1/check").with(student).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"definitely wrong\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(false));

        mockMvc.perform(post("/api/questions/99999/check").with(student).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"x\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Answer check without a CSRF token is rejected")
    void checkRequiresCsrf() throws Exception {
        mockMvc.perform(post("/api/questions/1/check").with(student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"int\"}"))
                .andExpect(status().isForbidden());
    }
}
