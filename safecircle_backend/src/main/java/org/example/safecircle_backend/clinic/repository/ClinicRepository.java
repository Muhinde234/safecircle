package org.example.safecircle_backend.clinic.repository;

import org.example.safecircle_backend.clinic.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, UUID> {

    List<Clinic> findByDistrictIgnoreCase(String district);

    List<Clinic> findByYouthFriendly(boolean youthFriendly);

    List<Clinic> findByDistrictIgnoreCaseAndYouthFriendly(String district, boolean youthFriendly);

    @Query("SELECT DISTINCT c FROM Clinic c JOIN c.clinicServices s WHERE LOWER(s.id.serviceName) LIKE LOWER(CONCAT('%', :service, '%'))")
    List<Clinic> findByServiceNameContainingIgnoreCase(@Param("service") String service);
}
