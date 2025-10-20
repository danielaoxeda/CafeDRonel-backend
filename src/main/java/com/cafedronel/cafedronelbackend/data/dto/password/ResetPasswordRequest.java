package com.cafedronel.cafedronelbackend.data.dto.password;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.regex.Pattern;

public record ResetPasswordRequest(
        String email,
        String recoveryCode,
        String newPassword
) {

    // Expresión regular simple para correos válidos
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Ejemplo de política de contraseña fuerte (mínimo 8 caracteres, al menos una mayúscula, una minúscula, un número)
    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    public ResetPasswordRequest {
        // --- email ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(email), "El correo es obligatorio");
        Preconditions.checkArgument(EMAIL_PATTERN.matcher(email.trim()).matches(),
                "El correo debe tener un formato válido");

        // --- recoveryCode ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(recoveryCode),
                "El código de recuperación es obligatorio");

        // --- newPassword ---
        Preconditions.checkArgument(!Strings.isNullOrEmpty(newPassword),
                "La nueva contraseña es obligatoria");
        Preconditions.checkArgument(STRONG_PASSWORD_PATTERN.matcher(newPassword).matches(),
                "La nueva contraseña debe tener al menos 8 caracteres, incluir una mayúscula, una minúscula y un número");

        // --- Normalización ---
        email = email.trim().toLowerCase();
        recoveryCode = recoveryCode.trim();
    }
}
