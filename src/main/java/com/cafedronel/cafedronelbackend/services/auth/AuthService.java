package com.cafedronel.cafedronelbackend.services.auth;

import com.cafedronel.cafedronelbackend.data.dto.auth.AuthResponse;
import com.cafedronel.cafedronelbackend.data.dto.auth.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.auth.VerifyRequest;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);
    Boolean register(RegisterRequest registerRequest);
    Boolean verify(VerifyRequest verifyRequest);
}
