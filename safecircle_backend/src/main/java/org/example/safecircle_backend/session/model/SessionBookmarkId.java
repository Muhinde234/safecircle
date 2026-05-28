package org.example.safecircle_backend.session.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SessionBookmarkId implements Serializable {

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "bookmark_type", nullable = false, length = 32)
    private String bookmarkType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionBookmarkId that = (SessionBookmarkId) o;
        return Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(bookmarkType, that.bookmarkType) &&
                Objects.equals(targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, bookmarkType, targetId);
    }
}
