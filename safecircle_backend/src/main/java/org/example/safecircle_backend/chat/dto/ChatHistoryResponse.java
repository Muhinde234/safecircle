package org.example.safecircle_backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryResponse {
    private String id;
    private String role;
    private String message;
    private String source;
    private String language;
    private String createdAt;
}
