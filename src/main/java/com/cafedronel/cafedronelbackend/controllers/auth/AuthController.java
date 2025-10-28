package com.cafedronel.cafedronelbackend.controllers.auth;

import com.cafedronel.cafedronelbackend.data.dto.MessageResponse;
import com.cafedronel.cafedronelbackend.data.dto.auth.AuthResponse;
import com.cafedronel.cafedronelbackend.data.dto.auth.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.VerifyRequest;
import com.cafedronel.cafedronelbackend.exceptions.BusinessException;
import com.cafedronel.cafedronelbackend.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse<Boolean>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        System.out.println(registerRequest);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(authService.register(registerRequest)));
    }

    @PostMapping("/verify")
    public ResponseEntity<MessageResponse<Boolean>> verify(@Valid @RequestBody VerifyRequest verifyRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(authService.verify(verifyRequest)));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<MessageResponse<String>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse<>(ex.getMessage()));
    }
}
