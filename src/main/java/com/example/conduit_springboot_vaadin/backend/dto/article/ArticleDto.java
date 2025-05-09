package com.example.conduit_springboot_vaadin.backend.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO for article data in responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {

    @Schema(description = "Slug of the article", example = "how-to-train-your-dragon")
    private String slug;

    @Schema(description = "Title of the article", example = "How to train your dragon")
    private String title;

    @Schema(description = "Short description of the article", example = "Ever wonder how?")
    private String description;

    @Schema(description = "Main content of the article", example = "You have to believe")
    private String body;

    @Schema(description = "List of tags associated with the article", example = "[\"reactjs\", \"angularjs\", \"dragons\"]")
    private List<String> tagList;

    @Schema(description = "Creation timestamp", example = "2024-12-20T15:30:00Z")
    private Instant createdAt;

    @Schema(description = "Last updated timestamp", example = "2024-12-20T15:30:00Z")
    private Instant updatedAt;

    @Schema(description = "Whether the current user has favorited the article", example = "false")
    private boolean favorited;

    @Schema(description = "Number of times the article has been favorited", example = "0")
    private int favoritesCount;

    @Schema(description = "Author's detailed information")
    private AuthorDto author;

}
