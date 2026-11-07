package com.hammaad.codecanvas.model;

/**
 * Result of grading a single submitted answer.
 */
public record AnswerResult(
        Long id,
        String selectedAnswer,
        boolean correct,
        String correctAnswer,
        String explanation
) {
    public static AnswerResult grade(Question q, String selectedAnswer) {
        boolean correct = selectedAnswer != null && selectedAnswer.equals(q.getCorrectAnswer());
        return new AnswerResult(q.getId(), selectedAnswer, correct, q.getCorrectAnswer(), q.getExplanation());
    }
}
