package com.example.conduit_springboot_vaadin.backend.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard structure for API responses.
 * <p>
 *     This generic response wrapper is used to encapsulate API responses in a consistent format,
 *     including the success status, response data, and a descriptive message.
 *     It is intended to provide a clear and predictable structure for all API responses.
 * </p>
 *
 * @param <T> The type of the response data contained within this structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Standard response structure for API requests")
public class ResponseDto<T> {

    @Schema(description = "Indicates if the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Contains the main data of the response")
    private T data;

    @Schema(description = "Message providing additional information about the request", example = "User registered successfully")
    private String message;

    public ResponseDto(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
    }

}

