package com.cafedronel.cafedronelbackend.data.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteUpdateDTO {
    
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @Email(message = "El correo debe tener un formato válido")
    private String correo;
    
    @Size(min = 8, max = 15, message = "El teléfono debe tener entre 8 y 15 caracteres")
    private String telefono;
    
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres")
    private String direccion;
}