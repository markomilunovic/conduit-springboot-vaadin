package com.example.conduit_springboot_vaadin.backend.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing author information in article responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    @Schema(description = "Username of the author", example = "jake")
    private String username;

    @Schema(description = "Biography of the author", example = "I work at statefarm")
    private String bio;

    @Schema(description = "URL to the author's profile image", example = "https://i.stack.imgur.com/xHWG8.jpg")
    private String image;

    @Schema(description = "Indicates if the current user is following the author", example = "false")
    private boolean following;
}

