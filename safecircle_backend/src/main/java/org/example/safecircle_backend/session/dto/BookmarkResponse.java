package org.example.safecircle_backend.session.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    private String bookmarkType;
    private String targetId;
    private String createdAt;
}
