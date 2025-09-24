package com.cafedronel.cafedronelbackend.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String email;
    private String rol;
}
