package org.example.safecircle_backend.clinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "clinic")
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 120)
    @NotNull
    @Column(name = "district", nullable = false, length = 120)
    private String district;

    @Size(max = 255)
    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @Size(max = 120)
    @NotNull
    @Column(name = "contact_info", nullable = false, length = 120)
    private String contactInfo;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "youth_friendly", nullable = false)
    private Boolean youthFriendly = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "no_judgment", nullable = false)
    private Boolean noJudgment = false;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "anonymous_visits", nullable = false)
    private Boolean anonymousVisits = false;

    @NotNull
    @Column(name = "what_to_expect", nullable = false, length = Integer.MAX_VALUE)
    private String whatToExpect;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @CreationTimestamp
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClinicService> clinicServices = new LinkedHashSet<>();

    public void addService(ClinicService service) {
        this.clinicServices.add(service);
        service.setClinic(this);

        // Check if the ID object exists; if not, create it
        if (service.getId() == null) {
            service.setId(new ClinicServiceId());
        }

        // Sync the clinicId from the parent to the child's composite key
        service.getId().setClinicId(this.id);
    }

    public void removeService(ClinicService service) {
        this.clinicServices.remove(service);
        service.setClinic(null);
    }

}