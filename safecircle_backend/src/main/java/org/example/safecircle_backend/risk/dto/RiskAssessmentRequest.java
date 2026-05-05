package org.example.safecircle_backend.risk.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessmentRequest {
    @NotNull(message = "Please tell us what happened...")
    @NotBlank(message = "Please tell us what happened...")
    private String eventType;

    @Min(value = 0)
    private int hoursSinceEvent;

    private boolean symptomsPresent;
}
