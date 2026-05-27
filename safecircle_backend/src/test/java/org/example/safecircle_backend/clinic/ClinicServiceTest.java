package org.example.safecircle_backend.clinic;

import org.example.safecircle_backend.clinic.dto.ClinicResponse;
import org.example.safecircle_backend.clinic.model.Clinic;
import org.example.safecircle_backend.clinic.model.ClinicServiceId;
import org.example.safecircle_backend.clinic.repository.ClinicRepository;
import org.example.safecircle_backend.clinic.service.ClinicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClinicServiceTest {

    @Mock
    private ClinicRepository clinicRepository;

    private ClinicService clinicService;

    @BeforeEach
    void setUp() {
        clinicService = new ClinicService(clinicRepository);
    }

    private Clinic buildClinic(String name, String district, boolean youthFriendly, String... services) {
        Clinic clinic = new Clinic();
        try {
            var idField = Clinic.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(clinic, UUID.randomUUID());
        } catch (Exception ignored) {}
        clinic.setName(name);
        clinic.setDistrict(district);
        clinic.setYouthFriendly(youthFriendly);
        clinic.setAddress("KG 1 ST");
        clinic.setContactInfo("+250780000000");
        clinic.setWhatToExpect("Confidential support");
        clinic.setNoJudgment(true);
        clinic.setAnonymousVisits(true);

        LinkedHashSet<org.example.safecircle_backend.clinic.model.ClinicService> svcSet = new LinkedHashSet<>();
        for (String svc : services) {
            org.example.safecircle_backend.clinic.model.ClinicService cs = new org.example.safecircle_backend.clinic.model.ClinicService();
            ClinicServiceId csId = new ClinicServiceId();
            csId.setClinicId(clinic.getId());
            csId.setServiceName(svc);
            cs.setId(csId);
            cs.setClinic(clinic);
            svcSet.add(cs);
        }
        clinic.setClinicServices(svcSet);
        return clinic;
    }

    @Test
    void shouldReturnAllClinicsWhenNoFiltersProvided() {
        List<Clinic> all = List.of(
                buildClinic("Clinic A", "Gasabo", true, "Family Planning"),
                buildClinic("Clinic B", "Kicukiro", false, "STI Testing")
        );
        Mockito.when(clinicRepository.findAll()).thenReturn(all);

        List<ClinicResponse> result = clinicService.getClinics(null, null, null);

        assertEquals(2, result.size());
    }

    @Test
    void shouldFilterClinicsByDistrict() {
        List<Clinic> gasabo = List.of(buildClinic("Clinic A", "Gasabo", true, "Family Planning"));
        Mockito.when(clinicRepository.findByDistrictIgnoreCase("Gasabo")).thenReturn(gasabo);

        List<ClinicResponse> result = clinicService.getClinics("Gasabo", null, null);

        assertEquals(1, result.size());
        assertEquals("Gasabo", result.getFirst().getDistrict());
    }

    @Test
    void shouldFilterClinicsByYouthFriendlyFlag() {
        List<Clinic> yf = List.of(
                buildClinic("Clinic A", "Gasabo", true, "Family Planning"),
                buildClinic("Clinic B", "Nyarugenge", true, "STI Testing")
        );
        Mockito.when(clinicRepository.findByYouthFriendly(true)).thenReturn(yf);

        List<ClinicResponse> result = clinicService.getClinics(null, true, null);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(ClinicResponse::isYouthFriendly));
    }

    @Test
    void shouldFilterClinicsByService() {
        List<Clinic> withFP = List.of(buildClinic("Clinic A", "Gasabo", true, "Family Planning"));
        Mockito.when(clinicRepository.findByServiceNameContainingIgnoreCase("Family Planning")).thenReturn(withFP);

        List<ClinicResponse> result = clinicService.getClinics(null, null, "Family Planning");

        assertEquals(1, result.size());
        assertTrue(result.getFirst().getServices().contains("Family Planning"));
    }

    @Test
    void shouldApplyMultipleFiltersTogether() {
        List<Clinic> filtered = List.of(buildClinic("Clinic B", "Nyarugenge", true, "Family Planning"));
        Mockito.when(clinicRepository.findByDistrictIgnoreCaseAndYouthFriendly("Nyarugenge", true)).thenReturn(filtered);

        List<ClinicResponse> result = clinicService.getClinics("Nyarugenge", true, "Family Planning");

        assertEquals(1, result.size());
        assertEquals("Nyarugenge", result.getFirst().getDistrict());
        assertTrue(result.getFirst().isYouthFriendly());
    }

    @Test
    void shouldThrowForInvalidDistrict() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clinicService.getClinics("InvalidPlace", null, null)
        );
        assertTrue(ex.getMessage().contains("Invalid district"));
    }
}
