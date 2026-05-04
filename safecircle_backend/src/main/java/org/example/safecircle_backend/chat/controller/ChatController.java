package org.example.safecircle_backend.chat.controller;

import jakarta.validation.Valid;
import org.example.safecircle_backend.chat.dto.ChatMessageRequest;
import org.example.safecircle_backend.chat.dto.ChatMessageResponse;
import org.example.safecircle_backend.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    private ChatController (ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> reply(@RequestBody @Valid ChatMessageRequest request){
        if(request.getSessionId() == null || request.getSessionId().isBlank()){
            throw new IllegalArgumentException("You don't have an active session. " +
                    "Please create a nickname to continue.");

        } if(request.getMessage() == null || request.getMessage().trim().isEmpty()){
            throw new IllegalArgumentException("Something went wrong. " +
                    "Please send the message again");
        }

        return ResponseEntity.ok(chatService.reply(request));
    }
}
