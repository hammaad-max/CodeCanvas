package com.hammaad.codecanvas.controller;

import com.hammaad.codecanvas.model.PasswordForms;
import com.hammaad.codecanvas.model.Student;
import com.hammaad.codecanvas.security.StudentPrincipal;
import com.hammaad.codecanvas.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/** Signed-in student's account settings: email address (for password reset) and password change. */
@Controller
public class AccountController {

    private final StudentService studentService;

    public AccountController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/account")
    public String account(@AuthenticationPrincipal StudentPrincipal principal, Model model) {
        Student student = studentService.get(principal.getStudentId());
        PasswordForms.EmailChange emailForm = new PasswordForms.EmailChange();
        emailForm.setEmail(student.getEmail());
        model.addAttribute("emailForm", emailForm);
        model.addAttribute("passwordForm", new PasswordForms.Change());
        addAccount(model, student);
        return "account";
    }

    @PostMapping("/account/email")
    public String changeEmail(@AuthenticationPrincipal StudentPrincipal principal,
                              @Valid @ModelAttribute("emailForm") PasswordForms.EmailChange form,
                              BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                studentService.updateEmail(principal.getStudentId(), form.getEmail());
                return "redirect:/account?emailSaved";
            } catch (StudentService.EmailTakenException e) {
                result.rejectValue("email", "taken", "That email is already used by another account.");
            }
        }
        model.addAttribute("passwordForm", new PasswordForms.Change());
        addAccount(model, studentService.get(principal.getStudentId()));
        return "account";
    }

    @PostMapping("/account/password")
    public String changePassword(@AuthenticationPrincipal StudentPrincipal principal,
                                 @Valid @ModelAttribute("passwordForm") PasswordForms.Change form,
                                 BindingResult result, Model model) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }
        if (!result.hasErrors()) {
            try {
                studentService.changePassword(principal.getStudentId(), form.getCurrentPassword(), form.getPassword());
                return "redirect:/account?passwordSaved";
            } catch (StudentService.WrongPasswordException e) {
                result.rejectValue("currentPassword", "wrong", "Your current password is not correct.");
            }
        }
        Student student = studentService.get(principal.getStudentId());
        PasswordForms.EmailChange emailForm = new PasswordForms.EmailChange();
        emailForm.setEmail(student.getEmail());
        model.addAttribute("emailForm", emailForm);
        addAccount(model, student);
        return "account";
    }

    private static void addAccount(Model model, Student student) {
        model.addAttribute("username", student.getUsername());
        model.addAttribute("hasEmail", student.getEmail() != null);
    }
}
