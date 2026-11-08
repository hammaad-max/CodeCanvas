package com.hammaad.codecanvas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SecurityConfig {

    // Pages use inline <style>/<script> and Google Fonts; everything else must come from this origin.
    private static final String CSP = String.join("; ",
            "default-src 'self'",
            "script-src 'self' 'unsafe-inline'",
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",
            "font-src 'self' https://fonts.gstatic.com",
            "img-src 'self' data:",
            "connect-src 'self'",
            "object-src 'none'",
            "base-uri 'self'",
            "form-action 'self'",
            "frame-ancestors 'none'");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/error", "/favicon.ico", "/js/**", "/css/**")
                        .permitAll()
                        // Health check for load balancers / uptime monitors (status only, no details)
                        .requestMatchers("/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness")
                        .permitAll()
                        .requestMatchers("/actuator/**").denyAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                // API calls get a plain 401; every other request is redirected to the login page
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint()))
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.changeSessionId()))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(CSP))
                        .referrerPolicy(ref -> ref.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)));
        // CSRF protection stays ON (default). Pages send the token from <meta name="_csrf"> on fetch().
        return http.build();
    }

    private static AuthenticationEntryPoint entryPoint() {
        RequestMatcher api = PathPatternRequestMatcher.withDefaults().matcher("/api/**");
        AuthenticationEntryPoint unauthorized = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
        AuthenticationEntryPoint loginPage = new LoginUrlAuthenticationEntryPoint("/");
        return (request, response, ex) -> {
            if (api.matches(request)) {
                unauthorized.commence(request, response, ex);
            } else {
                loginPage.commence(request, response, ex);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Delegating encoder: BCrypt by default, hashes stored with a {bcrypt} prefix so they can be upgraded later.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
