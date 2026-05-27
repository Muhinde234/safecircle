package org.example.safecircle_backend.events.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "event_log")
@NoArgsConstructor
@AllArgsConstructor
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "session_id", nullable = false)
    private AnonymousSession session;

    @Size(max = 32)
    @NotNull
    @ColumnDefault("'RECORDED'")
    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> metadata;

    @CreationTimestamp
    @NotNull
    @ColumnDefault("now()")
    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @Column(name = "event_type", columnDefinition = "event_type not null")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

}