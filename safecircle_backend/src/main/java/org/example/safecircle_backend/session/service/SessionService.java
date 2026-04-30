package org.example.safecircle_backend.session.service;

import org.example.safecircle_backend.session.dto.CreateSessionRequest;
import org.example.safecircle_backend.session.dto.SessionResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SessionService {

    // 1. Defined a list of adjectives
    private static final List<String> ADJECTIVES = List.of(
            "anonymous", "mysterious", "happy", "silent",
            "brave", "clever", "gentle", "sneaky", "swift"
    );

    // 2. Defined a list of animals
    private static final List<String> ANIMALS = List.of(
            "cheetah", "axolotl", "koala", "dolphin",
            "panda", "fox", "badger", "penguin", "lemur"
    );

    // Generates a nickname when none is provided
    private String generateGuestNickname(){
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Picks a random index for both lists
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String animal = ANIMALS.get(random.nextInt(ANIMALS.size()));

        // Join them together to form an anonymous user
        return adjective + "_" + animal;

    }

    // Used to handle incoming nicknames (trims spaces or generates a new one if blank)
    private String resolveNickname(String rawNickname){
        if(rawNickname == null || rawNickname.isBlank()){
            return generateGuestNickname();
        } if(rawNickname.length() > 20){
            throw new IllegalArgumentException(rawNickname.trim() + " is too long for a nickname." + " Please try again!");
        }
        return rawNickname.trim();
    }

    // Generate a Session response
    private SessionResponse generateSessionResponse(String nickname){
        return SessionResponse.builder()
                .nickname(nickname)
                .sessionId(UUID.randomUUID().toString())
                .createdAt(Instant.now().toString())
                .build();
    }

    // Uses the nickname to create an anonymous session
    public SessionResponse createAnonymousSession(CreateSessionRequest createSessionRequest) {

        return generateSessionResponse(resolveNickname(createSessionRequest.getNickname()));
    }

}
