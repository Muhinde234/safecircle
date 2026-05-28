package org.example.safecircle_backend.chat;

import org.example.safecircle_backend.chat.controller.ModerationController;
import org.example.safecircle_backend.chat.dto.FlagMessageRequest;
import org.example.safecircle_backend.chat.dto.ModeratedMessageResponse;
import org.example.safecircle_backend.chat.service.ChatService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModerationController.class)
class ModerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @Test
    void shouldFlagMessageWithRequestBody() throws Exception {
        UUID messageId = UUID.randomUUID();
        ModeratedMessageResponse response = ModeratedMessageResponse.builder()
                .id(messageId.toString())
                .sessionId(UUID.randomUUID().toString())
                .nickname("anon")
                .messageText("inappropriate text")
                .isFlagged(true)
                .moderationNotes("inappropriate language")
                .build();

        Mockito.when(chatService.flagMessage(Mockito.eq(messageId), Mockito.eq("inappropriate language")))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/moderation/chat-messages/" + messageId + "/flag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"moderationNotes\":\"inappropriate language\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.isFlagged").value(true))
                .andExpect(jsonPath("$.moderationNotes").value("inappropriate language"));
    }

    @Test
    void shouldFlagMessageWithRequestParam() throws Exception {
        UUID messageId = UUID.randomUUID();
        ModeratedMessageResponse response = ModeratedMessageResponse.builder()
                .id(messageId.toString())
                .sessionId(UUID.randomUUID().toString())
                .nickname("anon")
                .messageText("inappropriate text")
                .isFlagged(true)
                .moderationNotes("inappropriate language")
                .build();

        Mockito.when(chatService.flagMessage(Mockito.eq(messageId), Mockito.eq("inappropriate language")))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/moderation/chat-messages/" + messageId + "/flag")
                        .param("notes", "inappropriate language"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.isFlagged").value(true))
                .andExpect(jsonPath("$.moderationNotes").value("inappropriate language"));
    }

    @Test
    void shouldReturnFlaggedMessages() throws Exception {
        UUID messageId = UUID.randomUUID();
        List<ModeratedMessageResponse> flagged = List.of(
                ModeratedMessageResponse.builder()
                        .id(messageId.toString())
                        .sessionId(UUID.randomUUID().toString())
                        .nickname("anon")
                        .messageText("inappropriate text")
                        .isFlagged(true)
                        .moderationNotes("notes")
                        .build()
        );

        Mockito.when(chatService.getFlaggedMessages()).thenReturn(flagged);

        mockMvc.perform(get("/api/v1/moderation/chat-messages/flagged")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(messageId.toString()))
                .andExpect(jsonPath("$[0].isFlagged").value(true));
    }
}
