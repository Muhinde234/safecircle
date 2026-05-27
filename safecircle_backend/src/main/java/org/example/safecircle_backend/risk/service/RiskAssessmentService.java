package org.example.safecircle_backend.risk.service;

import lombok.extern.slf4j.Slf4j;
import org.example.safecircle_backend.risk.dto.RiskAssessmentRequest;
import org.example.safecircle_backend.risk.dto.RiskAssessmentResponse;
import org.example.safecircle_backend.risk.model.RiskAssessment;
import org.example.safecircle_backend.risk.model.RiskLevel;
import org.example.safecircle_backend.risk.repository.RiskAssessmentRepository;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.service.SessionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final SessionService sessionService;

    public RiskAssessmentService(RiskAssessmentRepository riskAssessmentRepository,
                                 SessionService sessionService) {
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.sessionService = sessionService;
    }

    public RiskAssessmentResponse assess(RiskAssessmentRequest request) {
        if (request.getHoursSinceEvent() < 0) {
            throw new IllegalArgumentException("hoursSinceEvent must be 0 or greater.");
        }

        AnonymousSession session = sessionService.getSessionById(request.getSessionId());

        RiskLevel level;
        String action;
        String urgency;

        if (request.getHoursSinceEvent() <= 72 && request.isSymptomsPresent()) {
            level = RiskLevel.HIGH;
            action = "Please visit an emergency clinic for PEP (Post-Exposure Prophylaxis).";
            urgency = "Within 24 hours";
        } else if (request.getHoursSinceEvent() <= 72) {
            level = RiskLevel.MEDIUM;
            action = "Consult a healthcare provider to discuss preventative measures.";
            urgency = "Within 48-72 hours";
        } else {
            level = RiskLevel.LOW;
            action = "Schedule a routine screening at your local clinic.";
            urgency = "Next available appointment";
        }

        RiskAssessment assessment = new RiskAssessment();
        assessment.setSession(session);
        assessment.setEventTypeLabel(request.getEventType());
        assessment.setHoursSinceEvent(request.getHoursSinceEvent());
        assessment.setSymptomsPresent(request.isSymptomsPresent());
        assessment.setRiskLevel(level);
        assessment.setRecommendedAction(action);
        assessment.setUrgencyWindow(urgency);
        riskAssessmentRepository.save(assessment);

        log.info("Risk assessment completed: {} for session {}", level, request.getSessionId());

        return RiskAssessmentResponse.builder()
                .riskLevel(level)
                .recommendedAction(action)
                .urgencyWindow(urgency)
                .build();
    }
}
