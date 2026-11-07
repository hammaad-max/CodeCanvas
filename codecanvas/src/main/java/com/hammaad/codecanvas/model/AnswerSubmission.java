package com.hammaad.codecanvas.model;

/**
 * A student's answer to one question. {@code answer} may be null for an unanswered exam question.
 */
public record AnswerSubmission(Long id, String answer) {
}
