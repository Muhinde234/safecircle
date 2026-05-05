package org.example.safecircle_backend.chat;

import org.example.safecircle_backend.chat.dto.ChatMessageRequest;
import org.example.safecircle_backend.chat.dto.ChatMessageResponse;
import org.example.safecircle_backend.chat.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {

    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService();
    }

    @Test
    void shouldReturnPrepResponseForPrepKeyword() {
        ChatMessageRequest request = ChatMessageRequest.builder()
                .sessionId("session-1")
                .message("Can you explain PrEP?")
                .language("en")
                .build();

        ChatMessageResponse response = chatService.reply(request);

        assertNotNull(response);
        assertEquals("RULE_BASED", response.getSource());
        assertNotNull(response.getTimestamp());
        assertTrue(response.getReply().toLowerCase().contains("prep"));
    }

    @Test
    void shouldReturnClinicResponseForClinicKeyword() {
        ChatMessageRequest request = ChatMessageRequest.builder()
                .sessionId("session-2")
                .message("I need a clinic near me")
                .language("en")
                .build();

        ChatMessageResponse response = chatService.reply(request);

        assertNotNull(response);
        assertTrue(response.getReply().toLowerCase().contains("clinic"));
    }

    @Test
    void shouldReturnDefaultResponseForUnknownTopic() {
        ChatMessageRequest request = ChatMessageRequest.builder()
                .sessionId("session-3")
                .message("hello there")
                .language("en")
                .build();

        ChatMessageResponse response = chatService.reply(request);

        assertNotNull(response);
        assertTrue(response.getReply().toLowerCase().contains("i'm here to listen"));
    }
}
