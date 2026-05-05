package org.example.safecircle_backend.chat.service;

import org.example.safecircle_backend.chat.dto.ChatMessageRequest;
import org.example.safecircle_backend.chat.dto.ChatMessageResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ChatService {

    public ChatMessageResponse reply(ChatMessageRequest request) {
        String userMessage = request.getMessage().trim().toLowerCase();
        String userLanguage = request.getLanguage();

        String botReply = "Welcome to SafeCircle!!";

        if (userLanguage.equals("en")) {
            if(userMessage.contains("hiv") || userMessage.contains("sti")) {
                botReply = "It's brave to ask. Testing is the only way to know your status. Would you like to find a clinic?";

            } else if(userMessage.contains("prep")) {
                botReply = "PrEP is a daily pill that highly effectively prevents HIV. It is a great way to take control of your health.";

            } else if(userMessage.contains("clinic")) {
                botReply = "I can help you find a non-judgmental clinic nearby. Are you looking for testing or a general checkup?";

            } else {
                botReply = "I'm here to listen. Can you tell me a bit more about what's on your mind?";
            }
        }
        
        return ChatMessageResponse.builder()
                .reply(botReply)
                .source("RULE_BASED")
                .timestamp(Instant.now().toString())
                .build();
    }
}
