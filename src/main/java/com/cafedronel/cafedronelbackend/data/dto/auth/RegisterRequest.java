package com.cafedronel.cafedronelbackend.data.dto.auth;

import com.cafedronel.cafedronelbackend.data.model.Rol;

public record RegisterRequest (
    String nombre,
    String correo,
    String contrasena,
    String telefono,
    String direccion,
    Rol rol
) {}
