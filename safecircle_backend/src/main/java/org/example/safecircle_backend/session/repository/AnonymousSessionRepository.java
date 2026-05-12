package org.example.safecircle_backend.session.repository;

import org.example.safecircle_backend.session.model.AnonymousSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnonymousSessionRepository extends JpaRepository<AnonymousSession, UUID> {
}
