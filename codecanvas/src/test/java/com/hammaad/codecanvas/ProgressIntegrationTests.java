package com.hammaad.codecanvas;

import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.model.Student;
import com.hammaad.codecanvas.repository.AttemptRepository;
import com.hammaad.codecanvas.repository.QuestionRepository;
import com.hammaad.codecanvas.service.StudentService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ProgressIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AttemptRepository attemptRepository;

    @Autowired
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = TestStudents.register(studentService, TestStudents.uniqueUsername());
    }

    /** JSON answers for the given questions: the first {@code correct} answered right, the rest wrong. */
    private static String answersJson(List<Question> questions, int correct) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String answer = i < correct ? q.getCorrectAnswer() : "__wrong__";
            items.add("{\"id\":" + q.getId() + ",\"answer\":\"" + answer.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
        }
        return "[" + String.join(",", items) + "]";
    }

    private String submitQuiz(String topic, String curriculum, String answersJson) {
        return "{\"topic\":\"" + topic + "\",\"curriculum\":\"" + curriculum + "\",\"answers\":" + answersJson + "}";
    }

    @Test
    @DisplayName("Quiz is graded on the server and stored; progress reflects best score and pass state")
    void quizPersistsProgress() throws Exception {
        List<Question> arrays = questionRepository.findByTopicAndCurriculum("Arrays", "AP CS");

        // 6/10 = fail
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Arrays", "APCS", answersJson(arrays, 6))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(6))
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.passed").value(false));

        mockMvc.perform(get("/api/progress").with(TestStudents.as(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Test Student"))
                .andExpect(jsonPath("$.completedTopics.Arrays.bestScore").value(6))
                .andExpect(jsonPath("$.completedTopics.Arrays.passed").value(false))
                .andExpect(jsonPath("$.quizzesCompleted").value(0));

        // 9/10 = pass; then 3/10 keeps best score and passed state
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Arrays", "APCS", answersJson(arrays, 9))))
                .andExpect(jsonPath("$.passed").value(true));
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Arrays", "APCS", answersJson(arrays, 3))))
                .andExpect(jsonPath("$.score").value(3));

        mockMvc.perform(get("/api/progress").with(TestStudents.as(student)))
                .andExpect(jsonPath("$.completedTopics.Arrays.lastScore").value(3))
                .andExpect(jsonPath("$.completedTopics.Arrays.bestScore").value(9))
                .andExpect(jsonPath("$.completedTopics.Arrays.passed").value(true))
                .andExpect(jsonPath("$.quizzesCompleted").value(1));
    }

    @Test
    @DisplayName("Quiz submissions with foreign, duplicate or unknown questions are rejected")
    void quizRejectsTampering() throws Exception {
        List<Question> arrays = questionRepository.findByTopicAndCurriculum("Arrays", "AP CS");
        List<Question> strings = questionRepository.findByTopicAndCurriculum("Strings", "AP CS");

        // question from another topic
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Arrays", "APCS", answersJson(List.of(arrays.get(0), strings.get(0)), 2))))
                .andExpect(status().isBadRequest());

        // duplicate question
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Arrays", "APCS", answersJson(List.of(arrays.get(0), arrays.get(0)), 2))))
                .andExpect(status().isBadRequest());

        // unknown topic / empty answers
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Hacking 101", "APCS", answersJson(arrays, 10))))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Arrays", "APCS", "[]")))
                .andExpect(status().isBadRequest());

        // no CSRF token
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Arrays", "APCS", answersJson(arrays, 10))))
                .andExpect(status().isForbidden());

        assertEquals(0, attemptRepository.findByStudentIdOrderByCreatedAtAsc(student.getId()).size());
    }

    @Test
    @DisplayName("Mock exam: only the issued questions can be graded, once, and the result is stored")
    void examFlow() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Grading before an exam was issued
        mockMvc.perform(post("/api/exam/grade").session(session).with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("[{\"id\":1,\"answer\":\"int\"}]"))
                .andExpect(status().isConflict());

        String examJson = mockMvc.perform(get("/api/exam/apcs").session(session).with(TestStudents.as(student)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Integer> ids = JsonPath.read(examJson, "$[*].id");
        assertEquals(40, ids.size());
        List<Question> issued = ids.stream().map(id -> questionRepository.findById(id.longValue()).orElseThrow()).toList();

        // Swapping in a question that was not issued is rejected
        List<Question> tampered = new ArrayList<>(issued.subList(0, 39));
        Question outsider = questionRepository.findByCurriculum("AP CS").stream()
                .filter(q -> !ids.contains(q.getId().intValue())).findFirst().orElseThrow();
        tampered.add(outsider);
        mockMvc.perform(post("/api/exam/grade").session(session).with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(answersJson(tampered, 40)))
                .andExpect(status().isBadRequest());

        // Honest submission: 30 correct of 40
        mockMvc.perform(post("/api/exam/grade").session(session).with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(answersJson(issued, 30)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(40)));

        // Same exam cannot be graded twice
        mockMvc.perform(post("/api/exam/grade").session(session).with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(answersJson(issued, 40)))
                .andExpect(status().isConflict());

        mockMvc.perform(get("/api/progress").with(TestStudents.as(student)))
                .andExpect(jsonPath("$.mockExam.score").value(30))
                .andExpect(jsonPath("$.mockExam.total").value(40))
                .andExpect(jsonPath("$.mockExam.percentage").value(75))
                .andExpect(jsonPath("$.mockExam.projectedScore").value(4))
                .andExpect(jsonPath("$.icseMockExam").doesNotExist());
    }

    @Test
    @DisplayName("Students only ever see their own progress, and reset clears only their own")
    void progressIsolationAndReset() throws Exception {
        Student other = TestStudents.register(studentService, TestStudents.uniqueUsername());
        List<Question> strings = questionRepository.findByTopicAndCurriculum("Strings", "AP CS");

        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Strings", "APCS", answersJson(strings, 10))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(other)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Strings", "APCS", answersJson(strings, 2))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/progress").with(TestStudents.as(other)))
                .andExpect(jsonPath("$.completedTopics.Strings.bestScore").value(2));

        mockMvc.perform(delete("/api/progress").with(TestStudents.as(student)).with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/progress").with(TestStudents.as(student)))
                .andExpect(jsonPath("$.completedTopics.Strings").doesNotExist());
        mockMvc.perform(get("/api/progress").with(TestStudents.as(other)))
                .andExpect(jsonPath("$.completedTopics.Strings.bestScore").value(2));
    }

    @Test
    @DisplayName("ICSE quiz and exam are stored under the ICSE curriculum")
    void icseProgress() throws Exception {
        List<Question> unit = questionRepository.findByTopicAndCurriculum("Classic Number Programs", "ICSE");
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Classic Number Programs", "ICSE", answersJson(unit, 7))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(true));

        // ICSE topic submitted under the wrong curriculum is rejected
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(submitQuiz("Classic Number Programs", "APCS", answersJson(unit, 6))))
                .andExpect(status().isBadRequest());

        MockHttpSession session = new MockHttpSession();
        String examJson = mockMvc.perform(get("/api/exam/icse").session(session).with(TestStudents.as(student)))
                .andReturn().getResponse().getContentAsString();
        List<Integer> ids = JsonPath.read(examJson, "$[*].id");
        List<Question> issued = ids.stream().map(id -> questionRepository.findById(id.longValue()).orElseThrow()).toList();
        mockMvc.perform(post("/api/exam/grade").session(session).with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(answersJson(issued, 37)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/progress").with(TestStudents.as(student)))
                .andExpect(jsonPath("$.icseMockExam.score").value(37))
                .andExpect(jsonPath("$.icseMockExam.projectedScore").value("100/100"))
                .andExpect(jsonPath("$.mockExam").doesNotExist());
    }
}
