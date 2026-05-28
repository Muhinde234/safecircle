package org.example.safecircle_backend.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicResponse {
    private String  id;
    private String name;
    private String district;
    private String address;
    private boolean youthFriendly;
    private String contactInfo;
    private List<String> services;
    private String whatToExpect;

}
