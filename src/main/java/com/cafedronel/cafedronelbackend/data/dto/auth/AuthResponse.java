package com.cafedronel.cafedronelbackend.data.dto.auth;


public record AuthResponse(
        String token,
        String email,
        String rol,
        Integer id
) {
}
