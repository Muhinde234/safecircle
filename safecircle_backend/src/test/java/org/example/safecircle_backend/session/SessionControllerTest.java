package org.example.safecircle_backend.session;

import org.example.safecircle_backend.session.controller.SessionController;
import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.example.safecircle_backend.session.service.SessionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @Test // Tells JUnit that this is a runnable test!
    void shouldCreateAnonymousSessionWhenNicknameProvided() throws Exception {
        // Arrange
        SessionResponse response = SessionResponse.builder()
                .sessionId("abc-123")
                .nickname("nina")
                .createdAt("2026-04-30T10:00:00Z")
                .build();

        Mockito.when(sessionService.createAnonymousSession(Mockito.any(CreateSessionRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/sessions/anonymous")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"nina\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionId").value("abc-123"))
                .andExpect(jsonPath("$.nickname").value("nina"))
                .andExpect(jsonPath("$.createdAt").value("2026-04-30T10:00:00Z"));
    }
}
