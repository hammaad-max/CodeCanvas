package com.hammaad.codecanvas;

import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("All 16 pages render")
    void pagesRender() throws Exception {
        String[] pages = {"/", "/dashboard", "/apcs", "/apcs/variables", "/apcs/expressions", "/apcs/strings",
                "/apcs/selection", "/apcs/iteration", "/apcs/methods", "/apcs/classes", "/apcs/arrays",
                "/apcs/quiz", "/apcs/exam", "/progress", "/icse", "/icse/learn", "/icse/exam"};
        for (String page : pages) {
            mockMvc.perform(get(page)).andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("Question list does not expose answers or explanations")
    void questionsHideAnswers() throws Exception {
        mockMvc.perform(get("/api/questions").param("topic", "Arrays").param("curriculum", "AP CS"))
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
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(40)))
                    .andExpect(jsonPath("$[*].correctAnswer").isEmpty());
        }
    }

    @Test
    @DisplayName("Single answer check grades correctly")
    void checkAnswer() throws Exception {
        Question q = questionRepository.findById(1L).orElseThrow();

        mockMvc.perform(post("/api/questions/1/check").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"" + q.getCorrectAnswer() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.correctAnswer").value(q.getCorrectAnswer()))
                .andExpect(jsonPath("$.explanation").value(q.getExplanation()));

        mockMvc.perform(post("/api/questions/1/check").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"definitely wrong\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(false));

        mockMvc.perform(post("/api/questions/99999/check").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"x\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Exam grading handles correct, wrong and unanswered questions")
    void gradeExam() throws Exception {
        Question q1 = questionRepository.findById(1L).orElseThrow();
        String body = "[{\"id\":1,\"answer\":\"" + q1.getCorrectAnswer() + "\"},"
                + "{\"id\":2,\"answer\":\"nope\"},"
                + "{\"id\":3,\"answer\":null}]";

        mockMvc.perform(post("/api/exam/grade").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].correct").value(true))
                .andExpect(jsonPath("$[1].correct").value(false))
                .andExpect(jsonPath("$[2].correct").value(false))
                .andExpect(jsonPath("$[2].correctAnswer").exists());
    }
}
