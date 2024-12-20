package com.example.conduit_springboot_vaadin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when attempting to fetch an article that does not exist in the system.
 * <p>
 * This exception returns a 404 Not Found HTTP status, indicating that the article with the specified
 * slug is not present in the database.
 * </p>
 */
public class ArticleNotFoundException extends ResponseStatusException {
    public ArticleNotFoundException(String slug) {
        super(HttpStatus.NOT_FOUND, "Book with id " + slug + " not found.");
    }
}
