package com.example.conduit_springboot_vaadin.backend.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper DTO for article responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponseDto {

    @Schema(description = "Article data in response")
    private ArticleDto article;

}
