package org.example.safecircle_backend.session.controller;

import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.example.safecircle_backend.session.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/anonymous")
    public ResponseEntity<SessionResponse> createAnonymousSession(@RequestBody(required = false) CreateSessionRequest request) {
        if(request == null){
            request = new CreateSessionRequest();
        }

        return new ResponseEntity<>(sessionService.createAnonymousSession(request), HttpStatus.CREATED);
    }
}
