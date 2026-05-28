package org.example.safecircle_backend.risk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.safecircle_backend.risk.dto.QuestionnaireDto;
import org.example.safecircle_backend.risk.dto.RiskAssessmentRequest;
import org.example.safecircle_backend.risk.dto.RiskAssessmentResponse;
import org.example.safecircle_backend.risk.service.RiskAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Risk Assessment", description = "Perform risk assessments and retrieve branching questionnaire decision trees")
@RestController
@RequestMapping("/api/v1/risk")
public class RiskController {
    private final RiskAssessmentService riskService;

    public RiskController(RiskAssessmentService riskService) {
        this.riskService = riskService;
    }

    @Operation(summary = "Perform a risk assessment", description = "Evaluates the risk level based on user input, hours since the event, and present symptoms.")
    @PostMapping("/assess")
    public ResponseEntity<RiskAssessmentResponse> riskAssessment(@RequestBody @Valid RiskAssessmentRequest request) {
        return ResponseEntity.ok(riskService.assess(request));
    }

    @Operation(summary = "Get the dynamic questionnaire decision tree", description = "Fetches the full branching questionnaire configuration for local client flow.")
    @GetMapping("/questionnaire")
    public ResponseEntity<QuestionnaireDto> getQuestionnaire() {
        return ResponseEntity.ok(riskService.getQuestionnaire());
    }
}
