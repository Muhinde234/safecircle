package org.example.safecircle_backend.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.example.safecircle_backend.chat.dto.ChatHistoryResponse;
import org.example.safecircle_backend.chat.dto.ChatMessageRequest;
import org.example.safecircle_backend.chat.dto.ChatMessageResponse;
import org.example.safecircle_backend.chat.model.ChatMessage;
import org.example.safecircle_backend.chat.model.ChatRole;
import org.example.safecircle_backend.chat.model.ChatSource;
import org.example.safecircle_backend.chat.repository.ChatMessageRepository;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.service.SessionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.example.safecircle_backend.chat.dto.ModeratedMessageResponse;

@Slf4j
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final SessionService sessionService;

    public ChatService(ChatMessageRepository chatMessageRepository, SessionService sessionService) {
        this.chatMessageRepository = chatMessageRepository;
        this.sessionService = sessionService;
    }

    private String buildReply(String userMessage) {
        String msg = userMessage.trim().toLowerCase();
        if (msg.contains("hiv") || msg.contains("sti")) {
            return "It's brave to ask. Testing is the only way to know your status. Would you like to find a clinic?";
        } else if (msg.contains("prep")) {
            return "PrEP is a daily pill that highly effectively prevents HIV. It is a great way to take control of your health.";
        } else if (msg.contains("clinic")) {
            return "I can help you find a non-judgmental clinic nearby. Are you looking for testing or a general checkup?";
        }
        return "I'm here to listen. Can you tell me a bit more about what's on your mind?";
    }

    public ChatMessageResponse reply(ChatMessageRequest request) {
        AnonymousSession session = sessionService.getSessionById(request.getSessionId());

        String language = (request.getLanguage() == null || request.getLanguage().isBlank())
                ? "en" : request.getLanguage().toLowerCase();

        String botReply = buildReply(request.getMessage());

        // Persist user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSession(session);
        userMsg.setMessageText(request.getMessage());
        userMsg.setRole(ChatRole.USER);
        userMsg.setLanguage(language);
        chatMessageRepository.save(userMsg);

        // Persist assistant reply
        ChatMessage assistantMsg = new ChatMessage();
        assistantMsg.setSession(session);
        assistantMsg.setMessageText(botReply);
        assistantMsg.setRole(ChatRole.ASSISTANT);
        assistantMsg.setSource(ChatSource.RULE_BASED);
        assistantMsg.setLanguage(language);
        chatMessageRepository.save(assistantMsg);

        log.info("Chat message processed for session {}", request.getSessionId());

        return ChatMessageResponse.builder()
                .reply(botReply)
                .source("RULE_BASED")
                .timestamp(Instant.now().toString())
                .build();
    }

    public List<ChatHistoryResponse> getHistory(String sessionId) {
        AnonymousSession session = sessionService.getSessionById(sessionId);

        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId())
                .stream()
                .map(m -> ChatHistoryResponse.builder()
                        .id(m.getId().toString())
                        .role(m.getRole() != null ? m.getRole().name() : null)
                        .message(m.getMessageText())
                        .source(m.getSource() != null ? m.getSource().name() : null)
                        .language(m.getLanguage())
                        .createdAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : null)
                        .build())
                .toList();
    }

    public ModeratedMessageResponse flagMessage(UUID messageId, String notes) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found with id: " + messageId));
        message.setIsFlagged(true);
        message.setModerationNotes(notes);
        ChatMessage saved = chatMessageRepository.save(message);
        return mapToModeratedResponse(saved);
    }

    public List<ModeratedMessageResponse> getFlaggedMessages() {
        return chatMessageRepository.findByIsFlaggedTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToModeratedResponse)
                .toList();
    }

    private ModeratedMessageResponse mapToModeratedResponse(ChatMessage message) {
        return ModeratedMessageResponse.builder()
                .id(message.getId().toString())
                .sessionId(message.getSession().getId().toString())
                .nickname(message.getSession().getNickname())
                .messageText(message.getMessageText())
                .createdAt(message.getCreatedAt() != null ? message.getCreatedAt().toString() : null)
                .isFlagged(message.getIsFlagged())
                .moderationNotes(message.getModerationNotes())
                .build();
    }
}
