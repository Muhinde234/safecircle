package org.example.safecircle_backend.events.controller;

import jakarta.validation.Valid;
import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> trackEvent (
            @Valid @RequestBody TrackEventRequest request
    ){
        if (!eventService.isValidEventType(String.valueOf(request.getEventType()))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid Event Type"));
        }

        eventService.trackEvent(request.getSessionId(), request.getEventType());

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("status", "Event Recorded"));
    }
}
