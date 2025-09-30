package com.cafedronel.cafedronelbackend.data.dto;

public record MessageResponse<T>(
        T message
) {
}
