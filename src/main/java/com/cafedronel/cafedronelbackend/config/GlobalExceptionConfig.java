package com.cafedronel.cafedronelbackend.config;

import com.cafedronel.cafedronelbackend.data.dto.error.ApiError;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@Order
@RestControllerAdvice
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        // Evita filtrar detalles sensibles
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno", req.getRequestURI());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI());
    }
}
