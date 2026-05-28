package org.example.safecircle_backend.clinic;

import org.example.safecircle_backend.clinic.controller.ClinicController;
import org.example.safecircle_backend.clinic.dto.ClinicResponse;
import org.example.safecircle_backend.clinic.service.ClinicService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClinicController.class)
class ClinicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClinicService clinicService;

    @Test
    void shouldReturnClinicsWithoutFilters() throws Exception {
        List<ClinicResponse> clinics = List.of(
                ClinicResponse.builder()
                        .id("c1")
                        .name("Kigali Youth Center Clinic")
                        .district("Gasabo")
                        .address("KG 345 ST 6")
                        .youthFriendly(true)
                        .contactInfo("+250780000000")
                        .services(List.of("Family Planning"))
                        .whatToExpect("Confidential support")
                        .build()
        );

        Mockito.when(clinicService.getClinics(null, null, null)).thenReturn(clinics);

        mockMvc.perform(get("/api/v1/clinics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Kigali Youth Center Clinic"))
                .andExpect(jsonPath("$[0].district").value("Gasabo"));
    }

    @Test
    void shouldReturnClinicsWithFilters() throws Exception {
        List<ClinicResponse> clinics = List.of(
                ClinicResponse.builder()
                        .id("c2")
                        .name("Nyamirambo Wellness Center")
                        .district("Nyarugenge")
                        .address("KN 345 ST 6")
                        .youthFriendly(true)
                        .contactInfo("+250780000001")
                        .services(List.of("Family Planning"))
                        .whatToExpect("Confidential consultation")
                        .build()
        );

        Mockito.when(clinicService.getClinics("Nyarugenge", true, "Family Planning")).thenReturn(clinics);

        mockMvc.perform(get("/api/v1/clinics")
                        .param("district", "Nyarugenge")
                        .param("youthFriendly", "true")
                        .param("service", "Family Planning")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].district").value("Nyarugenge"))
                .andExpect(jsonPath("$[0].youthFriendly").value(true))
                .andExpect(jsonPath("$[0].services[0]").value("Family Planning"));
    }
}
