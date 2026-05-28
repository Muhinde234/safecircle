package org.example.safecircle_backend.session.service;

import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.model.SessionBookmark;
import org.example.safecircle_backend.session.model.SessionBookmarkId;
import org.example.safecircle_backend.session.repository.AnonymousSessionRepository;
import org.example.safecircle_backend.session.repository.SessionBookmarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SessionService {

    private final AnonymousSessionRepository sessionRepository;
    private final SessionBookmarkRepository bookmarkRepository;

    private static final List<String> ADJECTIVES = List.of(
            "anonymous", "mysterious", "happy", "silent",
            "brave", "clever", "gentle", "sneaky", "swift"
    );

    private static final List<String> ANIMALS = List.of(
            "cheetah", "axolotl", "koala", "dolphin",
            "panda", "fox", "badger", "penguin", "lemur"
    );

    public SessionService(AnonymousSessionRepository sessionRepository,
                          SessionBookmarkRepository bookmarkRepository) {
        this.sessionRepository = sessionRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    private String generateGuestNickname() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String animal = ANIMALS.get(random.nextInt(ANIMALS.size()));
        return adjective + "_" + animal;
    }

    private String resolveNickname(String rawNickname) {
        if (rawNickname == null || rawNickname.isBlank()) {
            return generateGuestNickname();
        }
        if (rawNickname.trim().length() > 20) {
            throw new IllegalArgumentException(rawNickname.trim() + " is too long for a nickname. Please try again!");
        }
        return rawNickname.trim();
    }

    public SessionResponse createAnonymousSession(CreateSessionRequest request) {
        String nickname = resolveNickname(request.getNickname());

        AnonymousSession session = AnonymousSession.builder()
                .nickname(nickname)
                .language("en")
                .isPrivateSession(false)
                .build();

        AnonymousSession saved = sessionRepository.save(session);

        return SessionResponse.builder()
                .sessionId(saved.getId().toString())
                .nickname(saved.getNickname())
                .createdAt(saved.getCreatedAt().toString())
                .build();
    }

    public AnonymousSession getSessionById(String sessionId) {
        UUID id;
        try {
            id = UUID.fromString(sessionId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid session ID format.");
        }
        return sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Session not found. Please create a new session."));
    }

    public void addBookmark(String sessionId, String type, String targetId) {
        AnonymousSession session = getSessionById(sessionId);

        UUID targetUuid;
        try {
            targetUuid = UUID.fromString(targetId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid target ID format.");
        }

        String normalizedType = type.trim().toUpperCase();
        if (!normalizedType.equals("CLINIC") && !normalizedType.equals("CONTENT")) {
            throw new IllegalArgumentException("Bookmark type must be either CLINIC or CONTENT.");
        }

        SessionBookmarkId id = SessionBookmarkId.builder()
                .sessionId(session.getId())
                .bookmarkType(normalizedType)
                .targetId(targetUuid)
                .build();

        if (bookmarkRepository.existsById(id)) {
            return;
        }

        SessionBookmark bookmark = SessionBookmark.builder()
                .id(id)
                .session(session)
                .build();

        bookmarkRepository.save(bookmark);
    }

    public void removeBookmark(String sessionId, String type, String targetId) {
        AnonymousSession session = getSessionById(sessionId);

        UUID targetUuid;
        try {
            targetUuid = UUID.fromString(targetId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid target ID format.");
        }

        String normalizedType = type.trim().toUpperCase();

        SessionBookmarkId id = SessionBookmarkId.builder()
                .sessionId(session.getId())
                .bookmarkType(normalizedType)
                .targetId(targetUuid)
                .build();

        if (!bookmarkRepository.existsById(id)) {
            throw new IllegalArgumentException("Bookmark not found.");
        }

        bookmarkRepository.deleteById(id);
    }

    public List<SessionBookmark> getBookmarks(String sessionId) {
        AnonymousSession session = getSessionById(sessionId);
        return bookmarkRepository.findByIdSessionId(session.getId());
    }
}
