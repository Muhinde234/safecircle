package org.example.safecircle_backend.content;

import org.example.safecircle_backend.content.dto.ContentFeedResponse;
import org.example.safecircle_backend.content.service.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContentServiceTest {

    private ContentService contentService;

    @BeforeEach
    void setUp() {
        contentService = new ContentService();
    }

    @Test
    void shouldReturnSeededContentWhenNoFilters() {
        ContentFeedResponse response = contentService.getContent(null, null);

        assertNotNull(response);
        assertNotNull(response.getItems());
        assertFalse(response.getItems().isEmpty());
        assertEquals(response.getItems().size(), response.getTotal());
    }

    @Test
    void shouldFilterByCategory() {
        ContentFeedResponse response = contentService.getContent("HIV", null);

        assertNotNull(response);
        assertFalse(response.getItems().isEmpty());
        assertTrue(response.getItems().stream().allMatch(item -> item.getCategory().name().equals("HIV")));
    }

    @Test
    void shouldApplyLimit() {
        ContentFeedResponse response = contentService.getContent(null, 2);

        assertNotNull(response);
        assertEquals(2, response.getItems().size());
        assertEquals(2, response.getTotal());
    }

    @Test
    void shouldCapLimitToFifty() {
        ContentFeedResponse response = contentService.getContent(null, 100);

        assertNotNull(response);
        assertTrue(response.getItems().size() <= 50);
    }

    @Test
    void shouldThrowForInvalidCategory() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> contentService.getContent("NOT_A_CATEGORY", null)
        );

        assertTrue(ex.getMessage().contains("Invalid category"));
    }
}
