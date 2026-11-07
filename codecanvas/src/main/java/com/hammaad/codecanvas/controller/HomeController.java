package com.hammaad.codecanvas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/apcs")
    public String apcs() {
        return "apcs";
    }

    @GetMapping("/apcs/variables")
    public String variables() {
        return "variables";
    }

    @GetMapping("/apcs/expressions")
    public String expressions() {
        return "expressions";
    }

    @GetMapping("/apcs/strings")
    public String strings() {
        return "strings";
    }

    @GetMapping("/apcs/selection")
    public String selection() {
        return "selection";
    }

    @GetMapping("/apcs/iteration")
    public String iteration() {
        return "iteration";
    }

    @GetMapping("/apcs/methods")
    public String methods() {
        return "methods";
    }

    @GetMapping("/apcs/classes")
    public String classes() {
        return "classes";
    }

    @GetMapping("/apcs/arrays")
    public String arrays() {
        return "arrays";
    }

    // Legacy route — redirect to the generic quiz engine
    @GetMapping("/apcs/variables/quiz")
    public String variablesQuiz() {
        return "redirect:/apcs/quiz?topic=Variables%20%26%20Data%20Types";
    }

    // Full Timed AP CS A Diagnostic Mock Exam
    @GetMapping("/apcs/exam")
    public String apcsExam() {
        return "exam";
    }

    // Reusable quiz engine — reads topic from ?topic= query parameter
    @GetMapping("/apcs/quiz")
    public String apcsQuiz() {
        return "quiz";
    }

    // Detailed Student Analytics and Mastery Dashboard
    @GetMapping("/progress")
    public String progress() {
        return "progress";
    }

    // ICSE Java (Class 9 & 10) Curriculum Hub
    @GetMapping("/icse")
    public String icse() {
        return "icse";
    }

    // Timed ICSE Class 10 Java Board Diagnostic Mock Exam
    @GetMapping("/icse/exam")
    public String icseExam() {
        return "exam";
    }

    // ICSE Java Complete Curriculum Learning & Concept Guides
    @GetMapping("/icse/learn")
    public String icseLearn() {
        return "icse-learn";
    }
}