package org.example.safecircle_backend.chat.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "session_id", nullable = false)
    private AnonymousSession session;

    @NotNull
    @Column(name = "message_text", nullable = false, length = Integer.MAX_VALUE)
    private String messageText;
    @Column(name = "metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;

    @Size(max = 8)
    @NotNull
    @ColumnDefault("'en'")
    @Column(name = "language", nullable = false, length = 8)
    private String language;

    @CreationTimestamp
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatRole role;

    @Column(name = "source")
    @Enumerated(EnumType.STRING)
    private ChatSource source;

    @Column(name = "is_flagged")
    private Boolean isFlagged = false;

    @Column(name = "moderation_notes")
    private String moderationNotes;
}