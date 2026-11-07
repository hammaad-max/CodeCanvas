package com.hammaad.codecanvas.controller;

import com.hammaad.codecanvas.model.AnswerResult;
import com.hammaad.codecanvas.model.AnswerSubmission;
import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.model.QuestionView;
import com.hammaad.codecanvas.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public question API. Questions are sent WITHOUT the correct answer or explanation;
 * those are only returned by the grading endpoints after the student submits an answer.
 */
@RestController
public class QuestionController {

    private static final int MAX_QUESTIONS_PER_TOPIC = 10;

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
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
            @RequestParam(defaultValue = "5") int questionsPerTopic) {
        return toViews(questionService.getMockExam(clamp(questionsPerTopic)));
    }

    @GetMapping("/api/exam/icse")
    public List<QuestionView> getIcseMockExam(
            @RequestParam(defaultValue = "5") int questionsPerTopic) {
        return toViews(questionService.getIcseMockExam(clamp(questionsPerTopic)));
    }

    /** Check a single quiz answer. Body: {"answer": "..."} */
    @PostMapping("/api/questions/{id}/check")
    public ResponseEntity<AnswerResult> checkAnswer(
            @PathVariable Long id,
            @RequestBody AnswerSubmission submission) {
        String answer = submission == null ? null : submission.answer();
        return questionService.checkAnswer(id, answer)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Grade a whole mock exam. Body: [{"id": 1, "answer": "..."}, ...] */
    @PostMapping("/api/exam/grade")
    public List<AnswerResult> gradeExam(@RequestBody List<AnswerSubmission> submissions) {
        return questionService.gradeAnswers(submissions);
    }

    private static List<QuestionView> toViews(List<Question> questions) {
        return questions.stream().map(QuestionView::shuffled).toList();
    }

    private static int clamp(int questionsPerTopic) {
        return Math.max(1, Math.min(MAX_QUESTIONS_PER_TOPIC, questionsPerTopic));
    }
}
