package org.example.safecircle_backend.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentFeedResponse {
    private List<ContentItemResponse> items;
    private int total;
    private String category;
}
