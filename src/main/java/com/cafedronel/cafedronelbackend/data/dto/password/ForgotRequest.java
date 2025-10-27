package com.cafedronel.cafedronelbackend.data.dto.password;

import jakarta.validation.constraints.Email;

public record ForgotRequest(
        @Email
        String email
) {
}
