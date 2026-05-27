package org.example.safecircle_backend.risk;

import org.example.safecircle_backend.risk.dto.RiskAssessmentRequest;
import org.example.safecircle_backend.risk.dto.RiskAssessmentResponse;
import org.example.safecircle_backend.risk.model.RiskAssessment;
import org.example.safecircle_backend.risk.model.RiskLevel;
import org.example.safecircle_backend.risk.repository.RiskAssessmentRepository;
import org.example.safecircle_backend.risk.service.RiskAssessmentService;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RiskAssessmentServiceTest {

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @Mock
    private SessionService sessionService;

    private RiskAssessmentService riskAssessmentService;

    private static final String SESSION_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        riskAssessmentService = new RiskAssessmentService(riskAssessmentRepository, sessionService);
    }

    private void stubSession() {
        AnonymousSession session = AnonymousSession.builder().nickname("tester").language("en").build();
        Mockito.when(sessionService.getSessionById(SESSION_ID)).thenReturn(session);
        Mockito.when(riskAssessmentRepository.save(Mockito.any(RiskAssessment.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void shouldReturnHighRiskWhenRecentEventAndSymptomsPresent() {
        stubSession();
        RiskAssessmentRequest request = RiskAssessmentRequest.builder()
                .sessionId(SESSION_ID).eventType("unprotected sex")
                .hoursSinceEvent(24).symptomsPresent(true).build();

        RiskAssessmentResponse response = riskAssessmentService.assess(request);

        assertNotNull(response);
        assertEquals(RiskLevel.HIGH, response.getRiskLevel());
        assertEquals("Within 24 hours", response.getUrgencyWindow());
    }

    @Test
    void shouldReturnMediumRiskWhenRecentEventWithoutSymptoms() {
        stubSession();
        RiskAssessmentRequest request = RiskAssessmentRequest.builder()
                .sessionId(SESSION_ID).eventType("unprotected sex")
                .hoursSinceEvent(48).symptomsPresent(false).build();

        RiskAssessmentResponse response = riskAssessmentService.assess(request);

        assertNotNull(response);
        assertEquals(RiskLevel.MEDIUM, response.getRiskLevel());
        assertEquals("Within 48-72 hours", response.getUrgencyWindow());
    }

    @Test
    void shouldReturnLowRiskWhenEventIsOlderThan72Hours() {
        stubSession();
        RiskAssessmentRequest request = RiskAssessmentRequest.builder()
                .sessionId(SESSION_ID).eventType("unprotected sex")
                .hoursSinceEvent(100).symptomsPresent(false).build();

        RiskAssessmentResponse response = riskAssessmentService.assess(request);

        assertNotNull(response);
        assertEquals(RiskLevel.LOW, response.getRiskLevel());
        assertEquals("Next available appointment", response.getUrgencyWindow());
    }

    @Test
    void shouldThrowWhenHoursSinceEventIsNegative() {
        RiskAssessmentRequest request = RiskAssessmentRequest.builder()
                .sessionId(SESSION_ID).eventType("unprotected sex")
                .hoursSinceEvent(-1).symptomsPresent(false).build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> riskAssessmentService.assess(request)
        );
        assertEquals("hoursSinceEvent must be 0 or greater.", ex.getMessage());
    }
}
