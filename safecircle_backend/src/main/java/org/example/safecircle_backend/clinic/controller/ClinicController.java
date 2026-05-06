package org.example.safecircle_backend.clinic.controller;

import org.example.safecircle_backend.clinic.dto.ClinicResponse;
import org.example.safecircle_backend.clinic.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clinics")
public class ClinicController {
    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping
    public ResponseEntity<List<ClinicResponse>> getClinics(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Boolean youthFriendly,
            @RequestParam(required = false) String service
    ){
        return ResponseEntity.ok(clinicService.getClinics(district, youthFriendly, service));
    }
}
