package org.example.safecircle_backend.events.service;

import lombok.extern.slf4j.Slf4j;
import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.model.EventLog;
import org.example.safecircle_backend.events.repository.EventLogRepository;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.repository.AnonymousSessionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Service
public class EventService {

    private final EventLogRepository eventLogRepo;
    private final AnonymousSessionRepository sessionRepository;

    private static final String STATUS_RECORDED = "RECORDED";
    private static final List<String> PII_KEYS = List.of("email", "phone", "name", "address");
    private final List<TrackEventResponse> eventLog = new ArrayList<>();

    public EventService(EventLogRepository eventLogRepo, AnonymousSessionRepository sessionRepository) {
        this.eventLogRepo = eventLogRepo;
        this.sessionRepository = sessionRepository;
    }

    private Map<String, String> sanitize(Map<String, String> metadata) {
        if (metadata == null) return null;
        Map<String, String> clean = new HashMap<>(metadata);
        PII_KEYS.forEach(key -> clean.computeIfPresent(key, (k, v) -> "[MASKED]"));
        return clean;
    }

    public TrackEventResponse trackEvent(TrackEventRequest request) {
        if (request.getEventType() == null) {
            throw new IllegalArgumentException("Invalid event type null");
        }

        UUID sessionId;
        try {
            sessionId = UUID.fromString(request.getSessionId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid session ID format.");
        }

        AnonymousSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Session not found. Please create a new session."));

        Map<String, String> sanitizedMetadata = sanitize(request.getMetadata());

        EventLog eventLog = EventLog.builder()
                .session(session)
                .status(STATUS_RECORDED)
                .eventType(request.getEventType())
                .metadata(sanitizedMetadata)
                .recordedAt(Instant.now().atOffset(ZoneOffset.UTC))
                .build();

        eventLogRepo.save(eventLog);

        TrackEventResponse response = TrackEventResponse.builder()
                .sessionId(request.getSessionId())
                .eventType(request.getEventType())
                .status(STATUS_RECORDED)
                .recordedAt(Instant.now().toString())
                .build();

        this.eventLog.add(response);
        log.info("Event recorded: {} for session {}", request.getEventType(), request.getSessionId());

        return response;
    }

    public List<TrackEventResponse> viewEventLogs() {
        return eventLog;
    }
}
