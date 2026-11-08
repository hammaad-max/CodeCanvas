package com.hammaad.codecanvas;

import com.hammaad.codecanvas.model.Attempt;
import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.model.Student;
import com.hammaad.codecanvas.repository.AttemptRepository;
import com.hammaad.codecanvas.repository.QuestionRepository;
import com.hammaad.codecanvas.service.StudentService;
import com.jayway.jsonpath.JsonPath;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** ISC quizzes and mock exams are graded, stored under curriculum ISC, and reported separately. */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class IscProgressTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AttemptRepository attemptRepository;
    @Autowired private StudentService studentService;

    private static String answersJson(List<Question> questions, int correct) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String answer = i < correct ? q.getCorrectAnswer() : "__wrong__";
            items.add("{\"id\":" + q.getId() + ",\"answer\":\"" + answer.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
        }
        return "[" + String.join(",", items) + "]";
    }

    @Test
    @DisplayName("ISC quiz is stored as ISC and tagged with its curriculum in progress")
    void iscQuiz() throws Exception {
        Student student = TestStudents.register(studentService, TestStudents.uniqueUsername());
        List<Question> recursion = questionRepository.findByTopicAndCurriculum("Recursion", "ISC");
        assertEquals(10, recursion.size());

        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"Recursion\",\"curriculum\":\"isc\",\"answers\":" + answersJson(recursion, 8) + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(8))
                .andExpect(jsonPath("$.passed").value(true));

        List<Attempt> attempts = attemptRepository.findByStudentIdOrderByCreatedAtAsc(student.getId());
        assertEquals(1, attempts.size());
        assertEquals("ISC", attempts.getFirst().getCurriculum());

        mockMvc.perform(get("/api/progress").with(TestStudents.as(student)))
                .andExpect(jsonPath("$.completedTopics.Recursion.curriculum").value("ISC"))
                .andExpect(jsonPath("$.completedTopics.Recursion.bestScore").value(8))
                .andExpect(jsonPath("$.iscMockExam").doesNotExist());

        // An ISC topic cannot be submitted under another curriculum
        mockMvc.perform(post("/api/quiz/submit").with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topic\":\"Recursion\",\"curriculum\":\"ICSE\",\"answers\":" + answersJson(recursion, 8) + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ISC mock exam: 40 issued questions, graded once, reported as iscMockExam with a board grade")
    void iscExam() throws Exception {
        Student student = TestStudents.register(studentService, TestStudents.uniqueUsername());
        MockHttpSession session = new MockHttpSession();

        String examJson = mockMvc.perform(get("/api/exam/isc").session(session).with(TestStudents.as(student)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Integer> ids = JsonPath.read(examJson, "$[*].id");
        assertEquals(40, ids.size());
        List<Question> issued = ids.stream().map(id -> questionRepository.findById(id.longValue()).orElseThrow()).toList();
        assertTrue(issued.stream().allMatch(q -> "ISC".equals(q.getCurriculum())));

        mockMvc.perform(post("/api/exam/grade").session(session).with(TestStudents.as(student)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(answersJson(issued, 34)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/progress").with(TestStudents.as(student)))
                .andExpect(jsonPath("$.iscMockExam.score").value(34))
                .andExpect(jsonPath("$.iscMockExam.total").value(40))
                .andExpect(jsonPath("$.iscMockExam.percentage").value(85))
                .andExpect(jsonPath("$.iscMockExam.curriculum").value("ISC"))
                .andExpect(jsonPath("$.iscMockExam.projectedScore").value("90%+"))
                .andExpect(jsonPath("$.mockExam").doesNotExist())
                .andExpect(jsonPath("$.icseMockExam").doesNotExist());
    }
}
