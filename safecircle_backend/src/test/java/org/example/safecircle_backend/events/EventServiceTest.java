package org.example.safecircle_backend.events;

import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.model.EventLog;
import org.example.safecircle_backend.events.model.EventType;
import org.example.safecircle_backend.events.repository.EventLogRepository;
import org.example.safecircle_backend.events.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventLogRepository eventLogRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventLogRepository);
    }

    @Test
    void shouldTrackValidEvent() {
        Mockito.when(eventLogRepository.save(Mockito.any(EventLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TrackEventRequest request = TrackEventRequest.builder()
                .sessionId("session-123")
                .eventType(EventType.CHAT_SENT)
                .metadata(Map.of("screen", "chat"))
                .build();

        TrackEventResponse response = eventService.trackEvent(request);

        assertNotNull(response);
        assertEquals("RECORDED", response.getStatus());
        assertEquals(EventType.CHAT_SENT, response.getEventType());
        assertEquals("session-123", response.getSessionId());
        assertNotNull(response.getRecordedAt());
    }

    @Test
    void shouldAppendToEventLogAfterTracking() {
        Mockito.when(eventLogRepository.save(Mockito.any(EventLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TrackEventRequest request = TrackEventRequest.builder()
                .sessionId("session-456")
                .eventType(EventType.CLINIC_OPENED)
                .build();

        eventService.trackEvent(request);

        assertEquals(1, eventService.viewEventLogs().size());
        assertEquals(EventType.CLINIC_OPENED, eventService.viewEventLogs().getFirst().getEventType());
    }

    @Test
    void shouldThrowForNullEventType() {
        TrackEventRequest request = TrackEventRequest.builder()
                .sessionId("session-789")
                .eventType(null)
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.trackEvent(request)
        );

        assertEquals("Invalid event type null", ex.getMessage());
    }
}
