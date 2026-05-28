package org.example.safecircle_backend.risk.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireDto {

    private String startQuestionId;
    private Map<String, QuestionNodeDto> questions;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionNodeDto {
        private String id;
        private String text;
        private List<OptionDto> options;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDto {
        private String text;
        private String nextQuestionId;
        private RiskAssessmentShortcut riskAssessmentShortcut;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessmentShortcut {
        private String eventType;
        private int hoursSinceEvent;
        private boolean symptomsPresent;
    }
}
