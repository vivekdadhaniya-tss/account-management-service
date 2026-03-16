package com.tss.accountmanagementservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Handle custom ApplicationExceptions (e.g., ResourceNotFound, AccountNotFound, etc.)
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleException(ApplicationException e,
                                                         HttpServletRequest request) {

        logger.error("Application exception at {}: [{}] {}", 
                   request.getRequestURI(), e.getErrorCode(), e.getMessage(), e);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(e.getStatus().value());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setError(e.getErrorCode());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(Instant.now());

        return ResponseEntity
                .status(e.getStatus())
                .body(errorResponse);
    }

    // 2. Handle Validation Exceptions (triggered by @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        logger.warn("Validation failed at {}: {}", request.getRequestURI(), message);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(400);
        errorResponse.setMessage(message);
        errorResponse.setError("VALIDATION_FAILED");
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(Instant.now());

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    // 3. Handle all other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {

        logger.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setMessage("An unexpected error occurred. Please contact support.");
        errorResponse.setError("INTERNAL_SERVER_ERROR");
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(Instant.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
