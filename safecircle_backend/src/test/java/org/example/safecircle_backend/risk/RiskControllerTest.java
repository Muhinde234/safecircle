package org.example.safecircle_backend.risk;

import org.example.safecircle_backend.risk.controller.RiskController;
import org.example.safecircle_backend.risk.dto.RiskAssessmentRequest;
import org.example.safecircle_backend.risk.dto.RiskAssessmentResponse;
import org.example.safecircle_backend.risk.model.RiskLevel;
import org.example.safecircle_backend.risk.service.RiskAssessmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RiskController.class)
class RiskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiskAssessmentService riskAssessmentService;

    @Test
    void shouldReturnRiskAssessmentForValidRequest() throws Exception {
        RiskAssessmentResponse response = RiskAssessmentResponse.builder()
                .riskLevel(RiskLevel.MEDIUM)
                .recommendedAction("Consult a healthcare provider to discuss preventative measures.")
                .urgencyWindow("Within 48-72 hours")
                .build();

        Mockito.when(riskAssessmentService.assess(Mockito.any(RiskAssessmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/risk/assess")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventType\":\"unprotected sex\",\"hoursSinceEvent\":36,\"symptomsPresent\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("MEDIUM"))
                .andExpect(jsonPath("$.urgencyWindow").value("Within 48-72 hours"));
    }
}
