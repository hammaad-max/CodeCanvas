package com.hammaad.codecanvas;

import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CodeCanvasApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void contextLoads() {
        assertNotNull(questionRepository, "QuestionRepository should be injected into application context");
    }

    @Test
    @DisplayName("Verify that all questions have valid options and exact correct answers")
    void testAllQuestionsIntegrity() {
        List<Question> all = questionRepository.findAll();
        assertFalse(all.isEmpty(), "Question bank must not be empty");
        assertEquals(240, all.size(), "Should contain exactly 240 total questions (80 AP CS + 80 ICSE + 80 ISC)");

        Set<Long> ids = new HashSet<>();

        for (Question q : all) {
            assertNotNull(q.getId(), "Question ID must not be null");
            assertTrue(ids.add(q.getId()), "Question ID must be unique: " + q.getId());

            assertNotNull(q.getQuestionText(), "Question text must not be null for ID " + q.getId());
            assertFalse(q.getQuestionText().isBlank(), "Question text must not be blank for ID " + q.getId());

            assertNotNull(q.getOptionA(), "Option A must not be null for ID " + q.getId());
            assertNotNull(q.getOptionB(), "Option B must not be null for ID " + q.getId());
            assertNotNull(q.getOptionC(), "Option C must not be null for ID " + q.getId());
            assertNotNull(q.getOptionD(), "Option D must not be null for ID " + q.getId());

            assertNotNull(q.getCorrectAnswer(), "Correct answer must not be null for ID " + q.getId());
            assertNotNull(q.getExplanation(), "Explanation must not be null for ID " + q.getId());
            assertFalse(q.getExplanation().isBlank(), "Explanation must not be blank for ID " + q.getId());

            // CRITICAL CHECK: Correct answer must match one of the 4 options exactly!
            boolean matchesOption = q.getCorrectAnswer().equals(q.getOptionA())
                    || q.getCorrectAnswer().equals(q.getOptionB())
                    || q.getCorrectAnswer().equals(q.getOptionC())
                    || q.getCorrectAnswer().equals(q.getOptionD());

            assertTrue(matchesOption,
                    String.format("Question ID %d correct answer '%s' does not match any of its 4 options!",
                            q.getId(), q.getCorrectAnswer()));

            // The 4 options must be different, otherwise two choices would both be "correct"
            assertEquals(4, Set.of(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()).size(),
                    "Question ID " + q.getId() + " has duplicate options");
        }
    }

    @Test
    @DisplayName("Verify ICSE curriculum question bank covers all 8 ICSE units")
    void testIcseQuestionsCoverage() {
        List<Question> icseQuestions = questionRepository.findByCurriculum("ICSE");
        assertEquals(80, icseQuestions.size(), "ICSE must have 80 questions");

        String[] icseUnits = {
                "OOP & Java Fundamentals",
                "Operators & Expressions",
                "Conditional Branching & Switch",
                "Iterative Constructs & Loops",
                "Classic Number Programs",
                "Character & String Handling",
                "User-Defined Methods & Overloading",
                "Arrays, Searching & Sorting"
        };

        for (String unit : icseUnits) {
            List<Question> unitQuestions = icseQuestions.stream()
                    .filter(q -> q.getTopic().equalsIgnoreCase(unit))
                    .toList();
            assertEquals(10, unitQuestions.size(), "ICSE Unit '" + unit + "' must have exactly 10 questions");
        }
    }

    @Test
    @DisplayName("Verify AP CS curriculum question bank covers all 8 AP CS units")
    void testApcsQuestionsCoverage() {
        List<Question> apcsQuestions = questionRepository.findByCurriculum("AP CS");
        assertEquals(80, apcsQuestions.size(), "AP CS must have 80 questions");

        String[] apcsUnits = {
                "Variables & Data Types",
                "Expressions & Assignment",
                "Strings",
                "Selection",
                "Iteration",
                "Methods",
                "Classes & Objects",
                "Arrays"
        };

        for (String unit : apcsUnits) {
            List<Question> unitQuestions = apcsQuestions.stream()
                    .filter(q -> q.getTopic().equalsIgnoreCase(unit))
                    .toList();
            assertEquals(10, unitQuestions.size(), "AP CS Unit '" + unit + "' must have exactly 10 questions");
        }
    }

    @Test
    @DisplayName("Verify mock exam generation creates 40 questions covering all 8 topics")
    void testMockExamGeneration() {
        List<Question> mockExam = questionRepository.generateMockExam(5);
        assertEquals(40, mockExam.size(), "Mock exam must generate exactly 40 questions");

        Set<String> topicsCovered = new HashSet<>();
        for (Question q : mockExam) {
            topicsCovered.add(q.getTopic());
        }
        assertEquals(8, topicsCovered.size(), "Mock exam must cover all 8 topics");
    }

    @Test
    @DisplayName("Verify dual topic and curriculum filtering")
    void testTopicAndCurriculumFilter() {
        List<Question> result = questionRepository.findByTopicAndCurriculum("Arrays", "AP CS");
        assertEquals(10, result.size(), "Should find 10 AP CS Arrays questions");

        List<Question> wrongCombo = questionRepository.findByTopicAndCurriculum("Arrays", "ICSE");
        assertTrue(wrongCombo.isEmpty(), "ICSE does not have a topic named exactly 'Arrays'");

        List<Question> icseArrays = questionRepository.findByTopicAndCurriculum("Arrays, Searching & Sorting", "ICSE");
        assertEquals(10, icseArrays.size(), "Should find 10 ICSE Arrays questions");
    }

    @Test
    @DisplayName("Verify ICSE mock exam generation creates 40 questions covering all 8 ICSE units")
    void testIcseMockExamGeneration() {
        List<Question> icseMockExam = questionRepository.generateIcseMockExam(5);
        assertEquals(40, icseMockExam.size(), "ICSE mock exam must generate exactly 40 questions");

        Set<String> topicsCovered = new HashSet<>();
        for (Question q : icseMockExam) {
            assertEquals("ICSE", q.getCurriculum(), "All questions must belong to ICSE curriculum");
            topicsCovered.add(q.getTopic());
        }
        assertEquals(8, topicsCovered.size(), "ICSE mock exam must cover all 8 ICSE units");
    }

    @Test
    @DisplayName("Verify ISC curriculum question bank has 10 questions in each of the 8 ISC units")
    void testIscQuestionsCoverage() {
        List<Question> iscQuestions = questionRepository.findByCurriculum("ISC");
        assertEquals(80, iscQuestions.size(), "ISC must have 80 questions");
        String[] iscUnits = {
                "Classes, Objects & Wrapper Classes", "Strings & StringBuffer", "2D Arrays & Matrices", "Recursion",
                "Inheritance & Polymorphism", "Interfaces & Abstract Classes", "Stacks, Queues & Linked Lists",
                "Number & Date Programs"
        };
        for (String unit : iscUnits) {
            assertEquals(10, questionRepository.findByTopicAndCurriculum(unit, "ISC").size(),
                    "ISC Unit '" + unit + "' must have exactly 10 questions");
        }
    }

    @Test
    @DisplayName("Verify ISC mock exam generation creates 40 questions covering all 8 ISC units")
    void testIscMockExamGeneration() {
        List<Question> exam = questionRepository.generateIscMockExam(5);
        assertEquals(40, exam.size(), "ISC mock exam must generate exactly 40 questions");
        Set<String> topicsCovered = new HashSet<>();
        for (Question q : exam) {
            assertEquals("ISC", q.getCurriculum(), "All questions must belong to ISC curriculum");
            topicsCovered.add(q.getTopic());
        }
        assertEquals(8, topicsCovered.size(), "ISC mock exam must cover all 8 ISC units");
    }
}
