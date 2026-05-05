package org.example.safecircle_backend.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.safecircle_backend.risk.model.RiskLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentResponse {
    private RiskLevel riskLevel;      // LOW, MEDIUM, HIGH
    private String recommendedAction; // What to do next
    private String urgencyWindow;    // e.g., "Within 24 hours"
}
