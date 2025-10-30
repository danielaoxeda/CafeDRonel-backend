package com.cafedronel.cafedronelbackend.data.dto.pedido;

import com.cafedronel.cafedronelbackend.data.enums.EstadoPedido;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarEstadoPedidoDTO {
    
    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado;
}