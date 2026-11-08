package com.hammaad.codecanvas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A finished quiz or mock exam. The score is always computed on the server.
 */
@Entity
@Table(name = "attempts")
public class Attempt {

    public static final String QUIZ = "QUIZ";
    public static final String EXAM = "EXAM";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(nullable = false, length = 10)
    private String kind;

    @Column(nullable = false, length = 10)
    private String curriculum;

    @Column(length = 100)
    private String topic;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int total;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Attempt() {
    }

    public Attempt(Long studentId, String kind, String curriculum, String topic, int score, int total) {
        this.studentId = studentId;
        this.kind = kind;
        this.curriculum = curriculum;
        this.topic = topic;
        this.score = score;
        this.total = total;
    }

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public String getKind() { return kind; }
    public String getCurriculum() { return curriculum; }
    public String getTopic() { return topic; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }

    public int percentage() {
        return (int) Math.round(score * 100.0 / total);
    }
}
