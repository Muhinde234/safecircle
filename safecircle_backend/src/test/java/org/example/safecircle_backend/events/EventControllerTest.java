package org.example.safecircle_backend.events;

import org.example.safecircle_backend.events.controller.EventController;
import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.model.EventType;
import org.example.safecircle_backend.events.service.EventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void shouldCreateEvent() throws Exception {
        TrackEventResponse response = TrackEventResponse.builder()
                .status("Success")
                .eventType(EventType.CHAT_SENT)
                .recordedAt("2026-05-06T16:00:00Z")
                .sessionId("session-123")
                .build();

        Mockito.when(eventService.trackEvent(Mockito.any(TrackEventRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"session-123\",\"eventType\":\"CHAT_SENT\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.eventType").value("CHAT_SENT"))
                .andExpect(jsonPath("$.sessionId").value("session-123"));
    }

    @Test
    void shouldReturnBadRequestWhenSessionIdIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"\",\"eventType\":\"CHAT_SENT\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturnBadRequestWhenEventTypeMissing() throws Exception {
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"session-123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturnEventLogs() throws Exception {
        List<TrackEventResponse> logs = List.of(
                TrackEventResponse.builder()
                        .status("Success")
                        .eventType(EventType.CONTENT_VIEW)
                        .recordedAt("2026-05-06T16:00:00Z")
                        .sessionId("session-abc")
                        .build()
        );

        Mockito.when(eventService.viewEventLogs()).thenReturn(logs);

        mockMvc.perform(get("/api/v1/events").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventType").value("CONTENT_VIEW"))
                .andExpect(jsonPath("$[0].sessionId").value("session-abc"));
    }
}
