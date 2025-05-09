package com.example.conduit_springboot_vaadin.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for handling user login requests.
 * This DTO encapsulates the necessary information required for a user to log in,
 * specifically the user's email and password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @Schema(description = "Email of the user", example = "user@example.com")
    @NotEmpty(message = "Email field must not be empty")
    private String email;

    @Schema(description = "Password for the user account", example = "password123")
    @NotEmpty(message = "Password field must not be empty")
    private String password;

}

