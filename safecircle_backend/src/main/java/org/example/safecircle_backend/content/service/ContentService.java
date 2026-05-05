package org.example.safecircle_backend.content.service;

import org.example.safecircle_backend.content.dto.ContentFeedResponse;
import org.example.safecircle_backend.content.dto.ContentItemResponse;
import org.example.safecircle_backend.content.model.ContentCategory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ContentService {
    private List<ContentItemResponse> seedContent() {

        return new ArrayList<>(
                Arrays.asList(
                        ContentItemResponse.builder()
                                .id("a")
                                .title("a")
                                .description("a")
                                .contentType("PG")
                                .category(ContentCategory.GENERAL)
                                .createdAt("2026-04-30T12:49:42.615646900Z")
                                .build(),

                        ContentItemResponse.builder()
                                .id("b")
                                .title("b")
                                .description("b")
                                .contentType("PG")
                                .category(ContentCategory.HIV)
                                .createdAt("2026-03-30T12:49:42")
                                .build(),

                        ContentItemResponse.builder()
                                .id("c")
                                .title("C")
                                .contentType("PG")
                                .description("C")
                                .category(ContentCategory.PREVENTION)
                                .createdAt("2026-04-03T10:49:42")
                                .build(),

                        ContentItemResponse.builder()
                                .id("aa")
                                .title("aa")
                                .description("aa")
                                .contentType("PG")
                                .category(ContentCategory.GENERAL)
                                .createdAt("2026-04-30T12:49:42.615646900Z")
                                .build(),

                        ContentItemResponse.builder()
                                .id("bb")
                                .title("bb")
                                .description("bb")
                                .contentType("PG")
                                .category(ContentCategory.HIV)
                                .createdAt("2026-03-30T12:49:42")
                                .build(),

                        ContentItemResponse.builder()
                                .id("cc")
                                .title("cC")
                                .contentType("PG")
                                .description("Cc")
                                .category(ContentCategory.PREVENTION)
                                .createdAt("2026-04-03T10:49:42")
                                .build()
                )
        );
    }

    private ContentFeedResponse getContentFeed(List<ContentItemResponse> itemList, String category) {
        return ContentFeedResponse.builder()
                .items(itemList)
                .category(category)
                .total(itemList.size())
                .build();
    }

    public ContentFeedResponse getContent(String category, Integer limit) {

        List<ContentItemResponse> contentList = seedContent();

        // Parse category safely and filter if exists
        try{
            if (category != null && !category.isBlank()) {

                ContentCategory contentCategory = ContentCategory.valueOf(category.toUpperCase());

                contentList = contentList.stream()
                        .filter(item -> item.getCategory() == contentCategory)
                        .toList();
            }

        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid category: " + category);
        }


        // Apply limit fallbacks and caps
        int finalLimit = 10;
        if (limit != null && !(limit <= 0)) {
            finalLimit = limit;

            if (finalLimit > 50) {
                finalLimit = 50;
            }
        }

        // Apply limit to list
        List<ContentItemResponse> finalContentList = contentList.stream()
                .limit(finalLimit)
                .toList();

        return getContentFeed(finalContentList, category);

    }
}
