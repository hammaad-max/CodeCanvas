package com.hammaad.codecanvas;

import com.hammaad.codecanvas.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs against a real embedded server (not MockMvc) to verify health checks, error pages and API errors
 * exactly as a browser or load balancer would see them.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OperationsIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentService studentService;

    private HttpClient newClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager())
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private HttpResponse<String> get(HttpClient client, String path, String accept) throws Exception {
        return client.send(HttpRequest.newBuilder(uri(path)).header("Accept", accept).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    /** Signs in through the real login form (including its CSRF token) and returns the client + token. */
    private String[] signIn(HttpClient client) throws Exception {
        String username = TestStudents.uniqueUsername();
        TestStudents.register(studentService, username);

        String loginPage = get(client, "/", "text/html").body();
        Matcher m = Pattern.compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"").matcher(loginPage);
        assertTrue(m.find(), "login form must contain a CSRF token");
        String form = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(TestStudents.PASSWORD, StandardCharsets.UTF_8)
                + "&_csrf=" + URLEncoder.encode(m.group(1), StandardCharsets.UTF_8);
        HttpResponse<String> login = client.send(HttpRequest.newBuilder(uri("/login"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(form)).build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(302, login.statusCode());
        assertTrue(login.headers().firstValue("Location").orElse("").endsWith("/dashboard"));

        // CSRF token for API calls, as the pages get it from the <meta name="_csrf"> tag
        String dashboard = get(client, "/dashboard", "text/html").body();
        Matcher meta = Pattern.compile("<meta name=\"_csrf\" content=\"([^\"]+)\"").matcher(dashboard);
        assertTrue(meta.find(), "pages must expose the CSRF token in a meta tag");
        return new String[]{username, meta.group(1)};
    }

    @Test
    @DisplayName("Health check is public, reports UP (database included) and reveals no details")
    void healthCheck() throws Exception {
        HttpClient client = newClient();
        for (String path : new String[]{"/actuator/health", "/actuator/health/liveness", "/actuator/health/readiness"}) {
            HttpResponse<String> res = get(client, path, "application/json");
            assertEquals(200, res.statusCode(), path);
            assertTrue(res.body().contains("\"status\":\"UP\""), path + " -> " + res.body());
            assertFalse(res.body().contains("components"), "health must not list components: " + res.body());
        }
    }

    @Test
    @DisplayName("Other actuator endpoints are not reachable")
    void otherActuatorEndpointsBlocked() throws Exception {
        HttpClient client = newClient();
        for (String path : new String[]{"/actuator", "/actuator/env", "/actuator/beans", "/actuator/heapdump"}) {
            int status = get(client, path, "application/json").statusCode();
            assertNotEquals(200, status, path + " must not be reachable");
        }
        signIn(client);
        assertEquals(403, get(client, "/actuator/env", "application/json").statusCode());
    }

    @Test
    @DisplayName("Unknown pages show the branded 404 page to signed-in students")
    void notFoundPage() throws Exception {
        HttpClient client = newClient();
        signIn(client);
        HttpResponse<String> res = get(client, "/this-page-does-not-exist", "text/html");
        assertEquals(404, res.statusCode());
        assertTrue(res.body().contains("Page not found"), res.body());
        assertTrue(res.body().contains("Loop<span>Lab</span>"), "error page must use the LoopLab design");
        assertFalse(res.body().contains("Whitelabel"), "no default Spring error page");
        assertFalse(res.body().toLowerCase().contains("exception"), "no internals in error page");
    }

    @Test
    @DisplayName("API errors are short JSON messages without internals")
    void apiErrors() throws Exception {
        HttpClient client = newClient();
        String csrf = signIn(client)[1];

        HttpResponse<String> malformed = client.send(HttpRequest.newBuilder(uri("/api/quiz/submit"))
                        .header("Content-Type", "application/json").header("X-CSRF-TOKEN", csrf)
                        .POST(HttpRequest.BodyPublishers.ofString("{not json")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(400, malformed.statusCode());
        assertEquals("{\"status\":400,\"error\":\"Malformed request\"}", malformed.body());

        HttpResponse<String> noExam = client.send(HttpRequest.newBuilder(uri("/api/exam/grade"))
                        .header("Content-Type", "application/json").header("X-CSRF-TOKEN", csrf)
                        .POST(HttpRequest.BodyPublishers.ofString("[{\"id\":1,\"answer\":\"int\"}]")).build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(409, noExam.statusCode());
        assertTrue(noExam.body().contains("No active exam"), noExam.body());

        HttpResponse<String> badParam = get(client, "/api/exam/apcs?questionsPerTopic=abc", "application/json");
        assertEquals(400, badParam.statusCode());
        assertFalse(badParam.body().contains("Exception"), badParam.body());
    }
}
