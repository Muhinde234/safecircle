package org.example.safecircle_backend.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.safecircle_backend.events.model.EventType;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackEventRequest {
    @NotNull(message = "A  session id must provided.")
    @NotBlank(message = "A  session id must provided.")
    private String sessionId;

    @NotNull(message = "An event type must provided")
    private EventType eventType;

    private Map<String,String> metadata;
}
