package org.example.safecircle_backend.session;

import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.repository.AnonymousSessionRepository;
import org.example.safecircle_backend.session.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private AnonymousSessionRepository sessionRepository;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(sessionRepository);
    }

    private AnonymousSession buildSavedSession(String nickname) {
        AnonymousSession session = AnonymousSession.builder()
                .nickname(nickname)
                .language("en")
                .isPrivateSession(false)
                .build();
        // Simulate DB-assigned fields
        try {
            var idField = AnonymousSession.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(session, UUID.randomUUID());
            var createdAtField = AnonymousSession.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(session, OffsetDateTime.now());
        } catch (Exception ignored) {}
        return session;
    }

    @Test
    void shouldCreateSessionWithProvidedNickname() {
        AnonymousSession saved = buildSavedSession("Nicole");
        Mockito.when(sessionRepository.save(Mockito.any(AnonymousSession.class))).thenReturn(saved);

        SessionResponse response = sessionService.createAnonymousSession(new CreateSessionRequest("Nicole"));

        assertNotNull(response.getSessionId());
        assertEquals("Nicole", response.getNickname());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void shouldGenerateFallbackNicknameWhenBlank() {
        Mockito.when(sessionRepository.save(Mockito.any(AnonymousSession.class)))
                .thenAnswer(inv -> {
                    AnonymousSession s = inv.getArgument(0);
                    try {
                        var idField = AnonymousSession.class.getDeclaredField("id");
                        idField.setAccessible(true);
                        idField.set(s, UUID.randomUUID());
                        var createdAtField = AnonymousSession.class.getDeclaredField("createdAt");
                        createdAtField.setAccessible(true);
                        createdAtField.set(s, OffsetDateTime.now());
                    } catch (Exception ignored) {}
                    return s;
                });

        SessionResponse response = sessionService.createAnonymousSession(new CreateSessionRequest("   "));

        assertNotNull(response.getSessionId());
        assertNotNull(response.getNickname());
        assertTrue(response.getNickname().contains("_"));
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void shouldThrowWhenNicknameTooLong() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> sessionService.createAnonymousSession(new CreateSessionRequest("thisNicknameIsWayTooLongToAccept"))
        );
        assertTrue(ex.getMessage().contains("too long for a nickname"));
    }
}
