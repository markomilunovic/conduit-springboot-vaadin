package com.example.conduit_springboot_vaadin.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for handling User registration request.
 * This DTO encapsulates the necessary information required for a user to register.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDto {

    @Schema(description = "Username of the user", example = "user123")
    @NotBlank(message = "Username field must not be empty.")
    private String username;

    @Schema(description = "Email of the user", example = "user@example.com")
    @NotBlank(message = "Email field must not be empty.")
    @Email(message = "Email should be valid.")
    private String email;

    @Schema(description = "Password for the user account", example = "password123")
    @NotEmpty(message = "Password field must not be empty.")
    private String password;

    @Schema(description = "A short biography about the user", example = "A passionate developer.")
    private String bio;

    @Schema(description = "URL to the user's profile image", example = "https://example.com/profile.jpg")
    private String image;

    @Schema(description = "List of user IDs that this user is following", example = "[\"userId1\", \"userId2\"]")
    private List<String> following;
}
