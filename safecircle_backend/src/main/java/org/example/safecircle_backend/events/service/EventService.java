package org.example.safecircle_backend.events.service;

import org.example.safecircle_backend.events.model.EventType;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EventService {

//    public boolean isValidEventType(String eventType, String sessionId) {
//        return Arrays.stream(EventType.values())
//                .anyMatch(event -> event.name().equals(eventType))
//                && (sessionId != null);
//    }

    public boolean isValidEventType(String eventType) {
        return Arrays.stream(EventType.values())
                .anyMatch(event -> event.name().equals(eventType));
    }

    public void trackEvent(String sessionId, EventType eventType) {
        System.out.println("Anonymous Tracking - Session: " + sessionId + " | Event: " + eventType.toString());
    }
}
