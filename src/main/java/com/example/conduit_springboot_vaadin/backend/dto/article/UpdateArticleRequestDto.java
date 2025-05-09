package com.example.conduit_springboot_vaadin.backend.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO that wraps the UpdateArticleDto in an 'article' field
 * to match the API spec structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArticleRequestDto {

    @Schema(description = "Article update payload")
    private UpdateArticleDto article;

}
