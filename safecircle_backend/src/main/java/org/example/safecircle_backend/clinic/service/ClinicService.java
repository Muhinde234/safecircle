package org.example.safecircle_backend.clinic.service;

import org.example.safecircle_backend.clinic.dto.ClinicResponse;
import org.example.safecircle_backend.clinic.model.Clinic;
import org.example.safecircle_backend.clinic.repository.ClinicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    private static final List<String> VALID_DISTRICTS = List.of("Gasabo", "Nyarugenge", "Kicukiro");

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    private ClinicResponse toResponse(Clinic clinic) {
        List<String> services = clinic.getClinicServices().stream()
                .map(s -> s.getId().getServiceName())
                .toList();

        return ClinicResponse.builder()
                .id(clinic.getId().toString())
                .name(clinic.getName())
                .district(clinic.getDistrict())
                .address(clinic.getAddress())
                .youthFriendly(clinic.getYouthFriendly())
                .contactInfo(clinic.getContactInfo())
                .services(services)
                .whatToExpect(clinic.getWhatToExpect())
                .build();
    }

    public List<ClinicResponse> getClinics(String district, Boolean youthFriendly, String service) {
        if (district != null && !district.isBlank()) {
            boolean valid = VALID_DISTRICTS.stream().anyMatch(d -> d.equalsIgnoreCase(district.trim()));
            if (!valid) throw new IllegalArgumentException("Invalid district: " + district);
        }

        List<Clinic> clinics;

        boolean hasDistrict = district != null && !district.isBlank();
        boolean hasYouthFriendly = youthFriendly != null;
        boolean hasService = service != null && !service.isBlank();

        if (hasDistrict && hasYouthFriendly) {
            clinics = clinicRepository.findByDistrictIgnoreCaseAndYouthFriendly(district.trim(), youthFriendly);
        } else if (hasDistrict) {
            clinics = clinicRepository.findByDistrictIgnoreCase(district.trim());
        } else if (hasYouthFriendly) {
            clinics = clinicRepository.findByYouthFriendly(youthFriendly);
        } else if (hasService) {
            clinics = clinicRepository.findByServiceNameContainingIgnoreCase(service.trim());
        } else {
            clinics = clinicRepository.findAll();
        }

        // Apply service filter as a post-filter when combined with other filters
        if (hasService && (hasDistrict || hasYouthFriendly)) {
            String svc = service.trim().toLowerCase();
            clinics = clinics.stream()
                    .filter(c -> c.getClinicServices().stream()
                            .anyMatch(s -> s.getId().getServiceName().toLowerCase().contains(svc)))
                    .toList();
        }

        return clinics.stream().map(this::toResponse).toList();
    }
}
