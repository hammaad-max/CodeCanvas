package com.hammaad.codecanvas.controller;

import com.hammaad.codecanvas.model.AnswerSubmission;
import com.hammaad.codecanvas.security.StudentPrincipal;
import com.hammaad.codecanvas.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Per-student progress. The student is always taken from the login session, never from the request.
 */
@RestController
public class ProgressController {

    /** Body of a finished quiz. */
    public record QuizSubmission(String topic, String curriculum, List<AnswerSubmission> answers) {
    }

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/api/progress")
    public Map<String, Object> progress(@AuthenticationPrincipal StudentPrincipal student) {
        return progressService.progressFor(student.getStudentId(), student.getDisplayName());
    }

    @PostMapping("/api/quiz/submit")
    public ProgressService.QuizResult submitQuiz(@RequestBody QuizSubmission submission,
                                                 @AuthenticationPrincipal StudentPrincipal student) {
        return progressService.submitQuiz(student.getStudentId(),
                submission.topic(), submission.curriculum(), submission.answers());
    }

    @DeleteMapping("/api/progress")
    public ResponseEntity<Void> reset(@AuthenticationPrincipal StudentPrincipal student) {
        progressService.reset(student.getStudentId());
        return ResponseEntity.noContent().build();
    }
}
