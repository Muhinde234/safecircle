package org.example.safecircle_backend.session.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "anonymous_session")
public class AnonymousSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 64)
    @NotNull
    @Column(name = "nickname", nullable = false, length = 64)
    private String nickname;

    @Size(max = 8)
    @NotNull
    @ColumnDefault("'en'")
    @Column(name = "language", nullable = false, length = 8)
    private String language;

    @NotNull
    @ColumnDefault("false")
    @Builder.Default
    @Column(name = "is_private_session", nullable = false)
    private Boolean isPrivateSession = false;

    @CreationTimestamp
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}