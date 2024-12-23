package com.example.conduit_springboot_vaadin.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO containing the optional fields for updating an article.
 * All fields are optional, so the user can update any or all.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArticleDto {

    @Schema(description = "Updated title of the article", example = "Did you train your dragon?")
    @Size(max = 255, message = "Title must not exceed 255 characters.")
    private String title;

    @Schema(description = "Updated short description", example = "Ever wonder how?")
    private String description;

    @Schema(description = "Updated main content of the article", example = "Now you really have to believe!")
    private String body;
}
