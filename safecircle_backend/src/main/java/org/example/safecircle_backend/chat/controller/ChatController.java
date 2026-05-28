package org.example.safecircle_backend.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.safecircle_backend.chat.dto.ChatHistoryResponse;
import org.example.safecircle_backend.chat.dto.ChatMessageRequest;
import org.example.safecircle_backend.chat.dto.ChatMessageResponse;
import org.example.safecircle_backend.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat Operations", description = "Interact with the anonymous SRH support AI chatbot")
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Send a message and get an AI response", description = "Sends a chat message to the support bot and gets back an automated response.")
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> reply(@RequestBody @Valid ChatMessageRequest request) {
        return ResponseEntity.ok(chatService.reply(request));
    }

    @Operation(summary = "Get session chat history", description = "Retrieves the full list of messages sent and received within a valid session.")
    @GetMapping("/history")
    public ResponseEntity<List<ChatHistoryResponse>> getHistory(@RequestParam String sessionId) {
        return ResponseEntity.ok(chatService.getHistory(sessionId));
    }
}
