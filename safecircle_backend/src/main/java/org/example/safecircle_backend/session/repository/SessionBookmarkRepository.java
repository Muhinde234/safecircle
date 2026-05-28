package org.example.safecircle_backend.session.repository;

import org.example.safecircle_backend.session.model.SessionBookmark;
import org.example.safecircle_backend.session.model.SessionBookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionBookmarkRepository extends JpaRepository<SessionBookmark, SessionBookmarkId> {
    List<SessionBookmark> findByIdSessionId(UUID sessionId);
}
