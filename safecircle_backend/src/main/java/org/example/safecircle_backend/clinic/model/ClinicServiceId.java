package org.example.safecircle_backend.clinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class ClinicServiceId implements Serializable {
    private static final long serialVersionUID = 6304920970117923600L;
    @NotNull
    @Column(name = "clinic_id", nullable = false)
    private UUID clinicId;

    @Size(max = 120)
    @NotNull
    @Column(name = "service_name", nullable = false, length = 120)
    private String serviceName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClinicServiceId entity = (ClinicServiceId) o;
        return Objects.equals(this.clinicId, entity.clinicId) &&
                Objects.equals(this.serviceName, entity.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clinicId, serviceName);
    }

}