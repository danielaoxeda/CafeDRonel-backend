package com.cafedronel.cafedronelbackend.data.dto.error;

import java.time.OffsetDateTime;

public record ApiError(
        int status,
        String error,
        String message,
        String path,
        OffsetDateTime timestamp
) {
}
