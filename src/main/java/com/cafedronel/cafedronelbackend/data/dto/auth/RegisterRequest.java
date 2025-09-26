package com.cafedronel.cafedronelbackend.data.dto.auth;


public record RegisterRequest (
    String nombre,
    String correo,
    String contrasena,
    String telefono,
    String direccion
) {}
