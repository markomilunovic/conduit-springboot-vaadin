package com.example.conduit_springboot_vaadin.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a user attempts to update an article that they do not own.
 * <p>
 * This exception triggers an HTTP 403 Forbidden response, indicating
 * that the user does not have permission to update this article.
 * </p>
 */
public class AccessDeniedException extends ResponseStatusException {

    public AccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}

