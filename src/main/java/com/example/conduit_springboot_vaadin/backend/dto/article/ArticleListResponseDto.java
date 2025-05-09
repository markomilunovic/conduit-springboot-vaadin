package com.example.conduit_springboot_vaadin.backend.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResponseDto {

    @Schema(description = "List of all the articles.")
    private List<ArticleListDto> articles;

    @Schema(description = "Number of articles retrieved")
    private int articlesCount;
}
