package com.example.conduit_springboot_vaadin.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper DTO for creating a new article.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateArticleRequestDto {

    @Schema(description = "Article data for creation")
    private CreateArticleDto article;

}
