package org.example.safecircle_backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sessionEndpointCreatesSession() throws Exception {
        mockMvc.perform(post("/api/v1/sessions/anonymous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"smoketest\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").isNotEmpty())
                .andExpect(jsonPath("$.nickname").value("smoketest"));
    }

    @Test
    void contentEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/content").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void clinicsEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/clinics").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void chatEndpointRequiresValidSession() throws Exception {
        // Create a session first
        MvcResult result = mockMvc.perform(post("/api/v1/sessions/anonymous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        String sessionId = body.split("\"sessionId\":\"")[1].split("\"")[0];

        mockMvc.perform(post("/api/v1/chat/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"" + sessionId + "\",\"message\":\"hello\",\"language\":\"en\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").isNotEmpty());
    }

    @Test
    void riskEndpointRequiresValidSession() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/sessions/anonymous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        String sessionId = body.split("\"sessionId\":\"")[1].split("\"")[0];

        mockMvc.perform(post("/api/v1/risk/assess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"" + sessionId + "\",\"eventType\":\"unprotected sex\",\"hoursSinceEvent\":48,\"symptomsPresent\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").isNotEmpty());
    }

    @Test
    void chatEndpointReturnsBadRequestWithoutSession() throws Exception {
        mockMvc.perform(post("/api/v1/chat/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"\",\"message\":\"hello\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookmarkEndpointsWorkEndToEnd() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/sessions/anonymous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        String sessionId = body.split("\"sessionId\":\"")[1].split("\"")[0];
        String targetUuid = UUID.randomUUID().toString();

        // 1. Add a bookmark
        mockMvc.perform(post("/api/v1/sessions/" + sessionId + "/bookmarks")
                        .param("type", "CLINIC")
                        .param("targetId", targetUuid))
                .andExpect(status().isCreated());

        // 2. Get bookmarks and verify
        mockMvc.perform(get("/api/v1/sessions/" + sessionId + "/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookmarkType").value("CLINIC"))
                .andExpect(jsonPath("$[0].targetId").value(targetUuid));

        // 3. Remove bookmark
        mockMvc.perform(delete("/api/v1/sessions/" + sessionId + "/bookmarks")
                        .param("type", "CLINIC")
                        .param("targetId", targetUuid))
                .andExpect(status().isNoContent());

        // 4. Get bookmarks and verify empty
        mockMvc.perform(get("/api/v1/sessions/" + sessionId + "/bookmarks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void lowBandwidthContentEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/content/low-bandwidth").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }
}
