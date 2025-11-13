package com.cafedronel.cafedronelbackend.config;

import java.time.OffsetDateTime;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cafedronel.cafedronelbackend.data.dto.error.ApiError;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;

import jakarta.servlet.http.HttpServletRequest;

@Order(1)
@RestControllerAdvice(basePackages = "com.cafedronel.cafedronelbackend.controllers")
public class GlobalExceptionConfig {

    private ResponseEntity<ApiError> build(HttpStatus status, String message, String path) {
        ApiError body = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                OffsetDateTime.now()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciales inválidas", req.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .orElse("Datos inválidos");
        return build(HttpStatus.BAD_REQUEST, message, req.getRequestURI());
    }

    // Guava + validaciones personalizadas
    @ExceptionHandler({
            IllegalArgumentException.class,
            NullPointerException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ApiError> handleGuavaValidation(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonParseError(HttpMessageNotReadableException ex, HttpServletRequest req) {
        Throwable cause = ex.getCause();

        // Si la causa es IllegalArgumentException (por Guava Preconditions)
        if (cause instanceof IllegalArgumentException) {
            return build(HttpStatus.BAD_REQUEST, cause.getMessage(), req.getRequestURI());
        }

        // Si no, se devuelve un mensaje genérico
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        ex.printStackTrace(); // o logger.error("Unexpected error", ex);
        
        // Proporcionar más detalles para debugging en desarrollo
        String message = "Ha ocurrido un error interno";
        if (ex.getMessage() != null && !ex.getMessage().isEmpty()) {
            message += ": " + ex.getMessage();
        }
        
        return build(HttpStatus.INTERNAL_SERVER_ERROR, message, req.getRequestURI());
    }
}
