package org.example.safecircle_backend.risk;

import org.example.safecircle_backend.risk.dto.RiskAssessmentRequest;
import org.example.safecircle_backend.risk.dto.RiskAssessmentResponse;
import org.example.safecircle_backend.risk.model.RiskLevel;
import org.example.safecircle_backend.risk.service.RiskAssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RiskAssessmentServiceTest {

    private RiskAssessmentService riskAssessmentService;

    @BeforeEach
    void setUp() {
        riskAssessmentService = new RiskAssessmentService();
    }

    @Test
    void shouldReturnHighRiskWhenRecentEventAndSymptomsPresent() {
        RiskAssessmentRequest request = RiskAssessmentRequest.builder()
                .eventType("unprotected sex")
                .hoursSinceEvent(24)
                .symptomsPresent(true)
                .build();

        RiskAssessmentResponse response = riskAssessmentService.assess(request);

        assertNotNull(response);
        assertEquals(RiskLevel.HIGH, response.getRiskLevel());
        assertEquals("Within 24 hours", response.getUrgencyWindow());
    }

    @Test
    void shouldReturnMediumRiskWhenRecentEventWithoutSymptoms() {
        RiskAssessmentRequest request = RiskAssessmentRequest.builder()
                .eventType("unprotected sex")
                .hoursSinceEvent(48)
                .symptomsPresent(false)
                .build();

        RiskAssessmentResponse response = riskAssessmentService.assess(request);

        assertNotNull(response);
        assertEquals(RiskLevel.MEDIUM, response.getRiskLevel());
        assertEquals("Within 48-72 hours", response.getUrgencyWindow());
    }

    @Test
    void shouldReturnLowRiskWhenEventIsOlderThan72Hours() {
        RiskAssessmentRequest request = RiskAssessmentRequest.builder()
                .eventType("unprotected sex")
                .hoursSinceEvent(100)
                .symptomsPresent(false)
                .build();

        RiskAssessmentResponse response = riskAssessmentService.assess(request);

        assertNotNull(response);
        assertEquals(RiskLevel.LOW, response.getRiskLevel());
        assertEquals("Next available appointment", response.getUrgencyWindow());
    }
}
