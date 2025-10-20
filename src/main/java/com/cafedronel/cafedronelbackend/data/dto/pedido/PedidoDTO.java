package com.cafedronel.cafedronelbackend.data.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PedidoDTO {
    private Integer idPedido;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer idUsuario;
    
    private Date fecha;
    private String estado;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    private List<DetallePedidoDTO> detalles;
}