package org.example.safecircle_backend.events;

import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.model.EventLog;
import org.example.safecircle_backend.events.model.EventType;
import org.example.safecircle_backend.events.repository.EventLogRepository;
import org.example.safecircle_backend.events.service.EventService;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.repository.AnonymousSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventLogRepository eventLogRepository;

    @Mock
    private AnonymousSessionRepository sessionRepository;

    private EventService eventService;

    private static final String SESSION_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventLogRepository, sessionRepository);
    }

    private void stubSession() {
        AnonymousSession session = AnonymousSession.builder()
                .nickname("test_user")
                .language("en")
                .isPrivateSession(false)
                .build();
        Mockito.when(sessionRepository.findById(UUID.fromString(SESSION_ID)))
                .thenReturn(Optional.of(session));
        Mockito.when(eventLogRepository.save(Mockito.any(EventLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void shouldTrackValidEvent() {
        stubSession();

        TrackEventRequest request = TrackEventRequest.builder()
                .sessionId(SESSION_ID)
                .eventType(EventType.CHAT_SENT)
                .metadata(Map.of("screen", "chat"))
                .build();

        TrackEventResponse response = eventService.trackEvent(request);

        assertNotNull(response);
        assertEquals("RECORDED", response.getStatus());
        assertEquals(EventType.CHAT_SENT, response.getEventType());
        assertEquals(SESSION_ID, response.getSessionId());
        assertNotNull(response.getRecordedAt());
    }

    @Test
    void shouldAppendToEventLogAfterTracking() {
        stubSession();

        TrackEventRequest request = TrackEventRequest.builder()
                .sessionId(SESSION_ID)
                .eventType(EventType.CLINIC_OPENED)
                .build();

        eventService.trackEvent(request);

        assertEquals(1, eventService.viewEventLogs().size());
        assertEquals(EventType.CLINIC_OPENED, eventService.viewEventLogs().getFirst().getEventType());
    }

    @Test
    void shouldThrowForNullEventType() {
        TrackEventRequest request = TrackEventRequest.builder()
                .sessionId(SESSION_ID)
                .eventType(null)
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.trackEvent(request)
        );
        assertEquals("Invalid event type null", ex.getMessage());
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        Mockito.when(sessionRepository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.empty());

        TrackEventRequest request = TrackEventRequest.builder()
                .sessionId(SESSION_ID)
                .eventType(EventType.CONTENT_VIEW)
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.trackEvent(request)
        );
        assertTrue(ex.getMessage().contains("Session not found"));
    }
}
