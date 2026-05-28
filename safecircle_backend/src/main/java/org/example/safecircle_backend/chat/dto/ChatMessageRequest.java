package org.example.safecircle_backend.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    @NotNull(message = "You don't have an active session. " +
            "Please create a nickname to continue.")
    @NotBlank(message = "You don't have an active session. " +
            "Please create a nickname to continue.")
    private String sessionId;

    @NotNull(message = "Something went wrong. " +
            "Please send the message again")
    @NotBlank(message = "Something went wrong. " +
            "Please send the message again")
    private String message;

    private String language;
}
