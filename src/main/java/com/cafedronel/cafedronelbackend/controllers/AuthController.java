package com.cafedronel.cafedronelbackend.controllers;

import com.cafedronel.cafedronelbackend.data.dto.AuthResponse;
import com.cafedronel.cafedronelbackend.data.dto.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.VerifyRequest;
import com.cafedronel.cafedronelbackend.services.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.register(registerRequest));
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestBody VerifyRequest verifyRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.verify(verifyRequest));
    }
}
