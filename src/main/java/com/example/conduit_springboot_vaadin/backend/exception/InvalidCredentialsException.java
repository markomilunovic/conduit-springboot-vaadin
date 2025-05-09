package com.example.conduit_springboot_vaadin.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when the provided login credentials are invalid.
 * <p>
 * This exception triggers an HTTP 401 Unauthorized response, indicating
 * that the email or password provided is incorrect.
 * </p>
 */
public class InvalidCredentialsException extends ResponseStatusException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
    }
}

