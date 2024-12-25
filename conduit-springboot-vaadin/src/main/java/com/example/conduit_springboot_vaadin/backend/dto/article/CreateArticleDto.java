package com.example.conduit_springboot_vaadin.backend.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating a new article.
 * This DTO encapsulates the necessary information required for a creating an article.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateArticleDto {

    @Schema(description = "Title of the article", example = "How to train your dragon")
    @NotBlank(message = "Title is required.")
    @Size(max = 255, message = "Title must not exceed 255 characters.")
    private String title;

    @Schema(description = "Short description of the article", example = "Ever wonder how?")
    @NotBlank(message = "Description is required.")
    private String description;

    @Schema(description = "Main content of the article", example = "You have to believe")
    @NotBlank(message = "Body is required.")
    private String body;

    @Schema(description = "List of tags associated with the article", example = "[\"reactjs\", \"angularjs\", \"dragons\"]")
    private List<@Size(max = 50, message = "Tag must not exceed 50 characters.") String> tagList;

}
