package org.example.safecircle_backend.content.service;

import org.example.safecircle_backend.content.dto.ContentFeedResponse;
import org.example.safecircle_backend.content.dto.ContentItemResponse;
import org.example.safecircle_backend.content.model.ContentCategory;
import org.example.safecircle_backend.content.model.ContentItem;
import org.example.safecircle_backend.content.repository.ContentItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {

    private final ContentItemRepository contentItemRepository;

    public ContentService(ContentItemRepository contentItemRepository) {
        this.contentItemRepository = contentItemRepository;
    }

    private ContentItemResponse toResponse(ContentItem item) {
        return ContentItemResponse.builder()
                .id(item.getId().toString())
                .title(item.getTitle())
                .description(item.getSummary())
                .contentType(item.getContentType())
                .category(ContentCategory.valueOf(item.getCategory().toUpperCase()))
                .language(item.getLanguage())
                .createdAt(item.getCreatedAt().toString())
                .audioUrl(item.getAudioUrl())
                .build();
    }

    public ContentFeedResponse getContent(String category, Integer limit) {
        List<ContentItem> items;

        if (category != null && !category.isBlank()) {
            try {
                ContentCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid category: " + category);
            }
            items = contentItemRepository.findByPublishedTrueAndCategoryIgnoreCaseOrderByCreatedAtDesc(category);
        } else {
            items = contentItemRepository.findByPublishedTrueOrderByCreatedAtDesc();
        }

        int finalLimit = (limit == null || limit <= 0) ? 10 : Math.min(limit, 50);

        List<ContentItemResponse> result = items.stream()
                .limit(finalLimit)
                .map(this::toResponse)
                .toList();

        return ContentFeedResponse.builder()
                .items(result)
                .total(result.size())
                .category(category)
                .build();
    }

    public ContentFeedResponse getLowBandwidthContent(String category, Integer limit) {
        ContentFeedResponse normalFeed = getContent(category, limit);
        List<ContentItemResponse> lightweight = normalFeed.getItems().stream()
                .map(item -> ContentItemResponse.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .contentType(item.getContentType())
                        .category(item.getCategory())
                        .language(item.getLanguage())
                        .createdAt(item.getCreatedAt())
                        .audioUrl(item.getAudioUrl())
                        .build()) // summary/description is omitted
                .toList();

        return ContentFeedResponse.builder()
                .items(lightweight)
                .total(lightweight.size())
                .category(category)
                .build();
    }
}
