package org.example.safecircle_backend.risk.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "risk_assessment")
public class RiskAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "session_id", nullable = false)
    private AnonymousSession session;

    @Size(max = 120)
    @NotNull
    @Column(name = "event_type_label", nullable = false, length = 120)
    private String eventTypeLabel;

    @NotNull
    @Column(name = "hours_since_event", nullable = false)
    private Integer hoursSinceEvent;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "symptoms_present", nullable = false)
    private Boolean symptomsPresent = false;

    @NotNull
    @Column(name = "recommended_action", nullable = false, length = Integer.MAX_VALUE)
    private String recommendedAction;

    @Size(max = 120)
    @NotNull
    @Column(name = "urgency_window", nullable = false, length = 120)
    private String urgencyWindow;

    @CreationTimestamp
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @Column(name = "risk_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
}