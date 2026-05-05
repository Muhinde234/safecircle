package org.example.safecircle_backend.content;

import org.example.safecircle_backend.content.controller.ContentController;
import org.example.safecircle_backend.content.dto.ContentFeedResponse;
import org.example.safecircle_backend.content.dto.ContentItemResponse;
import org.example.safecircle_backend.content.model.ContentCategory;
import org.example.safecircle_backend.content.service.ContentService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentControllerTest {

    @Test
    void shouldReturnContentFeed() {
        ContentService contentService = mock(ContentService.class);
        ContentController contentController = new ContentController(contentService);

        ContentFeedResponse response = ContentFeedResponse.builder()
                .items(List.of(ContentItemResponse.builder()
                        .id("1")
                        .title("HIV basics")
                        .description("Know your status")
                        .contentType("TEXT")
                        .category(ContentCategory.HIV)
                        .createdAt("2026-05-04T10:00:00Z")
                        .build()))
                .total(1)
                .category(null)
                .build();

        when(contentService.getContent(null, null)).thenReturn(response);

        ResponseEntity<ContentFeedResponse> result = contentController.getContent(null, null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getTotal());
        assertEquals("HIV basics", result.getBody().getItems().get(0).getTitle());
    }

    @Test
    void shouldReturnFilteredCategoryContent() {
        ContentService contentService = mock(ContentService.class);
        ContentController contentController = new ContentController(contentService);

        ContentFeedResponse response = ContentFeedResponse.builder()
                .items(List.of(ContentItemResponse.builder()
                        .id("2")
                        .title("HIV test")
                        .description("Where to test")
                        .contentType("TEXT")
                        .category(ContentCategory.HIV)
                        .createdAt("2026-05-04T10:00:00Z")
                        .build()))
                .total(1)
                .category("HIV")
                .build();

        when(contentService.getContent("HIV", null)).thenReturn(response);

        ResponseEntity<ContentFeedResponse> result = contentController.getContent("HIV", null);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("HIV", result.getBody().getCategory());
        assertEquals("HIV", result.getBody().getItems().get(0).getCategory().name());
    }

    @Test
    void shouldApplyLimit() {
        ContentService contentService = mock(ContentService.class);
        ContentController contentController = new ContentController(contentService);

        ContentFeedResponse response = ContentFeedResponse.builder()
                .items(List.of(
                        ContentItemResponse.builder().id("1").title("A").build(),
                        ContentItemResponse.builder().id("2").title("B").build()
                ))
                .total(2)
                .build();

        when(contentService.getContent(null, 2)).thenReturn(response);

        ResponseEntity<ContentFeedResponse> result = contentController.getContent(null, 2);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().getTotal());
        assertEquals(2, result.getBody().getItems().size());
    }

    @Test
    void shouldThrowForInvalidCategoryFromService() {
        ContentService contentService = mock(ContentService.class);
        ContentController contentController = new ContentController(contentService);

        when(contentService.getContent("INVALID", null))
                .thenThrow(new IllegalArgumentException("Invalid category: INVALID"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> contentController.getContent("INVALID", null)
        );

        assertEquals("Invalid category: INVALID", ex.getMessage());
    }
}
