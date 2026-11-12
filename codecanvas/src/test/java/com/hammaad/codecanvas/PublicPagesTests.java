package com.hammaad.codecanvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Legal pages, footer, link previews and icons are reachable without signing in; no contact address leaks. */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PublicPagesTests {

    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("Privacy and Terms are public and name the data kept and the trademark notice")
    void legalPages() throws Exception {
        mockMvc.perform(get("/privacy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("bcrypt")))
                .andExpect(content().string(containsString("Last updated")));
        mockMvc.perform(get("/terms"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("College Board")))
                .andExpect(content().string(containsString("CISCE")));
    }

    @Test
    @DisplayName("No contact address is published unless CONTACT_EMAIL is set")
    void noContactByDefault() throws Exception {
        for (String page : new String[]{"/", "/privacy", "/terms"}) {
            mockMvc.perform(get(page))
                    .andExpect(status().isOk())
                    .andExpect(content().string(not(containsString("mailto:"))))
                    .andExpect(content().string(not(containsString("Report a problem"))));
        }
    }

    @Test
    @DisplayName("Login page has a footer with links, a description and link-preview tags")
    void loginPageBasics() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/privacy\"")))
                .andExpect(content().string(containsString("href=\"/terms\"")))
                .andExpect(content().string(containsString("name=\"description\"")))
                .andExpect(content().string(containsString("name=\"theme-color\"")))
                .andExpect(content().string(containsString("property=\"og:image\"")))
                .andExpect(content().string(containsString("/images/og-image.png")))
                .andExpect(content().string(containsString("apple-touch-icon")));
    }

    @Test
    @DisplayName("Sign-up page links to Terms and Privacy")
    void registerConsent() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("agree to the")));
    }

    @Test
    @DisplayName("Icons, manifest and robots.txt are public")
    void staticFilesPublic() throws Exception {
        for (String path : new String[]{"/site.webmanifest", "/robots.txt", "/images/apple-touch-icon.png",
                "/images/icon-192.png", "/images/icon-512.png", "/images/og-image.png"}) {
            mockMvc.perform(get(path)).andExpect(status().isOk());
        }
        mockMvc.perform(get("/robots.txt")).andExpect(content().string(containsString("Disallow: /api/")));
    }
}
