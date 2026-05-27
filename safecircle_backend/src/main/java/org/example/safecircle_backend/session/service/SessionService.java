package org.example.safecircle_backend.session.service;

import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.repository.AnonymousSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SessionService {

    private final AnonymousSessionRepository sessionRepository;

    private static final List<String> ADJECTIVES = List.of(
            "anonymous", "mysterious", "happy", "silent",
            "brave", "clever", "gentle", "sneaky", "swift"
    );

    private static final List<String> ANIMALS = List.of(
            "cheetah", "axolotl", "koala", "dolphin",
            "panda", "fox", "badger", "penguin", "lemur"
    );

    public SessionService(AnonymousSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
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
}
