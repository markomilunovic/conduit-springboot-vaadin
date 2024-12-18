package com.example.conduit_springboot_vaadin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for representing the response of an authenticated user.
 * This DTO is used to encapsulate the essential information sent to the client
 * after a successful login or authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    @Schema(description = "The access token issued to the authenticated user for authorization.")
    @NotEmpty(message = "Access Token field must not be empty")
    private String accessToken;

    @Schema(description = "The refresh token issued to the authenticated user for token renewal.")
    @NotEmpty(message = "Refresh Token field must not be empty")
    private String refreshToken;

    @Schema(description = "Email of the authenticated user", example = "user@example.com")
    @NotBlank(message = "Email field must not be empty.")
    @Email(message = "Email should be valid.")
    private String email;

    @Schema(description = "A short biography about the user", example = "A passionate developer.")
    private String bio;

    @Schema(description = "URL to the user's profile image", example = "https://example.com/profile.jpg")
    private String image;

}

