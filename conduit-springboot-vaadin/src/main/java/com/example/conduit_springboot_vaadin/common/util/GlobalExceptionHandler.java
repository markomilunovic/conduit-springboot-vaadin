package com.example.conduit_springboot_vaadin.common.util;


import com.example.conduit_springboot_vaadin.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for managing application-wide exceptions.
 * <p>
 * This class provides centralized handling of exceptions by defining specific
 * methods for common error cases, ensuring responses adhere to the required format.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles any unhandled exceptions that occur within the application.
     * <p>
     * This catch-all handler returns an error response with an HTTP 500
     * status code when an unhandled exception is thrown. The response includes
     * an error message and the details of the original request.
     * </p>
     *
     * @param ex      The exception that was not specifically handled.
     * @param request The current web request during which the exception occurred.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with an
     *         error message and a status of {@link HttpStatus#INTERNAL_SERVER_ERROR}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation errors and returns a 422 Unprocessable Entity response.
     *
     * @param ex The MethodArgumentNotValidException containing validation errors.
     * @return A ResponseEntity with the standardized validation error response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(
                Map.of("body", errorMessages)
        );

        log.warn("Validation failed: {}", errorMessages);

        return new ResponseEntity<>(validationErrorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles UserAlreadyExistsException for both email and username duplication.
     *
     * @param ex The UserAlreadyExistsException.
     * @return A ResponseEntity with the standardized error response.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        log.warn("UserAlreadyExistsException: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                "Duplicate user entry."
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

}

