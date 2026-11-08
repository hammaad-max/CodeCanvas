package com.hammaad.codecanvas.controller;

import com.hammaad.codecanvas.model.AnswerResult;
import com.hammaad.codecanvas.model.AnswerSubmission;
import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.model.QuestionView;
import com.hammaad.codecanvas.security.StudentPrincipal;
import com.hammaad.codecanvas.service.ProgressService;
import com.hammaad.codecanvas.service.QuestionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Question API (login required). Questions are sent WITHOUT the correct answer or explanation;
 * those are only returned by the grading endpoints after the student submits an answer.
 */
@RestController
public class QuestionController {

    static final String EXAM_SESSION_KEY = "codecanvas.issuedExam";
    private static final int MAX_QUESTIONS_PER_TOPIC = 10;

    /** Exam handed out to the student, remembered in the session until it is graded. */
    record IssuedExam(String curriculum, Set<Long> questionIds) implements Serializable {
    }

    private final QuestionService questionService;
    private final ProgressService progressService;

    public QuestionController(QuestionService questionService, ProgressService progressService) {
        this.questionService = questionService;
        this.progressService = progressService;
    }

    @GetMapping("/api/questions")
    public List<QuestionView> getQuestions(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String curriculum) {

        List<Question> result;
        if (topic != null && curriculum != null) {
            result = questionService.getQuestionsByTopicAndCurriculum(topic, curriculum);
        } else if (topic != null) {
            result = questionService.getQuestionsByTopic(topic);
        } else if (curriculum != null) {
            result = questionService.getQuestionsByCurriculum(curriculum);
        } else {
            result = questionService.getAllQuestions();
        }
        return toViews(result);
    }

    @GetMapping("/api/exam/apcs")
    public List<QuestionView> getMockExam(
            @RequestParam(defaultValue = "5") int questionsPerTopic, HttpSession session) {
        return issue("APCS", questionService.getMockExam(clamp(questionsPerTopic)), session);
    }

    @GetMapping("/api/exam/icse")
    public List<QuestionView> getIcseMockExam(
            @RequestParam(defaultValue = "5") int questionsPerTopic, HttpSession session) {
        return issue("ICSE", questionService.getIcseMockExam(clamp(questionsPerTopic)), session);
    }

    /** Check a single quiz answer (immediate feedback, not stored). Body: {"answer": "..."} */
    @PostMapping("/api/questions/{id}/check")
    public ResponseEntity<AnswerResult> checkAnswer(
            @PathVariable Long id,
            @RequestBody AnswerSubmission submission) {
        String answer = submission == null ? null : submission.answer();
        return questionService.checkAnswer(id, answer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Grade the exam issued in this session and store the attempt. Body: [{"id": 1, "answer": "..."}, ...]
     * Each issued exam can be graded once.
     */
    @PostMapping("/api/exam/grade")
    public List<AnswerResult> gradeExam(@RequestBody List<AnswerSubmission> submissions,
                                        @AuthenticationPrincipal StudentPrincipal student,
                                        HttpSession session) {
        if (!(session.getAttribute(EXAM_SESSION_KEY) instanceof IssuedExam issued)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No active exam. Please start a new exam.");
        }
        List<AnswerResult> results = progressService.submitExam(
                student.getStudentId(), issued.curriculum(), issued.questionIds(), submissions);
        session.removeAttribute(EXAM_SESSION_KEY);
        return results;
    }

    private static List<QuestionView> issue(String curriculum, List<Question> questions, HttpSession session) {
        Set<Long> ids = questions.stream().map(Question::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        session.setAttribute(EXAM_SESSION_KEY, new IssuedExam(curriculum, ids));
        return toViews(questions);
    }

    private static List<QuestionView> toViews(List<Question> questions) {
        return questions.stream().map(QuestionView::shuffled).toList();
    }

    private static int clamp(int questionsPerTopic) {
        return Math.max(1, Math.min(MAX_QUESTIONS_PER_TOPIC, questionsPerTopic));
    }
}
