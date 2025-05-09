package com.example.conduit_springboot_vaadin.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


/**
 * Exception thrown when attempting to register a user with an email or username that already exists in the system.
 * <p>
 * This exception triggers an HTTP 409 Conflict response, indicating that the email address provided
 * is already in use by another user.
 * </p>
 */
public class UserAlreadyExistsException extends ResponseStatusException {


    public UserAlreadyExistsException(String field, String value) {
        super(HttpStatus.CONFLICT, "User with " + field + " '" + value + "' already exists.");
    }
}

