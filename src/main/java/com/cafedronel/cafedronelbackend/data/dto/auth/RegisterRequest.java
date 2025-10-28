package com.cafedronel.cafedronelbackend.data.dto.auth;

import com.cafedronel.cafedronelbackend.data.model.Rol;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.regex.Pattern;

public record RegisterRequest(
        String nombre,
        String apellido,
        String correo,
        String contrasena,
        String telefono,
        String direccion,
        Rol rol
) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{9}$"); // exactamente 9 dígitos

    public RegisterRequest {
        // --- nombre ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nombre), "El nombre es obligatorio");
        // --- nombre ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(apellido), "El apellido es obligatorio");

        // --- correo ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(correo), "El correo es obligatorio");
        Preconditions.checkArgument(EMAIL_PATTERN.matcher(correo.trim()).matches(),
                "El correo debe tener un formato válido");

        // --- contraseña ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(contrasena), "La contraseña es obligatoria");

        // --- teléfono ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(telefono), "El teléfono es obligatorio");
        Preconditions.checkArgument(PHONE_PATTERN.matcher(telefono.trim()).matches(),
                "El teléfono debe tener 9 dígitos numéricos");

        // --- dirección ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(direccion), "La dirección es obligatoria");

        // --- rol ---
        Preconditions.checkNotNull(rol, "El rol es obligatorio");

        // --- normalización opcional ---
        correo = correo.trim().toLowerCase();
        nombre = nombre.trim();
        apellido = apellido.trim();
        direccion = direccion.trim();
        telefono = telefono.trim();
    }
}
