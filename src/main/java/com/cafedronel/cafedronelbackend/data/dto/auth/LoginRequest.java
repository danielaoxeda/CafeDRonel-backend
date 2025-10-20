package com.cafedronel.cafedronelbackend.data.dto.auth;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.regex.Pattern;

public record LoginRequest(String correo, String contrasena) {

    // Expresión regular básica para emails
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    
    public LoginRequest {
        // Validar correo
        Preconditions.checkArgument(!Strings.isNullOrEmpty(correo), "El correo es obligatorio");
        Preconditions.checkArgument(EMAIL_PATTERN.matcher(correo.trim()).matches(),
                "El correo debe tener un formato válido");

        // Validar contraseña
        Preconditions.checkArgument(!Strings.isNullOrEmpty(contrasena), "La contraseña es obligatoria");

        // Normalización (opcional)
        correo = correo.trim().toLowerCase();
        contrasena = contrasena.trim();
    }
}

