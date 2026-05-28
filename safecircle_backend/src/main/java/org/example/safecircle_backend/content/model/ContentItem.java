package org.example.safecircle_backend.content.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "content_item")
public class ContentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary", length = Integer.MAX_VALUE)
    private String summary;

    @Column(name = "body", length = Integer.MAX_VALUE)
    private String body;

    @Size(max = 32)
    @NotNull
    @ColumnDefault("'TEXT'")
    @Column(name = "content_type", nullable = false, length = 32)
    private String contentType;

    @Size(max = 64)
    @NotNull
    @Column(name = "category", nullable = false, length = 64)
    private String category;

    @Size(max = 8)
    @NotNull
    @ColumnDefault("'en'")
    @Column(name = "language", nullable = false, length = 8)
    private String language;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "published")
    private Boolean published = true;

    @Size(max = 255)
    @Column(name = "audio_url", length = 255)
    private String audioUrl;

    @CreationTimestamp
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}