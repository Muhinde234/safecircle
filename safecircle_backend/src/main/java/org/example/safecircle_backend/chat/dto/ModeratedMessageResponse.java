package org.example.safecircle_backend.chat.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModeratedMessageResponse {
    private String id;
    private String sessionId;
    private String nickname;
    private String messageText;
    private String createdAt;
    private Boolean isFlagged;
    private String moderationNotes;
}
