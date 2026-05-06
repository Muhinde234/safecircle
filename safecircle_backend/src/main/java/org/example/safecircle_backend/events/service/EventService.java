package org.example.safecircle_backend.events.service;

import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.model.EventType;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EventService {

    private boolean isValidEventType(String eventType) {
        if(eventType == null) return false;
        return Arrays.stream(EventType.values())
                .anyMatch(event -> event.name().equals(eventType));
    }

    private TrackEventResponse eventResponse(TrackEventRequest request) {
        if(request.getEventType() == null || !isValidEventType(request.getEventType().toString())){
            throw new IllegalArgumentException("Invalid event type " + request.getEventType());
        }

        return TrackEventResponse.builder()
                .sessionId(request.getSessionId())
                .eventType(request.getEventType())
                .status("Success")
                .recordedAt(Instant.now().toString())
                .build();
    }

    private final List<TrackEventResponse> eventLog = new ArrayList<>();

    public TrackEventResponse trackEvent(TrackEventRequest request) {
        TrackEventResponse response = eventResponse(request);

        eventLog.add(response);

        System.out.println("Event Recorded: " + response.getEventType() + " for session " + response.getSessionId());

        return response;
    }

    public List<TrackEventResponse> viewEventLogs() {
        return eventLog;
    }
}
