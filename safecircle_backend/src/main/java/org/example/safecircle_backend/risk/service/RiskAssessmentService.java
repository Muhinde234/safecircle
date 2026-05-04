package org.example.safecircle_backend.risk.service;

import org.example.safecircle_backend.risk.dto.RiskAssessmentRequest;
import org.example.safecircle_backend.risk.dto.RiskAssessmentResponse;
import org.example.safecircle_backend.risk.model.RiskLevel;
import org.springframework.stereotype.Service;

@Service
public class RiskAssessmentService {
    public RiskAssessmentResponse assess (RiskAssessmentRequest request){
        RiskLevel level;
        String action;
        String urgency;

        if(request.getHoursSinceEvent() <= 72 && request.isSymptomsPresent()){
            level = RiskLevel.HIGH;
            action = "Please visit an emergency clinic for PEP (Post-Exposure Prophylaxis).";
            urgency = "Within 24 hours";        
        
        } else if (request.getHoursSinceEvent() <= 72) {
            level = RiskLevel.MEDIUM;
            action = "Consult a healthcare provider to discuss preventative measures.";
            urgency = "Within 48-72 hours";

        } else {         // Rule 3: Low Risk (Old event or very long ago)
            level = RiskLevel.LOW;
            action = "Schedule a routine screening at your local clinic.";
            urgency = "Next available appointment";

        }

        return RiskAssessmentResponse.builder()
                .riskLevel(level)
                .recommendedAction(action)
                .urgencyWindow(urgency)
                .build();
    }
}
