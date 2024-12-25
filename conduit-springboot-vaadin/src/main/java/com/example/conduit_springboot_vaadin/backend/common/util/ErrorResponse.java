package com.example.conduit_springboot_vaadin.backend.common.util;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a standard structure for error responses in the application.
 * <p>
 * This class provides information about an error, including a message, specific details,
 * and a timestamp indicating when the error occurred. It is used to standardize error
 * responses sent to clients.
 * </p>
 */
@Data
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private String details;
    private LocalDateTime timestamp;


    /**
     * Constructs an ErrorResponse with the given message and details, setting the timestamp to the current time.
     *
     * @param message A brief message describing the error.
     * @param details Specific details about the error.
     */
    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

}
