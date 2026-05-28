package org.example.safecircle_backend.chat.controller;

import org.example.safecircle_backend.chat.dto.FlagMessageRequest;
import org.example.safecircle_backend.chat.dto.ModeratedMessageResponse;
import org.example.safecircle_backend.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/moderation/chat-messages")
public class ModerationController {

    private final ChatService chatService;

    public ModerationController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PutMapping("/{messageId}/flag")
    public ResponseEntity<ModeratedMessageResponse> flagMessage(
            @PathVariable UUID messageId,
            @RequestBody(required = false) FlagMessageRequest request,
            @RequestParam(value = "notes", required = false) String paramNotes) {
        
        String notes = null;
        if (request != null && request.getModerationNotes() != null) {
            notes = request.getModerationNotes();
        } else if (paramNotes != null) {
            notes = paramNotes;
        }
        
        return ResponseEntity.ok(chatService.flagMessage(messageId, notes));
    }

    @GetMapping("/flagged")
    public ResponseEntity<List<ModeratedMessageResponse>> getFlaggedMessages() {
        return ResponseEntity.ok(chatService.getFlaggedMessages());
    }
}
