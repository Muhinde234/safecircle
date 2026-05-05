package org.example.safecircle_backend.session;

import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.example.safecircle_backend.session.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService();
    }

    @Test
    void shouldCreateSessionWithProvidedNickname() {
        CreateSessionRequest request = new CreateSessionRequest("Nicole");

        SessionResponse response = sessionService.createAnonymousSession(request);

        assertNotNull(response.getSessionId());
        assertEquals("Nicole", response.getNickname());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void shouldGenerateFallbackNicknameWhenBlank() {
        CreateSessionRequest request = new CreateSessionRequest("   ");

        SessionResponse response = sessionService.createAnonymousSession(request);

        assertNotNull(response.getSessionId());
        assertNotNull(response.getNickname());
        assertTrue(response.getNickname().contains("_"));
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void shouldThrowWhenNicknameTooLong() {
        CreateSessionRequest request = new CreateSessionRequest("thisNicknameIsWayTooLongToAccept");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sessionService.createAnonymousSession(request)
        );

        assertTrue(ex.getMessage().contains("too long for a nickname"));
    }
}
