package com.hammaad.codecanvas.service;

import com.hammaad.codecanvas.model.AnswerResult;
import com.hammaad.codecanvas.model.AnswerSubmission;
import com.hammaad.codecanvas.model.Attempt;
import com.hammaad.codecanvas.model.Question;
import com.hammaad.codecanvas.repository.AttemptRepository;
import com.hammaad.codecanvas.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Grades quiz/exam submissions on the server, stores the attempts, and builds each student's progress summary.
 */
@Service
public class ProgressService {

    public static final int PASS_PERCENT = 70;

    private static final Logger log = LoggerFactory.getLogger(ProgressService.class);

    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;
    private final QuestionService questionService;

    public ProgressService(QuestionRepository questionRepository, AttemptRepository attemptRepository,
                           QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.questionService = questionService;
    }

    /** "ICSE" / "ISC" (any case) → that curriculum; anything else → APCS. */
    public static String normalizeCurriculum(String curriculum) {
        String c = curriculum == null ? "" : curriculum.trim().toUpperCase(java.util.Locale.ROOT);
        return switch (c) {
            case "ICSE", "ISC" -> c;
            default -> "APCS";
        };
    }

    /** Curriculum name used in the question bank. */
    private static String bankCurriculum(String curriculum) {
        return "APCS".equals(curriculum) ? "AP CS" : curriculum;
    }

    // ------------------------------------------------------------------ quiz

    public record QuizResult(int score, int total, int percentage, boolean passed) {
    }

    @Transactional
    public QuizResult submitQuiz(Long studentId, String topic, String curriculumParam, List<AnswerSubmission> answers) {
        if (topic == null || topic.isBlank()) {
            throw badRequest("Topic is required");
        }
        String curriculum = normalizeCurriculum(curriculumParam);
        List<Question> topicQuestions = questionRepository.findByTopicAndCurriculum(topic.trim(), bankCurriculum(curriculum));
        if (topicQuestions.isEmpty()) {
            throw badRequest("Unknown topic");
        }
        Set<Long> allowedIds = topicQuestions.stream().map(Question::getId).collect(Collectors.toSet());
        List<AnswerSubmission> checked = validateAnswers(answers, allowedIds, topicQuestions.size());

        List<AnswerResult> results = questionService.gradeAnswers(checked);
        int score = (int) results.stream().filter(AnswerResult::correct).count();
        int total = results.size();
        // Store the canonical topic name from the question bank, not the client's spelling
        String canonicalTopic = topicQuestions.getFirst().getTopic();
        attemptRepository.save(new Attempt(studentId, Attempt.QUIZ, curriculum, canonicalTopic, score, total));
        log.info("attempt.quiz studentId={} curriculum={} topic=\"{}\" score={}/{}", studentId, curriculum, canonicalTopic, score, total);

        int pct = (int) Math.round(score * 100.0 / total);
        return new QuizResult(score, total, pct, pct >= PASS_PERCENT);
    }

    // ------------------------------------------------------------------ exam

    /**
     * Grades a mock exam. {@code issuedIds} are the question ids the server handed out for this exam
     * (kept in the session), so a student cannot swap in questions they already know.
     */
    @Transactional
    public List<AnswerResult> submitExam(Long studentId, String curriculum, Set<Long> issuedIds,
                                         List<AnswerSubmission> answers) {
        List<AnswerSubmission> checked = validateAnswers(answers, issuedIds, issuedIds.size());
        Set<Long> submittedIds = checked.stream().map(AnswerSubmission::id).collect(Collectors.toSet());
        if (!submittedIds.equals(issuedIds)) {
            throw badRequest("Submission does not match the issued exam");
        }
        List<AnswerResult> results = questionService.gradeAnswers(checked);
        int score = (int) results.stream().filter(AnswerResult::correct).count();
        attemptRepository.save(new Attempt(studentId, Attempt.EXAM, curriculum, null, score, results.size()));
        log.info("attempt.exam studentId={} curriculum={} score={}/{}", studentId, curriculum, score, results.size());
        return results;
    }

    private static List<AnswerSubmission> validateAnswers(List<AnswerSubmission> answers, Set<Long> allowedIds, int max) {
        if (answers == null || answers.isEmpty()) {
            throw badRequest("No answers submitted");
        }
        if (answers.size() > max) {
            throw badRequest("Too many answers");
        }
        Set<Long> seen = new HashSet<>();
        for (AnswerSubmission a : answers) {
            if (a == null || a.id() == null || !allowedIds.contains(a.id())) {
                throw badRequest("Answer refers to a question that is not part of this quiz");
            }
            if (!seen.add(a.id())) {
                throw badRequest("Duplicate answer for a question");
            }
            if (a.answer() != null && a.answer().length() > 500) {
                throw badRequest("Answer too long");
            }
        }
        return answers;
    }

    private static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    // ------------------------------------------------------------------ progress summary

    /**
     * Progress in the same shape the pages already use in localStorage, so the existing UI code keeps working.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> progressFor(Long studentId, String displayName) {
        List<Attempt> attempts = attemptRepository.findByStudentIdOrderByCreatedAtAsc(studentId);

        Map<String, Map<String, Object>> completedTopics = new LinkedHashMap<>();
        Attempt latestApcsExam = null;
        Attempt latestIcseExam = null;
        Attempt latestIscExam = null;

        for (Attempt a : attempts) {
            if (Attempt.EXAM.equals(a.getKind())) {
                switch (a.getCurriculum()) {
                    case "ICSE" -> latestIcseExam = a;
                    case "ISC" -> latestIscExam = a;
                    default -> latestApcsExam = a;
                }
                continue;
            }
            Map<String, Object> prev = completedTopics.get(a.getTopic());
            int prevBest = prev == null ? 0 : (int) prev.get("bestScore");
            boolean prevPassed = prev != null && (boolean) prev.get("passed");
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("curriculum", a.getCurriculum());
            entry.put("lastScore", a.getScore());
            entry.put("bestScore", Math.max(prevBest, a.getScore()));
            entry.put("total", a.getTotal());
            entry.put("percentage", a.percentage());
            entry.put("passed", prevPassed || a.percentage() >= PASS_PERCENT);
            entry.put("completedAt", a.getCreatedAt().toString());
            completedTopics.put(a.getTopic(), entry);
        }

        long passedTopics = completedTopics.values().stream().filter(e -> (boolean) e.get("passed")).count();

        Map<String, Object> progress = new LinkedHashMap<>();
        progress.put("displayName", displayName);
        progress.put("completedTopics", completedTopics);
        progress.put("quizzesCompleted", passedTopics);
        progress.put("mockExam", examSummary(latestApcsExam));
        progress.put("icseMockExam", examSummary(latestIcseExam));
        progress.put("iscMockExam", examSummary(latestIscExam));
        return progress;
    }

    private static Map<String, Object> examSummary(Attempt exam) {
        if (exam == null) {
            return null;
        }
        int pct = exam.percentage();
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("score", exam.getScore());
        summary.put("total", exam.getTotal());
        summary.put("percentage", pct);
        // ICSE and ISC use board-style grade bands; AP CS uses the 1-5 AP score
        summary.put("projectedScore", "APCS".equals(exam.getCurriculum()) ? apScore(pct) : icseGrade(pct));
        summary.put("curriculum", exam.getCurriculum());
        summary.put("completedAt", exam.getCreatedAt().toString());
        return summary;
    }

    // Same bands as exam.html
    static int apScore(int pct) {
        if (pct >= 85) return 5;
        if (pct >= 70) return 4;
        if (pct >= 55) return 3;
        if (pct >= 40) return 2;
        return 1;
    }

    static String icseGrade(int pct) {
        if (pct >= 90) return "100/100";
        if (pct >= 80) return "90%+";
        if (pct >= 70) return "80%+";
        return pct + "%";
    }

    @Transactional
    public void reset(Long studentId) {
        long deleted = attemptRepository.deleteByStudentId(studentId);
        log.info("progress.reset studentId={} attemptsDeleted={}", studentId, deleted);
    }
}
