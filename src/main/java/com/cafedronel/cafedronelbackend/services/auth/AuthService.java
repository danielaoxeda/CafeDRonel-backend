package com.cafedronel.cafedronelbackend.services.auth;

import com.cafedronel.cafedronelbackend.data.dto.AuthResponse;
import com.cafedronel.cafedronelbackend.data.dto.LoginRequest;
import com.cafedronel.cafedronelbackend.data.dto.RegisterRequest;
import com.cafedronel.cafedronelbackend.data.dto.VerifyRequest;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);
    Boolean register(RegisterRequest registerRequest);
    Boolean verify(VerifyRequest verifyRequest);
}
