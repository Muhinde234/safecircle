package org.example.safecircle_backend.chat;

import org.example.safecircle_backend.chat.dto.ChatHistoryResponse;
import org.example.safecircle_backend.chat.dto.ChatMessageRequest;
import org.example.safecircle_backend.chat.dto.ChatMessageResponse;
import org.example.safecircle_backend.chat.model.ChatMessage;
import org.example.safecircle_backend.chat.model.ChatRole;
import org.example.safecircle_backend.chat.model.ChatSource;
import org.example.safecircle_backend.chat.repository.ChatMessageRepository;
import org.example.safecircle_backend.chat.service.ChatService;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private SessionService sessionService;

    private ChatService chatService;

    private static final String SESSION_ID = UUID.randomUUID().toString();
    private AnonymousSession mockSession;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(chatMessageRepository, sessionService);
        mockSession = AnonymousSession.builder().nickname("tester").language("en").build();
        Mockito.when(sessionService.getSessionById(SESSION_ID)).thenReturn(mockSession);
    }

    private void stubSave() {
        Mockito.when(chatMessageRepository.save(Mockito.any(ChatMessage.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void shouldReturnPrepResponseForPrepKeyword() {
        stubSave();
        ChatMessageRequest request = ChatMessageRequest.builder()
                .sessionId(SESSION_ID).message("Can you explain PrEP?").language("en").build();

        ChatMessageResponse response = chatService.reply(request);

        assertNotNull(response);
        assertEquals("RULE_BASED", response.getSource());
        assertTrue(response.getReply().toLowerCase().contains("prep"));
    }

    @Test
    void shouldReturnClinicResponseForClinicKeyword() {
        stubSave();
        ChatMessageRequest request = ChatMessageRequest.builder()
                .sessionId(SESSION_ID).message("I need a clinic near me").language("en").build();

        ChatMessageResponse response = chatService.reply(request);

        assertTrue(response.getReply().toLowerCase().contains("clinic"));
    }

    @Test
    void shouldReturnDefaultResponseForUnknownTopic() {
        stubSave();
        ChatMessageRequest request = ChatMessageRequest.builder()
                .sessionId(SESSION_ID).message("hello there").language("en").build();

        ChatMessageResponse response = chatService.reply(request);

        assertTrue(response.getReply().toLowerCase().contains("i'm here to listen"));
    }

    @Test
    void shouldDefaultLanguageToEnglishWhenMissing() {
        stubSave();
        ChatMessageRequest request = ChatMessageRequest.builder()
                .sessionId(SESSION_ID).message("Need HIV advice").language(null).build();

        ChatMessageResponse response = chatService.reply(request);

        assertTrue(response.getReply().toLowerCase().contains("testing is the only way"));
    }

    @Test
    void shouldReturnChatHistory() {
        UUID sessionUUID = UUID.randomUUID();
        try {
            var idField = AnonymousSession.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockSession, sessionUUID);
        } catch (Exception ignored) {}

        ChatMessage msg = new ChatMessage();
        msg.setMessageText("Hello");
        msg.setRole(ChatRole.USER);
        msg.setSource(ChatSource.RULE_BASED);
        msg.setLanguage("en");
        msg.setCreatedAt(OffsetDateTime.now());
        try {
            var idField = ChatMessage.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(msg, UUID.randomUUID());
        } catch (Exception ignored) {}

        Mockito.when(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionUUID))
                .thenReturn(List.of(msg));

        List<ChatHistoryResponse> history = chatService.getHistory(SESSION_ID);

        assertEquals(1, history.size());
        assertEquals("Hello", history.getFirst().getMessage());
        assertEquals("USER", history.getFirst().getRole());
    }
}
