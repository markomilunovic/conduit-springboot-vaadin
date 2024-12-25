package com.example.conduit_springboot_vaadin.backend.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper DTO for adding a comment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentRequestDto {

    @Schema(description = "Comment data for creation")
    private AddCommentDto comment;

}
