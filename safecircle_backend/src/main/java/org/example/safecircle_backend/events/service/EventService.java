package org.example.safecircle_backend.events.service;

import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.model.EventLog;
import org.example.safecircle_backend.events.repository.EventLogRepository;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class EventService {

    private final EventLogRepository eventLogRepo;

    public EventService(EventLogRepository eventLogRepo) {
        this.eventLogRepo = eventLogRepo;
    }

    private static final String STATUS_RECORDED = "RECORDED";
    private static final List<String> PII_KEYS = Arrays.asList("email", "phone", "name", "address");
    private final List<TrackEventResponse> eventLog = new ArrayList<>();

    private Map<String, String> sanitize(Map<String,String> metadata) {
        if (metadata == null) return null;

        Map<String, String> clean = new HashMap<>(metadata);

        PII_KEYS.forEach(key -> clean.computeIfPresent(key, (k, v) -> "[MASKED]"));

        return clean;
    }

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

        EventLog log = EventLog.builder()
                .session(new AnonymousSession())
                .status("RECORDED")
                .eventType(request.getEventType())
                .metadata(request.getMetadata())
                .recordedAt(Instant.now().atOffset(java.time.ZoneOffset.UTC))
                .build();

        EventLog eventLog = eventLogRepo.save(log);

        return response;
    }

    public List<TrackEventResponse> viewEventLogs() {
        return eventLog;
    }
}
