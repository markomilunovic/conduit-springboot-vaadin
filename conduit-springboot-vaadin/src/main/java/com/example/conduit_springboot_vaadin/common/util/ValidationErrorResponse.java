package com.example.conduit_springboot_vaadin.common.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents a standardized validation error response structure.
 * <p>
 * This class encapsulates validation error messages under an "errors" key,
 * specifically within a "body" field.
 * </p>
 */
@Data
@NoArgsConstructor
public class ValidationErrorResponse {

    private Map<String, List<String>> errors;

    /**
     * Constructs a ValidationErrorResponse with the given field errors.
     *
     * @param errors A map where the key is the error context (e.g., "body") and the value is a list of error messages.
     */
    public ValidationErrorResponse(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}
