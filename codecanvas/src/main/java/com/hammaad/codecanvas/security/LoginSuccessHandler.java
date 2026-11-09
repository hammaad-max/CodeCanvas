package com.hammaad.codecanvas.security;

import com.hammaad.codecanvas.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * After a successful sign-in: records the sign-in time, remembers in the session whether it was the student's
 * first sign-in ever (for the dashboard greeting), then always goes to the dashboard.
 */
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    /** Session attribute: true if the current session began with the student's first-ever sign-in. */
    public static final String FIRST_LOGIN = "tracetables.firstLogin";

    private final StudentService studentService;

    public LoginSuccessHandler(StudentService studentService) {
        super("/dashboard");
        setAlwaysUseDefaultTargetUrl(true);
        this.studentService = studentService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof StudentPrincipal student) {
            boolean firstLogin = studentService.recordLogin(student.getStudentId());
            request.getSession().setAttribute(FIRST_LOGIN, firstLogin);
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
