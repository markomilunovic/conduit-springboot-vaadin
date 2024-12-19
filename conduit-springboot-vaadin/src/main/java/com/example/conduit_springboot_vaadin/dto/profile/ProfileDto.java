package com.example.conduit_springboot_vaadin.dto.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for user profiles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    @Schema(description = "Username of the profile", example = "user123")
    private String username;

    @Schema(description = "Bio of the user", example = "A passionate developer.")
    private String bio;

    @Schema(description = "URL of the user's profile image", example = "https://example.com/profile.jpg")
    private String image;

    @Schema(description = "Indicates if the current user is following this profile", example = "false")
    private boolean following;
}

