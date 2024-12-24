package com.example.conduit_springboot_vaadin.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper DTO for comment responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    @Schema(description = "Comment data in response")
    private CommentDto comment;

}