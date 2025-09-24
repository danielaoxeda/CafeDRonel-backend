package com.cafedronel.cafedronelbackend.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String nombre;
    private String correo;
    private String contrasena;
    private String telefono;
    private String direccion;
}
