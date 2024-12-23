package com.example.conduit_springboot_vaadin.dto.comment;

import com.example.conduit_springboot_vaadin.dto.article.AuthorDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for the response after adding a comment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @Schema(description = "The comment ID", example = "1")
    private String id;

    @Schema(description = "The comment body", example = "His name was my name too.")
    private String body;

    @Schema(description = "The timestamp when the comment was created", example = "2024-12-20T15:30:00Z")
    private Instant createdAt;

    @Schema(description = "The timestamp when the comment was last updated", example = "2024-12-20T15:30:00Z")
    private Instant updatedAt;

    @Schema(description = "Author's detailed information")
    private AuthorDto author;

}
