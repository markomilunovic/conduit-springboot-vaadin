package com.example.conduit_springboot_vaadin.controller;

import com.example.conduit_springboot_vaadin.common.util.ErrorResponse;
import com.example.conduit_springboot_vaadin.common.util.ValidationErrorResponse;
import com.example.conduit_springboot_vaadin.dto.article.*;
import com.example.conduit_springboot_vaadin.dto.user.ResponseDto;
import com.example.conduit_springboot_vaadin.security.CustomUserDetails;
import com.example.conduit_springboot_vaadin.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.conduit_springboot_vaadin.dto.article.ArticleListResponseDto;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

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

        log.info("Request received to create article with : {}", createArticleDto.getTitle());
        log.debug("Received request to create article with data: {}", createArticleDto);

        ArticleDto articleDto = articleService.createArticle(
                createArticleDto,
                userDetails.getUsername(),
                userDetails.getId()
        );

        ArticleResponseDto responseDto = ArticleResponseDto.builder()
                .article(articleDto)
                .build();

        log.info("Article created successfully: {}", articleDto.getTitle());
        log.debug("ArticleDto created with data: {}", articleDto);

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

        log.info("Request received to retrieve article with slug: {}", slug);

        ArticleDto articleDto = articleService.getArticleBySlug(slug);

        ArticleResponseDto responseDto = ArticleResponseDto.builder()
                .article(articleDto)
                .build();

        log.info("Article retrieved successfully: {}", articleDto.getTitle());
        log.debug("ArticleDto data: {}", articleDto);

        ResponseDto<ArticleResponseDto> response = new ResponseDto<>(responseDto, "Article retrieved successfully.");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List articles",
            description = "Lists articles globally or filtered by tag, author, or favorited, with pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseDto<Map<String, Object>>> listArticles(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String favorited,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        log.info("Request received to retrieve articles with filters: " +
                "tag: {}, author: {}, favorited: {}, limit: {}, offset: {}", tag, author, favorited, limit, offset);

        String currentUserId = userDetails != null ? userDetails.getId() : null;
        log.debug("Current user ID: {}", currentUserId);

        ArticleListResponseDto responseDto = articleService.listArticles(
                tag,
                author,
                favorited,
                limit,
                offset,
                currentUserId
        );

        Map<String, Object> responseData = Map.of(
                "articles", responseDto.getArticles(),
                "articlesCount", responseDto.getArticlesCount()
        );

        log.debug("Articles retrieved: {}, Articles count: {}",
                responseDto.getArticles().size(), responseDto.getArticlesCount());

        ResponseDto<Map<String, Object>> response = new ResponseDto<>(
                responseData,
                "Articles retrieved successfully."
        );

        log.info("Returning response with {} articles and count {}",
                responseDto.getArticles().size(), responseDto.getArticlesCount());

        return ResponseEntity.ok(response);
    }

}

