package org.example.safecircle_backend.session.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.safecircle_backend.session.dto.BookmarkResponse;
import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.example.safecircle_backend.session.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Session Management", description = "Create and manage anonymous user sessions and preferences/bookmarks")
@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(summary = "Create an anonymous session", description = "Generates a new anonymous session with a given nickname (or default) and preferred language.")
    @PostMapping("/anonymous")
    public ResponseEntity<SessionResponse> createAnonymousSession(@RequestBody(required = false) CreateSessionRequest request) {
        if(request == null){
            request = new CreateSessionRequest();
        }

        return new ResponseEntity<>(sessionService.createAnonymousSession(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Add a bookmark", description = "Saves a reference to a clinic or educational content item for the session.")
    @PostMapping("/{sessionId}/bookmarks")
    public ResponseEntity<Void> addBookmark(@PathVariable String sessionId,
                                            @RequestParam String type,
                                            @RequestParam String targetId) {
        sessionService.addBookmark(sessionId, type, targetId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Remove a bookmark", description = "Deletes a saved bookmark from the session.")
    @DeleteMapping("/{sessionId}/bookmarks")
    public ResponseEntity<Void> removeBookmark(@PathVariable String sessionId,
                                               @RequestParam String type,
                                               @RequestParam String targetId) {
        sessionService.removeBookmark(sessionId, type, targetId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get bookmarks", description = "Retrieves all saved bookmarks (clinics and articles) for this session.")
    @GetMapping("/{sessionId}/bookmarks")
    public ResponseEntity<List<BookmarkResponse>> getBookmarks(@PathVariable String sessionId) {
        List<BookmarkResponse> bookmarks = sessionService.getBookmarks(sessionId).stream()
                .map(b -> BookmarkResponse.builder()
                        .bookmarkType(b.getId().getBookmarkType())
                        .targetId(b.getId().getTargetId().toString())
                        .createdAt(b.getCreatedAt() != null ? b.getCreatedAt().toString() : null)
                        .build())
                .toList();
        return new ResponseEntity<>(bookmarks, HttpStatus.OK);
    }
}
