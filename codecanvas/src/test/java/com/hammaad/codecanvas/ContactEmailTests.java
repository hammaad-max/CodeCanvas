package com.hammaad.codecanvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** When CONTACT_EMAIL is set, the footer and legal pages show it. */
@SpringBootTest(properties = {"tracetables.contact-email=help@example.test", "tracetables.base-url=https://tt.example.test/"})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ContactEmailTests {

    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("Configured contact address appears in the footer and legal pages")
    void contactShown() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("mailto:help@example.test")))
                .andExpect(content().string(containsString("Report a problem")));
        mockMvc.perform(get("/privacy")).andExpect(content().string(containsString("help@example.test")));
        mockMvc.perform(get("/terms")).andExpect(content().string(containsString("help@example.test")));
    }

    @Test
    @DisplayName("Link preview image uses the configured public address")
    void previewUsesBaseUrl() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("https://tt.example.test/images/og-image.png")));
    }
}
