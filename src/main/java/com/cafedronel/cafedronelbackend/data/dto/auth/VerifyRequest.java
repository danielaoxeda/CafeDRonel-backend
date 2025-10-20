package com.cafedronel.cafedronelbackend.data.dto.auth;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public record VerifyRequest(String token) {

    public VerifyRequest {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(token), "El token es obligatorio");

        // Normalización opcional: eliminar espacios laterales
        token = token.trim();
    }
}
