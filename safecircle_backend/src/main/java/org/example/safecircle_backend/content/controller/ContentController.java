package org.example.safecircle_backend.content.controller;

import org.example.safecircle_backend.content.dto.ContentFeedResponse;
import org.example.safecircle_backend.content.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping()
    public ResponseEntity<ContentFeedResponse> getContent(@RequestParam(required = false) String category,
                                                                @RequestParam(required = false) Integer limit) {
        return new ResponseEntity<>(contentService.getContent(category, limit), HttpStatus.OK);
    }
}
