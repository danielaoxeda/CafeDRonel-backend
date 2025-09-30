package com.cafedronel.cafedronelbackend.data.dto.password;

public record ResetPasswordRequest(
        String email,
        String recoveryCode,
        String newPassword
) {
}
