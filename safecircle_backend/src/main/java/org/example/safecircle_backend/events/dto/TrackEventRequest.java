package org.example.safecircle_backend.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.safecircle_backend.events.model.EventType;

import java.sql.Timestamp;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackEventRequest {
    private String sessionId;
    private EventType eventType;
    private Map<String,String> metadata;
    private Timestamp timestamp;
}
