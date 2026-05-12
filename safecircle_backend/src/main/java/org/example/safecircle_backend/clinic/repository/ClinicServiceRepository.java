package org.example.safecircle_backend.clinic.repository;

import org.example.safecircle_backend.clinic.model.ClinicService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClinicServiceRepository extends JpaRepository<ClinicService, UUID> {
}
