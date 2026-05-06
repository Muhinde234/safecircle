package org.example.safecircle_backend.events.controller;

import jakarta.validation.Valid;
import org.example.safecircle_backend.events.dto.TrackEventRequest;
import org.example.safecircle_backend.events.dto.TrackEventResponse;
import org.example.safecircle_backend.events.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<TrackEventResponse> trackEvent (
            @Valid @RequestBody TrackEventRequest request
    ){
        return new ResponseEntity<>(eventService.trackEvent(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TrackEventResponse>> viewEventLogs(){
        return new ResponseEntity<>(eventService.viewEventLogs(), HttpStatus.OK);
    }
}
