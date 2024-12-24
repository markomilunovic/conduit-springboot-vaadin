package com.example.conduit_springboot_vaadin.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a comment to an article.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {

    @Schema(description = "The comment body", example = "His name was my name too.")
    @NotBlank(message = "Comment body cannot be blank.")
    private String body;
}
