package org.example.safecircle_backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String reply;
    private String source; // Will be "RULE_BASED"
    private String timestamp;

}
