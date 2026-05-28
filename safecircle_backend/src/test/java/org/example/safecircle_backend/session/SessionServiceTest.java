package org.example.safecircle_backend.session;

import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.repository.AnonymousSessionRepository;
import org.example.safecircle_backend.session.repository.SessionBookmarkRepository;
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

    @Mock
    private SessionBookmarkRepository bookmarkRepository;

    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(sessionRepository, bookmarkRepository);
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

    @Test
    void shouldAddBookmark() {
        UUID sessionUuid = UUID.randomUUID();
        AnonymousSession session = AnonymousSession.builder().id(sessionUuid).nickname("nina").build();
        Mockito.when(sessionRepository.findById(sessionUuid)).thenReturn(java.util.Optional.of(session));

        UUID targetUuid = UUID.randomUUID();
        sessionService.addBookmark(sessionUuid.toString(), "CLINIC", targetUuid.toString());

        Mockito.verify(bookmarkRepository).save(Mockito.any());
    }

    @Test
    void shouldRemoveBookmark() {
        UUID sessionUuid = UUID.randomUUID();
        AnonymousSession session = AnonymousSession.builder().id(sessionUuid).nickname("nina").build();
        Mockito.when(sessionRepository.findById(sessionUuid)).thenReturn(java.util.Optional.of(session));

        UUID targetUuid = UUID.randomUUID();
        org.example.safecircle_backend.session.model.SessionBookmarkId id = org.example.safecircle_backend.session.model.SessionBookmarkId.builder()
                .sessionId(sessionUuid)
                .bookmarkType("CLINIC")
                .targetId(targetUuid)
                .build();
        Mockito.when(bookmarkRepository.existsById(id)).thenReturn(true);

        sessionService.removeBookmark(sessionUuid.toString(), "CLINIC", targetUuid.toString());

        Mockito.verify(bookmarkRepository).deleteById(id);
    }
}
