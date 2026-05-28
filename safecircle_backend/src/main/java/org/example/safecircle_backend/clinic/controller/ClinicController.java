package org.example.safecircle_backend.clinic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.safecircle_backend.clinic.dto.ClinicResponse;
import org.example.safecircle_backend.clinic.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Clinic Locator", description = "Query and locate non-judgmental and youth-friendly SRH clinics in Rwanda")
@RestController
@RequestMapping("/api/v1/clinics")
public class ClinicController {
    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Operation(summary = "Search for clinics", description = "Retrieves a list of clinics based on filter criteria like district, youth-friendly status, and offered services.")
    @GetMapping
    public ResponseEntity<List<ClinicResponse>> getClinics(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Boolean youthFriendly,
            @RequestParam(required = false) String service
    ){
        return ResponseEntity.ok(clinicService.getClinics(district, youthFriendly, service));
    }
}
