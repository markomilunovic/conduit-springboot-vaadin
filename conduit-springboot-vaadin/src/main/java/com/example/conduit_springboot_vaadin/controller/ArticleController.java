package com.example.conduit_springboot_vaadin.controller;

import com.example.conduit_springboot_vaadin.common.util.ErrorResponse;
import com.example.conduit_springboot_vaadin.common.util.ValidationErrorResponse;
import com.example.conduit_springboot_vaadin.dto.article.ArticleDto;
import com.example.conduit_springboot_vaadin.dto.article.ArticleResponseDto;
import com.example.conduit_springboot_vaadin.dto.article.CreateArticleDto;
import com.example.conduit_springboot_vaadin.dto.article.CreateArticleRequestDto;
import com.example.conduit_springboot_vaadin.dto.user.ResponseDto;
import com.example.conduit_springboot_vaadin.security.CustomUserDetails;
import com.example.conduit_springboot_vaadin.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(
            summary = "Create a new article",
            description = "Creates a new article with the provided details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Article created successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed due to invalid input parameters",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<ResponseDto<ArticleResponseDto>> createArticle(
            @Valid @RequestBody CreateArticleRequestDto createArticleRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        CreateArticleDto createArticleDto = createArticleRequestDto.getArticle();

        ArticleDto articleDto = articleService.createArticle(
                createArticleDto,
                userDetails.getUsername(),
                userDetails.getId()
        );

        ArticleResponseDto responseDto = ArticleResponseDto.builder()
                .article(articleDto)
                .build();

        ResponseDto<ArticleResponseDto> response = new ResponseDto<>(responseDto, "Article created successfully.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get an article by slug",
            description = "Retrieves a single article based on the provided slug."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{slug}")
    public ResponseEntity<ResponseDto<ArticleResponseDto>> getArticle(
            @PathVariable String slug
    ) {

        ArticleDto articleDto = articleService.getArticleBySlug(slug);

        ArticleResponseDto responseDto = ArticleResponseDto.builder()
                .article(articleDto)
                .build();

        ResponseDto<ArticleResponseDto> response = new ResponseDto<>(responseDto, "Article retrieved successfully.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

