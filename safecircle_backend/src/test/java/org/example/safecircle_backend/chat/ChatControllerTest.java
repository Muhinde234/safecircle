package org.example.safecircle_backend.chat;

import org.example.safecircle_backend.chat.controller.ChatController;
import org.example.safecircle_backend.chat.dto.ChatMessageRequest;
import org.example.safecircle_backend.chat.dto.ChatMessageResponse;
import org.example.safecircle_backend.chat.service.ChatService;
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

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void shouldReturnChatReplyForValidRequest() throws Exception {
        ChatMessageResponse response = ChatMessageResponse.builder()
                .reply("Testing reply")
                .source("RULE_BASED")
                .timestamp("2026-05-05T08:00:00Z")
                .build();

        Mockito.when(chatService.reply(Mockito.any(ChatMessageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/chat/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"session-123\",\"message\":\"I need HIV help\",\"language\":\"en\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Testing reply"))
                .andExpect(jsonPath("$.source").value("RULE_BASED"))
                .andExpect(jsonPath("$.timestamp").value("2026-05-05T08:00:00Z"));
    }

    @Test
    void shouldReturnBadRequestWhenSessionIdMissing() throws Exception {
        mockMvc.perform(post("/api/v1/chat/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":\"\",\"message\":\"Hello\",\"language\":\"en\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
