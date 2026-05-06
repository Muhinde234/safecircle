package org.example.safecircle_backend.clinic.service;

import org.example.safecircle_backend.clinic.dto.ClinicResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ClinicService {

    private final List<ClinicResponse> mockClinics = Arrays.asList(
            new ClinicResponse(
                    "41bcd968-aeea-4e66-9b4c-d26a3ab6c520",
                    "Kigali Youth Center Clinic",
                    "Gasabo",
                    "KG 345 ST 6",
                    true,
                    "+250780000000",
                    Arrays.asList("Internal Medicine", "Family Planning"),
                    "Free counseling and STI testing in a private, non-judgmental environment."
            ),
            new ClinicResponse("" +
                    "41bcd968-aeea-4e66-9b4c-d26a3ab6c522",
                    "Nyamirambo Wellness Center",
                    "Nyarugenge",
                    "KN 345 ST 6",
                    true,
                    "+250780000001",
                    Arrays.asList("Dentistry", "Family Planning"),
                    "Confidential consultation and contraception access."
            ),
            new ClinicResponse(
                    "41bcd968-aeea-4e66-9b4c-d26a3ab6c524",
                    "Kicukiro Health Facility",
                    "Kicukiro",
                    "KK 345 ST 6",
                    false,
                    "+250780000002",
                    Arrays.asList("Ophthalmology", "Family Planning"),
                    "General STI testing and reproductive health information."
            )
    );

    private List<String> VALID_DISTRICTS = Arrays.asList("Gasabo", "Nyarugenge", "Kicukiro");


    public List<ClinicResponse> getClinics(String district, Boolean youthFriendly, String service) {
        if(district !=null && !district.isBlank()) {
            boolean isValid = VALID_DISTRICTS.stream()
                    .anyMatch(d -> d.equalsIgnoreCase(district.trim()));
            System.out.println("District "+district+" is valid? "+isValid);
            if(!isValid) {
                throw new IllegalArgumentException("Invalid district: " + district);
            }
        }

        return mockClinics.stream()
                .filter(c -> district == null || district.isBlank()  || c.getDistrict().equalsIgnoreCase(district.trim()))
                .filter(c -> youthFriendly == null || c.isYouthFriendly() == youthFriendly)
                .filter(c -> service == null || service.isBlank() || c.getServices().stream()
                        .anyMatch(s -> s.toLowerCase().contains(service.trim().toLowerCase())))
                .toList();
    }
}
