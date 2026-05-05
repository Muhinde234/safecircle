package org.example.safecircle_backend.risk.controller;

import jakarta.validation.Valid;
import org.example.safecircle_backend.risk.dto.RiskAssessmentRequest;
import org.example.safecircle_backend.risk.dto.RiskAssessmentResponse;
import org.example.safecircle_backend.risk.service.RiskAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/risk")
public class RiskController {
    private final RiskAssessmentService riskService;

    public RiskController(RiskAssessmentService riskService) {
        this.riskService = riskService;
    }

    @PostMapping("/assess")
    public ResponseEntity<RiskAssessmentResponse> riskAssessment(@RequestBody @Valid RiskAssessmentRequest request) {
        return ResponseEntity.ok(riskService.assess(request));
    }
}
