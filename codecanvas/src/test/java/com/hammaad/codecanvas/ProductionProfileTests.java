package com.hammaad.codecanvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The application context starts with the prod profile (database swapped for H2 by the test profile)
 * and the production-critical settings are in effect.
 */
@SpringBootTest
@ActiveProfiles({"prod", "test"})
class ProductionProfileTests {

    @Autowired
    private Environment env;

    @Test
    @DisplayName("Prod profile enables secure cookies, proxy headers, template cache and safe errors")
    void productionSettings() {
        assertEquals("true", env.getProperty("server.servlet.session.cookie.secure"));
        assertEquals("true", env.getProperty("server.servlet.session.cookie.http-only"));
        assertEquals("native", env.getProperty("server.forward-headers-strategy"));
        assertEquals("true", env.getProperty("spring.thymeleaf.cache"));
        assertEquals("true", env.getProperty("spring.flyway.clean-disabled"));
        assertEquals("never", env.getProperty("server.error.include-stacktrace"));
        assertEquals("never", env.getProperty("management.endpoint.health.show-details"));
        assertEquals("health", env.getProperty("management.endpoints.web.exposure.include"));
    }
}
