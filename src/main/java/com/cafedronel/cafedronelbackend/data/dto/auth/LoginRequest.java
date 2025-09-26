package com.cafedronel.cafedronelbackend.data.dto.auth;

public record LoginRequest (
    String correo,
    String contrasena
) {}
