package com.example.conduit_springboot_vaadin.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data transfer object for user information.
 * <p>
 * This DTO is used to transfer non-sensitive user data, excluding sensitive field like password,
 * between different layers of the application or in API responses.
 * </p>
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Schema(description = "Username of the user", example = "user123")
    private String username;

    @Schema(description = "Email of the user", example = "user@example.com")
    @Email(message = "Email should be valid.")
    private String email;

    @Schema(description = "A short biography about the user", example = "A passionate developer.")
    private String bio;

    @Schema(description = "URL to the user's profile image", example = "https://example.com/profile.jpg")
    private String image;

    @Schema(description = "List of user IDs that this user is following", example = "[\"userId1\", \"userId2\"]")
    private List<String> following;

}