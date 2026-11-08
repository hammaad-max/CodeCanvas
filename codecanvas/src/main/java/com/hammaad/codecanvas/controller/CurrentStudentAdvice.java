package com.hammaad.codecanvas.controller;

import com.hammaad.codecanvas.security.StudentPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Makes the signed-in student's display name available to every page template as ${studentName}.
 */
@ControllerAdvice(annotations = Controller.class)
public class CurrentStudentAdvice {

    @ModelAttribute("studentName")
    public String studentName(@AuthenticationPrincipal StudentPrincipal student) {
        return student == null ? null : student.getDisplayName();
    }
}
