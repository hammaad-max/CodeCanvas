package com.hammaad.codecanvas.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Public, answer-free view of a question sent to the browser.
 * The correct answer and explanation are only revealed by the grading endpoints.
 */
public record QuestionView(
        Long id,
        String questionText,
        String optionA,
        String optionB,
        String optionC,
        String optionD,
        String topic,
        String curriculum
) {
    public static QuestionView from(Question q) {
        return new QuestionView(q.getId(), q.getQuestionText(),
                q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(),
                q.getTopic(), q.getCurriculum());
    }

    /**
     * Same as {@link #from(Question)} but with the four options in random order.
     * The question bank stores most correct answers in position B, so serving a fixed
     * order would let students pass by always picking B. Grading compares answer text,
     * so reordering is safe.
     */
    public static QuestionView shuffled(Question q) {
        List<String> options = new ArrayList<>(List.of(
                q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()));
        Collections.shuffle(options);
        return new QuestionView(q.getId(), q.getQuestionText(),
                options.get(0), options.get(1), options.get(2), options.get(3),
                q.getTopic(), q.getCurriculum());
    }
}
