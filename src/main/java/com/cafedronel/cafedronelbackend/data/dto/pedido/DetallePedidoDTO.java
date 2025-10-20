package com.cafedronel.cafedronelbackend.data.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetallePedidoDTO {
    private Integer idDetalle;
    
    @NotNull(message = "El ID del producto es obligatorio")
    private Integer idProducto;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    private Double precioUnitario;
}