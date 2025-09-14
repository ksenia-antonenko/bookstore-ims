package org.example.bookstore.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookstore.dto.ApiError;
import org.example.bookstore.exception.BookstoreEntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();

        ApiError body = new ApiError(
            ZonedDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            "Invalid request body",
            request.getRequestURI(),
            details
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
        AccessDeniedException ex, HttpServletRequest request) {
        ApiError body = new ApiError(
            ZonedDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            "Access Denied",
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
        ConstraintViolationException ex,
        HttpServletRequest request) {
        List<String> details = ex.getConstraintViolations()
            .stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .toList();

        ApiError body = new ApiError(
            ZonedDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            ex.getMessage(),
            request.getRequestURI(),
            details
        );
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BookstoreEntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
        BookstoreEntityNotFoundException ex,
        HttpServletRequest request
    ) {
        ApiError body = new ApiError(
            ZonedDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonParse(
        HttpMessageNotReadableException ex,
        HttpServletRequest request) {

        Throwable root = ex.getMostSpecificCause();
        String msg = root.getMessage();

        ApiError body = new ApiError(
            ZonedDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Malformed JSON request",
            msg,
            request.getRequestURI(),
            List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
        Exception ex,
        HttpServletRequest request) {
        ApiError body = new ApiError(
            ZonedDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Unexpected error",
            ex.getMessage(),
            request.getRequestURI(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
