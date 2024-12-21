package com.example.conduit_springboot_vaadin.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResponseDto {

    @Schema
    private List<ArticleListDto> articles;

    @Schema
    private int articlesCount;
}
