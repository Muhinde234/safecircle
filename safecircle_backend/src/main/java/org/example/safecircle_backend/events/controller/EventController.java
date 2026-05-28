package org.example.safecircle_backend.events.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Event Tracking", description = "Track anonymous user interactions and application events")
@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Track a user interaction/event", description = "Logs an event linked to a valid anonymous session (e.g., page views, button clicks) with event metadata.")
    @PostMapping
    public ResponseEntity<TrackEventResponse> trackEvent (
            @Valid @RequestBody TrackEventRequest request
    ){
        return new ResponseEntity<>(eventService.trackEvent(request), HttpStatus.CREATED);
    }

    @Operation(summary = "View logged events", description = "Retrieves a list of tracked event logs from the system for analytics.")
    @GetMapping
    public ResponseEntity<List<TrackEventResponse>> viewEventLogs(){
        return new ResponseEntity<>(eventService.viewEventLogs(), HttpStatus.OK);
    }
}
