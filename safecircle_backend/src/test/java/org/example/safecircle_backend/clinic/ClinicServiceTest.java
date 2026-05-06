package org.example.safecircle_backend.clinic;

import org.example.safecircle_backend.clinic.dto.ClinicResponse;
import org.example.safecircle_backend.clinic.service.ClinicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClinicServiceTest {

    private ClinicService clinicService;

    @BeforeEach
    void setUp() {
        clinicService = new ClinicService();
    }

    @Test
    void shouldReturnAllClinicsWhenNoFiltersProvided() {
        List<ClinicResponse> clinics = clinicService.getClinics(null, null, null);

        assertNotNull(clinics);
        assertEquals(3, clinics.size());
    }

    @Test
    void shouldFilterClinicsByDistrict() {
        List<ClinicResponse> clinics = clinicService.getClinics("Gasabo", null, null);

        assertEquals(1, clinics.size());
        assertEquals("Gasabo", clinics.getFirst().getDistrict());
    }

    @Test
    void shouldFilterClinicsByYouthFriendlyFlag() {
        List<ClinicResponse> clinics = clinicService.getClinics(null, true, null);

        assertEquals(2, clinics.size());
        assertTrue(clinics.stream().allMatch(ClinicResponse::isYouthFriendly));
    }

    @Test
    void shouldFilterClinicsByService() {
        List<ClinicResponse> clinics = clinicService.getClinics(null, null, "Family Planning");

        assertEquals(3, clinics.size());
        assertTrue(clinics.stream().allMatch(c -> c.getServices().stream()
                .anyMatch(s -> s.equalsIgnoreCase("Family Planning"))));
    }

    @Test
    void shouldApplyMultipleFiltersTogether() {
        List<ClinicResponse> clinics = clinicService.getClinics("Nyarugenge", true, "Family Planning");

        assertEquals(1, clinics.size());
        ClinicResponse clinic = clinics.getFirst();
        assertEquals("Nyarugenge", clinic.getDistrict());
        assertTrue(clinic.isYouthFriendly());
    }
}
