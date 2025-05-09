package com.example.conduit_springboot_vaadin.backend.controller;

import com.example.conduit_springboot_vaadin.backend.common.util.ErrorResponse;
import com.example.conduit_springboot_vaadin.backend.common.util.ValidationErrorResponse;
import com.example.conduit_springboot_vaadin.backend.dto.article.*;
import com.example.conduit_springboot_vaadin.backend.dto.comment.*;
import com.example.conduit_springboot_vaadin.backend.dto.user.ResponseDto;
import com.example.conduit_springboot_vaadin.backend.security.CustomUserDetails;
import com.example.conduit_springboot_vaadin.backend.service.ArticleService;
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

import java.util.List;
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

    @Operation(
            summary = "Get article feed",
            description = "Retrieves articles from followed users, ordered by most recent first. Authentication required."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Feed retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/feed")
    public ResponseEntity<ResponseDto<Map<String, Object>>> getFeed(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("Request received for retrieving feed articles with limit={}, offset={}", limit, offset);

        String currentUserId = userDetails.getId();

        ArticleListResponseDto feedDto = articleService.getFeed(currentUserId, limit, offset);

        Map<String, Object> responseData = Map.of(
                "articles", feedDto.getArticles(),
                "articlesCount", feedDto.getArticlesCount()
        );

        ResponseDto<Map<String, Object>> response = new ResponseDto<>(
                responseData,
                "Feed retrieved successfully."
        );

        log.info("Returning feed with {} articles and articlesCount={}",
                feedDto.getArticles().size(), feedDto.getArticlesCount());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update an existing article",
            description = "Updates article fields (title, description, body)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Article updated successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user not author of this article",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed due to invalid input parameters",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            )
    })
    @PutMapping("/{slug}")
    public ResponseEntity<ResponseDto<ArticleResponseDto>> updateArticle(
            @PathVariable String slug,
            @Valid @RequestBody UpdateArticleRequestDto updateArticleRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("Request received to update article with slug: {}", slug);

        UpdateArticleDto updateArticleDto = updateArticleRequestDto.getArticle();
        String currentUserId = userDetails.getId();
        String currentUsername = userDetails.getUsername();

        ArticleDto updatedArticleDto = articleService.updateArticle(
                slug,
                updateArticleDto,
                currentUserId,
                currentUsername
        );

        ArticleResponseDto responseDto = ArticleResponseDto.builder()
                .article(updatedArticleDto)
                .build();

        log.debug("Article updated: {}", updatedArticleDto);

        ResponseDto<ArticleResponseDto> response = new ResponseDto<>(
                responseDto,
                "Article updated successfully."
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete an article",
            description = "Deletes the article identified by the slug."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Article deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - user not author of the article",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{slug}")
    public ResponseEntity<ResponseDto<Void>> deleteArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("Request received to delete article with slug: '{}'", slug);
        String currentUsername = userDetails.getUsername();

        articleService.deleteArticle(slug, currentUsername);

        ResponseDto<Void> response = new ResponseDto<>(null, "Article deleted successfully.");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Add a new comment",
            description = "Adds a new comment with the provided body."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment added successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(
                    responseCode = "422",
                    description = "Validation failed due to invalid input parameters",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{slug}/comments")
    public ResponseEntity<ResponseDto<CommentResponseDto>> addCommentToArticle(
            @PathVariable String slug,
            @Valid @RequestBody AddCommentRequestDto addCommentRequestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        log.info("Request received to add comment to article with slug: {}", slug);

        String currentUsername = userDetails.getUsername();
        String currentUserId = userDetails.getId();
        AddCommentDto addCommentDto = addCommentRequestDto.getComment();

        CommentDto commentDto = articleService.addCommentToArticle(slug, addCommentDto, currentUsername, currentUserId);

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .comment(commentDto)
                .build();

        log.info("Comment created successfully: {}", commentDto.getId());
        log.debug("Comment created with data: {}", commentDto);

        ResponseDto<CommentResponseDto> response = new ResponseDto<>(commentResponseDto, "Comment added successfully.");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "List comments",
            description = "Lists comments for a specific article."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Comments retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{slug}/comments")
    public ResponseEntity<ResponseDto<ListCommentDto>> getCommentsForArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        log.info("Request received to fetch comments for article: {}", slug);

        String currentUserId = (userDetails != null) ? userDetails.getId() : null;

        List<CommentDto> commentDtos = articleService.getCommentsFromArticle(slug, currentUserId);

        ListCommentDto listCommentDto = ListCommentDto.builder()
                .comments(commentDtos)
                .build();

        ResponseDto<ListCommentDto> response = new ResponseDto<>(listCommentDto, "Comments retrieved successfully.");

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Delete a comment",
            description = "Deletes a specific comment from an article. Only the comment's author can perform this action."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment deleted successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user is not the author of the comment",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Comment or article not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{slug}/comments/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteComment(
            @PathVariable String slug,
            @PathVariable("id") String commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("Request received to delete comment with ID: {} from article: {}", commentId, slug);

        String currentUsername = userDetails.getUsername();

        articleService.deleteComment(slug, commentId, currentUsername);

        ResponseDto<Void> response = new ResponseDto<>(null, "Comment deleted successfully.");

        log.info("Comment with ID: {} deleted successfully from article: {}", commentId, slug);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Favorite an article",
            description = "Allows an authenticated user to favorite a specific article."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Article favorited successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Article already favorited",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{slug}/favorite")
    public ResponseEntity<ResponseDto<ArticleResponseDto>> favoriteArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("Request received to favorite article with slug: {}", slug);

        String currentUserId = userDetails.getId();

        ArticleDto favoritedArticleDto = articleService.favoriteArticle(slug, currentUserId);

        ArticleResponseDto responseDto = ArticleResponseDto.builder()
                .article(favoritedArticleDto)
                .build();

        ResponseDto<ArticleResponseDto> response = new ResponseDto<>(responseDto, "Article favorited successfully.");

        log.info("Article with slug '{}' favorited successfully by user ID: {}", slug, currentUserId);
        log.debug("Favorited ArticleDto: {}", favoritedArticleDto);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Unfavorite an article",
            description = "Allows an authenticated user to unfavorite a specific article."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Article unfavorited successfully",
                    content = @Content(schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Article not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Article already unfavorited",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{slug}/favorite")
    public ResponseEntity<ResponseDto<ArticleResponseDto>> unfavoriteArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("Request received to unfavorite article with slug: {}", slug);

        String currentUserId = userDetails.getId();

        ArticleDto unfavoritedArticleDto = articleService.unfavoriteArticle(slug, currentUserId);

        ArticleResponseDto responseDto = ArticleResponseDto.builder()
                .article(unfavoritedArticleDto)
                .build();

        ResponseDto<ArticleResponseDto> response = new ResponseDto<>(responseDto, "Article unfavorited successfully.");

        log.info("Article with slug '{}' unfavorited successfully by user ID: {}", slug, currentUserId);
        log.debug("Unfavorited ArticleDto: {}", unfavoritedArticleDto);

        return ResponseEntity.ok(response);
    }


}

