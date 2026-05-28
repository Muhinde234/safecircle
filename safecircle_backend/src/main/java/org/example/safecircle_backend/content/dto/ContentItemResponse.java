package org.example.safecircle_backend.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.safecircle_backend.content.model.ContentCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentItemResponse {
    private String id;
    private String title;
    private String description;
    private String contentType;
    private ContentCategory category;
    private String language;
    private String createdAt;
    private String audioUrl;
}
