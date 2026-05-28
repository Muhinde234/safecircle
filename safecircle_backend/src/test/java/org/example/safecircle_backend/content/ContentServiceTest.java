package org.example.safecircle_backend.content;

import org.example.safecircle_backend.content.dto.ContentFeedResponse;
import org.example.safecircle_backend.content.model.ContentItem;
import org.example.safecircle_backend.content.repository.ContentItemRepository;
import org.example.safecircle_backend.content.service.ContentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentItemRepository contentItemRepository;

    private ContentService contentService;

    @BeforeEach
    void setUp() {
        contentService = new ContentService(contentItemRepository);
    }

    private ContentItem buildItem(String title, String category) {
        ContentItem item = new ContentItem();
        try {
            var id = ContentItem.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(item, UUID.randomUUID());
            var createdAt = ContentItem.class.getDeclaredField("createdAt");
            createdAt.setAccessible(true);
            createdAt.set(item, OffsetDateTime.now());
        } catch (Exception ignored) {}
        item.setTitle(title);
        item.setSummary("summary");
        item.setContentType("TEXT");
        item.setCategory(category);
        item.setLanguage("en");
        item.setPublished(true);
        return item;
    }

    @Test
    void shouldReturnContentWhenNoFilters() {
        List<ContentItem> items = List.of(buildItem("PrEP Guide", "HIV"), buildItem("STI Myths", "STI"));
        Mockito.when(contentItemRepository.findByPublishedTrueOrderByCreatedAtDesc()).thenReturn(items);

        ContentFeedResponse response = contentService.getContent(null, null);

        assertNotNull(response);
        assertEquals(2, response.getItems().size());
        assertEquals(2, response.getTotal());
    }

    @Test
    void shouldFilterByCategory() {
        List<ContentItem> items = List.of(buildItem("PrEP Guide", "HIV"));
        Mockito.when(contentItemRepository.findByPublishedTrueAndCategoryIgnoreCaseOrderByCreatedAtDesc("HIV"))
                .thenReturn(items);

        ContentFeedResponse response = contentService.getContent("HIV", null);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals("HIV", response.getItems().getFirst().getCategory().name());
    }

    @Test
    void shouldApplyLimit() {
        List<ContentItem> items = List.of(
                buildItem("A", "HIV"), buildItem("B", "STI"), buildItem("C", "PREVENTION")
        );
        Mockito.when(contentItemRepository.findByPublishedTrueOrderByCreatedAtDesc()).thenReturn(items);

        ContentFeedResponse response = contentService.getContent(null, 2);

        assertEquals(2, response.getItems().size());
        assertEquals(2, response.getTotal());
    }

    @Test
    void shouldCapLimitToFifty() {
        Mockito.when(contentItemRepository.findByPublishedTrueOrderByCreatedAtDesc()).thenReturn(List.of());

        ContentFeedResponse response = contentService.getContent(null, 100);

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

    @Test
    void shouldReturnLowBandwidthContentWithoutSummary() {
        List<ContentItem> items = List.of(buildItem("PrEP Guide", "HIV"));
        Mockito.when(contentItemRepository.findByPublishedTrueOrderByCreatedAtDesc()).thenReturn(items);

        ContentFeedResponse response = contentService.getLowBandwidthContent(null, null);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertNull(response.getItems().getFirst().getDescription()); // summary should be null
    }
}
