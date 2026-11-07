package com.hammaad.codecanvas.service;

import com.hammaad.codecanvas.model.AnswerResult;
import com.hammaad.codecanvas.model.AnswerSubmission;
import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByTopic(String topic) {
        return questionRepository.findByTopic(topic);
    }

    public List<Question> getQuestionsByCurriculum(String curriculum) {
        return questionRepository.findByCurriculum(curriculum);
    }

    public List<Question> getQuestionsByTopicAndCurriculum(String topic, String curriculum) {
        return questionRepository.findByTopicAndCurriculum(topic, curriculum);
    }

    public List<Question> getMockExam(int questionsPerTopic) {
        return questionRepository.generateMockExam(questionsPerTopic);
    }

    public List<Question> getIcseMockExam(int questionsPerTopic) {
        return questionRepository.generateIcseMockExam(questionsPerTopic);
    }

    /** Grades one answer; empty if the question id does not exist. */
    public Optional<AnswerResult> checkAnswer(Long id, String answer) {
        return questionRepository.findById(id).map(q -> AnswerResult.grade(q, answer));
    }

    /** Grades a batch of answers (mock exam submission). Unknown ids are skipped. */
    public List<AnswerResult> gradeAnswers(List<AnswerSubmission> submissions) {
        if (submissions == null) {
            return List.of();
        }
        return submissions.stream()
                .filter(s -> s != null && s.id() != null)
                .map(s -> checkAnswer(s.id(), s.answer()))
                .flatMap(Optional::stream)
                .toList();
    }
}