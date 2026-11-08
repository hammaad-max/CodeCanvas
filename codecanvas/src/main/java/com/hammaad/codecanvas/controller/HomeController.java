package com.hammaad.codecanvas.controller;

import com.hammaad.codecanvas.security.StudentPrincipal;
import com.hammaad.codecanvas.service.StudentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class HomeController {

    private final StudentService studentService;

    public HomeController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal StudentPrincipal principal, Model model) {
        // Accounts created before email was required are asked to add one (needed for password reset)
        model.addAttribute("needsEmail", principal != null
                && studentService.get(principal.getStudentId()).getEmail() == null);
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

    // Old tabbed ICSE guide (/icse/learn?unit=N) now lives on one page per unit; keep old links working
    @GetMapping("/icse/learn")
    public String icseLearn(@RequestParam(required = false) Integer unit) {
        int index = unit == null ? 0 : unit - 1;
        if (index < 0 || index >= ICSE_LESSONS.size()) {
            return "redirect:/icse";
        }
        return "redirect:/icse/" + ICSE_LESSONS.get(index);
    }

    // ICSE lesson pages (one per syllabus unit), styled like the AP CS lessons
    @GetMapping("/icse/{slug}")
    public String icseLesson(@PathVariable String slug) {
        return lesson("icse", ICSE_LESSONS, slug);
    }

    // ISC Java (Class 11 & 12) Curriculum Hub
    @GetMapping("/isc")
    public String isc() {
        return "isc";
    }

    // Timed ISC Java Mock Exam (shares the exam page; it detects the curriculum from the URL)
    @GetMapping("/isc/exam")
    public String iscExam() {
        return "exam";
    }

    // ISC lesson pages (one per syllabus unit)
    @GetMapping("/isc/{slug}")
    public String iscLesson(@PathVariable String slug) {
        return lesson("isc", ISC_LESSONS, slug);
    }

    /** Only known lesson slugs map to templates; anything else is a 404. */
    private static String lesson(String folder, List<String> slugs, String slug) {
        if (!slugs.contains(slug)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return folder + "/" + slug;
    }

    /** ICSE units in syllabus order (index + 1 = unit number). */
    static final List<String> ICSE_LESSONS = List.of(
            "oop-fundamentals", "operators", "conditionals", "loops",
            "number-programs", "strings", "methods", "arrays");

    /** ISC units in syllabus order. */
    static final List<String> ISC_LESSONS = List.of(
            "classes-objects", "strings", "matrices", "recursion",
            "inheritance", "interfaces", "data-structures", "number-date");
}