package org.example.safecircle_backend.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionResponse {
    private String sessionId;
    private String nickname;
    private String createdAt;
}
