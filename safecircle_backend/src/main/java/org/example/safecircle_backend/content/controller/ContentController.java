package org.example.safecircle_backend.content.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.safecircle_backend.content.dto.ContentFeedResponse;
import org.example.safecircle_backend.content.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Content Delivery", description = "Retrieve SRH educational articles, low-bandwidth configurations, and text-to-speech audio URLs")
@RestController
@RequestMapping("/api/v1/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @Operation(summary = "Get educational content items", description = "Retrieves a feed of SRH educational articles, optionally filtered by category and limited to a specific count.")
    @GetMapping()
    public ResponseEntity<ContentFeedResponse> getContent(@RequestParam(required = false) String category,
                                                                 @RequestParam(required = false) Integer limit) {
        return new ResponseEntity<>(contentService.getContent(category, limit), HttpStatus.OK);
    }

    @Operation(summary = "Get low-bandwidth educational content metadata", description = "Retrieves a feed of content metadata (omitting full bodies) to conserve mobile data costs, including text-to-speech voiceover audio URLs.")
    @GetMapping("/low-bandwidth")
    public ResponseEntity<ContentFeedResponse> getLowBandwidthContent(@RequestParam(required = false) String category,
                                                                             @RequestParam(required = false) Integer limit) {
        return new ResponseEntity<>(contentService.getLowBandwidthContent(category, limit), HttpStatus.OK);
    }
}
