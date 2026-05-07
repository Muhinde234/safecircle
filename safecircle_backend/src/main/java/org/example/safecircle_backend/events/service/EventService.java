package org.example.safecircle_backend.events.service;

import jakarta.validation.constraints.Size;
import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.model.EventType;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class EventService {

    private static final String STATUS_RECORDED = "RECORDED";
    private static final List<String> PII_KEYS = Arrays.asList("email", "phone", "name", "address");
    private final List<TrackEventResponse> eventLog = new ArrayList<>();

    private TrackEventResponse eventResponse(TrackEventRequest request) {
        if (request.getEventType() == null) {
            throw new IllegalArgumentException("Invalid event type null");
        }

        return TrackEventResponse.builder()
                .sessionId(request.getSessionId())
                .eventType(request.getEventType())
                .status(STATUS_RECORDED)
                .recordedAt(Instant.now().toString())
                .build();
    }

    public TrackEventResponse trackEvent(TrackEventRequest request) {
        Map<String,String> sanitizedMetadata = sanitize(request.getMetadata());
        TrackEventResponse response = eventResponse(request);

        eventLog.add(response);

        System.out.println("Event Recorded: " + response.getEventType() + " for session " + response.getSessionId());

        return response;
    }

    private Map<String, String> sanitize(Map<String,String> metadata) {
        if (metadata == null) return null;

        Map<String, String> clean = new HashMap<>(metadata);

        PII_KEYS.forEach(key -> clean.computeIfPresent(key, (k, v) -> "[MASKED]"));

        return clean;
    }

    public List<TrackEventResponse> viewEventLogs() {
        return eventLog;
    }
}
