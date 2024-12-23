package com.example.conduit_springboot_vaadin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when attempting to fetch a comment that does not exist in the system.
 * <p>
 * This exception returns a 404 Not Found HTTP status, indicating that the comment with the specified
 * ID is not present in the database.
 * </p>
 */
public class CommentNotFoundException extends ResponseStatusException {
    public CommentNotFoundException(String commentId, String slug) {
        super(HttpStatus.NOT_FOUND, "Comment with ID " + commentId + " not found for article " + slug);
    }
}
