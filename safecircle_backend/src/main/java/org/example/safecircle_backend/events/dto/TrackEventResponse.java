package org.example.safecircle_backend.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.safecircle_backend.events.model.EventType;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackEventResponse {
    private String status;
    private EventType eventType;
    private String recordedAt;
    private String sessionId;
}
