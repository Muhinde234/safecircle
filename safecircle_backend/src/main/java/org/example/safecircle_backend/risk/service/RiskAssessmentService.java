package org.example.safecircle_backend.risk.service;

import lombok.extern.slf4j.Slf4j;
import org.example.safecircle_backend.risk.dto.QuestionnaireDto;
import org.example.safecircle_backend.risk.dto.RiskAssessmentRequest;
import org.example.safecircle_backend.risk.dto.RiskAssessmentResponse;
import org.example.safecircle_backend.risk.model.RiskAssessment;
import org.example.safecircle_backend.risk.model.RiskLevel;
import org.example.safecircle_backend.risk.repository.RiskAssessmentRepository;
import org.example.safecircle_backend.session.model.AnonymousSession;
import org.example.safecircle_backend.session.service.SessionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public QuestionnaireDto getQuestionnaire() {
        var eventNode = QuestionnaireDto.QuestionNodeDto.builder()
                .id("q_event")
                .text("What event occurred?")
                .options(List.of(
                        QuestionnaireDto.OptionDto.builder()
                                .text("Unprotected intercourse (vaginal/anal)")
                                .nextQuestionId("q_time_72")
                                .build(),
                        QuestionnaireDto.OptionDto.builder()
                                .text("Condom broke/slipped during intercourse")
                                .nextQuestionId("q_time_72")
                                .build(),
                        QuestionnaireDto.OptionDto.builder()
                                .text("Shared needles or injection equipment")
                                .nextQuestionId("q_time_72")
                                .build(),
                        QuestionnaireDto.OptionDto.builder()
                                .text("Oral sex without barrier")
                                .nextQuestionId("q_oral_symptoms")
                                .build()
                ))
                .build();

        var timeNode = QuestionnaireDto.QuestionNodeDto.builder()
                .id("q_time_72")
                .text("Did this event happen within the last 72 hours (3 days)?")
                .options(List.of(
                        QuestionnaireDto.OptionDto.builder()
                                .text("Yes, within the last 72 hours")
                                .nextQuestionId("q_pep_symptoms")
                                .build(),
                        QuestionnaireDto.OptionDto.builder()
                                .text("No, more than 72 hours ago")
                                .nextQuestionId("q_long_term_symptoms")
                                .build()
                ))
                .build();

        var pepSymptomsNode = QuestionnaireDto.QuestionNodeDto.builder()
                .id("q_pep_symptoms")
                .text("Are you experiencing any physical symptoms (e.g. pain, burning, sores, unusual discharge)?")
                .options(List.of(
                        QuestionnaireDto.OptionDto.builder()
                                .text("Yes, symptoms are present")
                                .riskAssessmentShortcut(QuestionnaireDto.RiskAssessmentShortcut.builder()
                                        .eventType("unprotected sex")
                                        .hoursSinceEvent(24)
                                        .symptomsPresent(true)
                                        .build())
                                .build(),
                        QuestionnaireDto.OptionDto.builder()
                                .text("No, no symptoms")
                                .riskAssessmentShortcut(QuestionnaireDto.RiskAssessmentShortcut.builder()
                                        .eventType("unprotected sex")
                                        .hoursSinceEvent(24)
                                        .symptomsPresent(false)
                                        .build())
                                .build()
                ))
                .build();

        var longTermSymptomsNode = QuestionnaireDto.QuestionNodeDto.builder()
                .id("q_long_term_symptoms")
                .text("Are you experiencing any physical symptoms?")
                .options(List.of(
                        QuestionnaireDto.OptionDto.builder()
                                .text("Yes, symptoms are present")
                                .riskAssessmentShortcut(QuestionnaireDto.RiskAssessmentShortcut.builder()
                                        .eventType("unprotected sex")
                                        .hoursSinceEvent(96)
                                        .symptomsPresent(true)
                                        .build())
                                .build(),
                        QuestionnaireDto.OptionDto.builder()
                                .text("No, no symptoms")
                                .riskAssessmentShortcut(QuestionnaireDto.RiskAssessmentShortcut.builder()
                                        .eventType("unprotected sex")
                                        .hoursSinceEvent(96)
                                        .symptomsPresent(false)
                                        .build())
                                .build()
                ))
                .build();

        var oralSymptomsNode = QuestionnaireDto.QuestionNodeDto.builder()
                .id("q_oral_symptoms")
                .text("Are you experiencing physical symptoms?")
                .options(List.of(
                        QuestionnaireDto.OptionDto.builder()
                                .text("Yes, symptoms are present")
                                .riskAssessmentShortcut(QuestionnaireDto.RiskAssessmentShortcut.builder()
                                        .eventType("oral sex")
                                        .hoursSinceEvent(48)
                                        .symptomsPresent(true)
                                        .build())
                                .build(),
                        QuestionnaireDto.OptionDto.builder()
                                .text("No, no symptoms")
                                .riskAssessmentShortcut(QuestionnaireDto.RiskAssessmentShortcut.builder()
                                        .eventType("oral sex")
                                        .hoursSinceEvent(48)
                                        .symptomsPresent(false)
                                        .build())
                                .build()
                ))
                .build();

        return QuestionnaireDto.builder()
                .startQuestionId("q_event")
                .questions(Map.of(
                        "q_event", eventNode,
                        "q_time_72", timeNode,
                        "q_pep_symptoms", pepSymptomsNode,
                        "q_long_term_symptoms", longTermSymptomsNode,
                        "q_oral_symptoms", oralSymptomsNode
                ))
                .build();
    }
}
