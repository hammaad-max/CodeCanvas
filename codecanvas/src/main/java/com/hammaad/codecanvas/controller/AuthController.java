package com.hammaad.codecanvas.controller;

import com.hammaad.codecanvas.model.RegistrationForm;
import com.hammaad.codecanvas.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final StudentService studentService;

    public AuthController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** Login page. Already signed-in students go straight to the dashboard. */
    @GetMapping("/")
    public String login(Authentication authentication) {
        return isSignedIn(authentication) ? "redirect:/dashboard" : "login";
    }

    @GetMapping("/register")
    public String registerForm(Authentication authentication, Model model) {
        if (isSignedIn(authentication)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("form", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult result) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match.");
        }
        if (result.hasErrors()) {
            return "register";
        }
        try {
            studentService.register(form);
        } catch (StudentService.UsernameTakenException e) {
            result.rejectValue("username", "taken", "That username is already taken.");
            return "register";
        } catch (StudentService.EmailTakenException e) {
            result.rejectValue("email", "taken", "That email is already used by another account.");
            return "register";
        }
        return "redirect:/?registered";
    }

    private static boolean isSignedIn(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
